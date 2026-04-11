// File: utilities.server.StartAppiumWithPlugin
package utilities.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static utils.seleniumUtils.SeleniumUtils.sleep;

/**
 * Appium starter for parallel execution.
 *
 * Design: one Appium process per device (keyed by UDID or any unique device key),
 * each bound to its own port. This avoids cross-talk and makes parallel runs deterministic.
 */
public class StartAppiumWithPlugin {

    private static final Logger logger = LogManager.getLogger(StartAppiumWithPlugin.class);

    // ---- Adjust these if needed ----
    private static final String HOST = "127.0.0.1"; // Force IPv4
    private static final int DEFAULT_PREFERRED_PORT = 4723;
    private static final String BASE_PATH = "/wd/hub"; // legacy compatibility; readiness check tries both /wd/hub and /
    // --------------------------------

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase().contains("win");

    private static final int LOG_TAIL_SIZE = 250;

    private static final Map<String, ServerHandle> SERVERS = new ConcurrentHashMap<>();
    private static final Map<String, Object> KEY_LOCKS = new ConcurrentHashMap<>();
    private static volatile boolean shutdownHookAdded;
    private static volatile boolean prismaEnsured;

    private static final class ServerHandle {
        final String deviceKey;
        final int port;
        final URL baseUrl;
        final Process process; // null if we reused an externally started server
        final Deque<String> logTail;
        final Path logFile;    // may be null for external reuse

        private ServerHandle(String deviceKey, int port, URL baseUrl, Process process, Deque<String> logTail, Path logFile) {
            this.deviceKey = deviceKey;
            this.port = port;
            this.baseUrl = baseUrl;
            this.process = process;
            this.logTail = logTail;
            this.logFile = logFile;
        }

        boolean isManagedByUs() {
            return process != null;
        }
    }

    /**
     * Backwards-compatible: starts a single default server (NOT suitable for parallel devices).
     */
    public static synchronized void startAppiumDeviceFarmServer() {
        startAppiumDeviceFarmServer("default", DEFAULT_PREFERRED_PORT);
    }

    /**
     * Parallel-safe start: one server per deviceKey, each on its own port.
     *
     * @param deviceKey     unique identifier (usually UDID)
     * @param requestedPort preferred port for this device; if null, a free port is selected
     * @return base URL e.g. http://127.0.0.1:4723/wd/hub
     */
    public static URL startAppiumDeviceFarmServer(String deviceKey, Integer requestedPort) {
        String key = normalizeDeviceKey(deviceKey);
        Object lock = KEY_LOCKS.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            ServerHandle existing = SERVERS.get(key);
            if (existing != null) {
                if (existing.isManagedByUs() && existing.process.isAlive() && isServerRunningAndReady(existing.port)) {
                    logger.info("[{}] Appium already running and ready at {}", key, existing.baseUrl);
                    return existing.baseUrl;
                }
                if (!existing.isManagedByUs() && isServerRunningAndReady(existing.port)) {
                    logger.info("[{}] External Appium already ready at {}. Reusing.", key, existing.baseUrl);
                    return existing.baseUrl;
                }
                // stale handle
                SERVERS.remove(key);
            }

            // Global one-time init should remain process-wide (thread-safe)
            synchronized (StartAppiumWithPlugin.class) {
                ensurePrismaGeneratedOnce();
                addShutdownHookOnce();
            }

            int chosenPort = choosePort(key, requestedPort);
            URL baseUrl = buildBaseUrl(chosenPort);

            // If already ready (external), just reuse and store as external.
            if (isServerRunningAndReady(chosenPort)) {
                logger.info("[{}] Appium already ready at {}. Reusing.", key, baseUrl);
                ServerHandle external = new ServerHandle(key, chosenPort, baseUrl, null, new ArrayDeque<>(LOG_TAIL_SIZE), null);
                SERVERS.put(key, external);
                return baseUrl;
            }

            // If port is open but not ready, DO NOT kill anything (can race with other parallel starts).
            // Instead, pick a new free port. This is safer and more scalable.
            if (isPortOpen(chosenPort)) {
                int free = PortAllocator.allocate("appiumPortFallback:" + key);
                logger.warn("[{}] Port {} is in use (not-ready). Falling back to free port {}.",
                        key, chosenPort, free);
                chosenPort = free;
                baseUrl = buildBaseUrl(chosenPort);
            }

            Deque<String> tail = new ArrayDeque<>(LOG_TAIL_SIZE);
            StartProcessResult spr = startAppiumProcess(key, chosenPort, tail);

            waitForServerReadyOrThrow(key, chosenPort, spr.process, tail, spr.logFile);

            ServerHandle handle = new ServerHandle(key, chosenPort, baseUrl, spr.process, tail, spr.logFile);
            SERVERS.put(key, handle);
            logger.info("[{}] Appium server ready at {}", key, baseUrl);
            return baseUrl;
        }
    }

    /**
     * Backwards-compatible getter: returns default server URL.
     */
    public static URL getServerUrl() {
        return getServerUrl("default");
    }

    public static URL getServerUrl(String deviceKey) {
        String key = normalizeDeviceKey(deviceKey);
        ServerHandle h = SERVERS.get(key);
        if (h == null) {
            throw new IllegalStateException("No Appium server started for deviceKey=" + key);
        }
        return h.baseUrl;
    }

    public static void stopAppiumServer(String deviceKey) {
        String key = normalizeDeviceKey(deviceKey);
        ServerHandle h = SERVERS.remove(key);
        if (h == null) return;

        if (h.isManagedByUs() && h.process.isAlive()) {
            logger.info("[{}] Stopping Appium process on port {}", key, h.port);
            h.process.destroyForcibly();
        } else {
            logger.info("[{}] Not stopping external Appium server on port {}", key, h.port);
        }

        // release reserved allocations (best-effort)
        PortAllocator.release("appiumPort:" + key);
        PortAllocator.release("appiumPortFallback:" + key);
    }

    public static synchronized void stopAllManagedServers() {
        for (String key : SERVERS.keySet()) {
            stopAppiumServer(key);
        }
    }

    // ----------------- Internal helpers -----------------

    private static String normalizeDeviceKey(String deviceKey) {
        String key = (deviceKey == null || deviceKey.isBlank()) ? "default" : deviceKey.trim();
        // Avoid filesystem/log weirdness; keep key stable
        return key.replaceAll("\\s+", "_");
    }

    private static int choosePort(String key, Integer requestedPort) {
        if (requestedPort != null && requestedPort > 0) {
            // Try to reserve to prevent JVM-local collisions.
            // If it's already reserved or currently in use, fall back to an allocated free port.
            boolean reserved = PortAllocator.reserve("appiumPort:" + key, requestedPort);
            if (reserved) return requestedPort;

            int fallback = PortAllocator.allocate("appiumPort:" + key);
            logger.warn("[{}] Requested port {} cannot be reserved/used. Using allocated port {} instead.",
                    key, requestedPort, fallback);
            return fallback;
        }

        // If key=default, attempt the traditional port first.
        if (Objects.equals(key, "default") && !isPortOpen(DEFAULT_PREFERRED_PORT)) {
            return DEFAULT_PREFERRED_PORT;
        }

        return PortAllocator.allocate("appiumPort:" + key);
    }

    private static URL buildBaseUrl(int port) {
        try {
            return new URL("http://" + HOST + ":" + port + BASE_PATH);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addShutdownHookOnce() {
        if (shutdownHookAdded) return;
        shutdownHookAdded = true;
        Runtime.getRuntime().addShutdownHook(new Thread(StartAppiumWithPlugin::stopAllManagedServers));
    }

    private static void ensurePrismaGeneratedOnce() {
        if (prismaEnsured) return;
        prismaEnsured = true;
        ensurePrismaGenerated();
    }

    // --------------- Appium process launching (updated) ---------------

    private static final class StartProcessResult {
        final Process process;
        final Path logFile;

        StartProcessResult(Process process, Path logFile) {
            this.process = process;
            this.logFile = logFile;
        }
    }

    private static String resolveAppiumBinary() {
        logger.info("===== Resolving Appium binary =====");
        logger.info("OS               : {}", System.getProperty("os.name"));
        logger.info("User name        : {}", System.getProperty("user.name"));
        logger.info("User home        : {}", System.getProperty("user.home"));
        logger.info("Working dir      : {}", System.getProperty("user.dir"));
        logger.info("APPDATA          : {}", System.getenv("APPDATA"));
        logger.info("LOCALAPPDATA     : {}", System.getenv("LOCALAPPDATA"));
        logger.info("PATH             : {}", System.getenv("PATH"));

        if (!IS_WINDOWS) {
            return "appium";
        }

        String explicit = System.getenv("APPIUM_BINARY");
        if (explicit != null && !explicit.isBlank()) {
            logger.info("Using APPIUM_BINARY from env: {}", explicit);
            return explicit;
        }

        String whereAppium = runAndReadFirstLine(new String[]{"cmd", "/c", "where appium"});
        logger.info("where appium => {}", whereAppium);

        if (whereAppium != null && !whereAppium.isBlank()) {
            return whereAppium.trim();
        }

        String whereAppiumCmd = runAndReadFirstLine(new String[]{"cmd", "/c", "where appium.cmd"});
        logger.info("where appium.cmd => {}", whereAppiumCmd);

        if (whereAppiumCmd != null && !whereAppiumCmd.isBlank()) {
            return whereAppiumCmd.trim();
        }

        logger.warn("Falling back to appium.cmd from PATH");
        return "appium.cmd";
    }

    private static String runAndReadFirstLine(String[] command) {
        try {
            logger.info("Executing probe command: {}", String.join(" ", command));
            Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line = br.readLine();
                int exit = p.waitFor();
                logger.info("Probe command exit={} firstLine={}", exit, line);
                return line;
            }
        } catch (Exception e) {
            logger.warn("Probe command failed: {} => {}", String.join(" ", command), e.getMessage(), e);
            return null;
        }
    }

    private static StartProcessResult startAppiumProcess(String key, int port, Deque<String> tail) {
        try {
            String appiumBin = resolveAppiumBinary();

            ProcessBuilder pb = new ProcessBuilder(
                    appiumBin,
                    "--address", HOST,
                    "--port", String.valueOf(port),
                    "--base-path", BASE_PATH,
                    "--use-plugins=device-farm",
                    "--plugin-device-farm-platform=android",
                    "--keep-alive-timeout", "800",
                    "--log-level", "debug",
                    "--log-timestamp",
                    "--debug-log-spacing"
            );

            // Merge stderr into stdout so we only need one reader
            pb.redirectErrorStream(true);

            logger.info("[{}] ===== Appium startup diagnostics =====", key);
            logger.info("[{}] Resolved appium binary : {}", key, appiumBin);
            logger.info("[{}] Full command           : {}", key, String.join(" ", pb.command()));
            logger.info("[{}] PATH                   : {}", key, pb.environment().get("PATH"));
            logger.info("[{}] APPDATA                : {}", key, pb.environment().get("APPDATA"));
            logger.info("[{}] LOCALAPPDATA           : {}", key, pb.environment().get("LOCALAPPDATA"));
            logger.info("[{}] USERPROFILE            : {}", key, pb.environment().get("USERPROFILE"));

            Process p = pb.start();

            logger.info("[{}] Appium process started. pid={}", key, getPidSafe(p));

            // Stream logs live to console/logger
            startMergedLogStreaming(p, key, port, tail);

            return new StartProcessResult(p, null);

        } catch (IOException e) {
            throw new RuntimeException("Failed to start Appium process for " + key + " on port " + port, e);
        }
    }

    private static long getPidSafe(Process p) {
        try {
            return p.pid();
        } catch (Throwable t) {
            return -1;
        }
    }
    private static void waitForServerReadyOrThrow(String key, int port, Process p, Deque<String> tail, Path logFile) {
        logger.info("[{}] Waiting for Appium startup on port {}", key, port);

        sleep(4000);

        if (!p.isAlive()) {
            int exitCode = safeExitValue(p);
            dumpAppiumLogTail(key, tail);
            throw new RuntimeException("[" + key + "] Appium process exited early (exit " + exitCode + ").");
        }

        if (!waitForPortOpen(port, Duration.ofSeconds(20))) {
            dumpAppiumLogTail(key, tail);
            throw new RuntimeException("[" + key + "] Port not open after 20s on " + port);
        }

        if (!waitForAppiumReady(buildBaseUrl(port), Duration.ofSeconds(90))) {
            dumpAppiumLogTail(key, tail);
            throw new RuntimeException("[" + key + "] Appium not ready after 90s on " + port);
        }
    }

    private static int safeExitValue(Process p) {
        try {
            return p.exitValue();
        } catch (IllegalThreadStateException e) {
            return -1;
        }
    }

    private static boolean isServerRunningAndReady(int port) {
        return isPortOpen(port) && isReadyAt(buildBaseUrl(port));
    }

    private static boolean waitForPortOpen(int port, Duration timeout) {
        long start = System.currentTimeMillis();
        while (Duration.ofMillis(System.currentTimeMillis() - start).compareTo(timeout) < 0) {
            if (isPortOpen(port)) return true;
            sleep(1000);
        }
        return false;
    }

    private static boolean waitForAppiumReady(URL serverUrl, Duration timeout) {
        long start = System.currentTimeMillis();
        logger.info("Waiting for Appium to become READY at {} ...", serverUrl);

        while (Duration.ofMillis(System.currentTimeMillis() - start).compareTo(timeout) < 0) {
            if (isReadyAt(serverUrl)) {
                logger.info("Appium READY at {} at {}", serverUrl, java.time.LocalTime.now());
                return true;
            }
            logger.debug("Appium not ready yet...");
            sleep(1000);
        }

        logger.error("Appium failed to become ready (timeout) at {}", serverUrl);
        return false;
    }

    // ----------------- Status readiness (updated) -----------------

    public static boolean isReadyAt(URL baseUrl) {
        // Try:
        // 1) <baseUrl>/status  (when base-path is /wd/hub)
        // 2) <root>/status     (when base-path is /)
        String base = trimTrailingSlash(baseUrl.toString());

        if (isReadyAtStatusUrl(base + "/status")) return true;

        // If base contains /wd/hub, try root
        if (base.endsWith("/wd/hub")) {
            String root = base.substring(0, base.length() - "/wd/hub".length());
            if (isReadyAtStatusUrl(root + "/status")) return true;
        }

        return false;
    }

    private static boolean isReadyAtStatusUrl(String statusUrlStr) {
        HttpURLConnection conn = null;
        try {
            URL statusUrl = new URL(statusUrlStr);
            conn = (HttpURLConnection) statusUrl.openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();

            String body = "";
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8))) {
                if (br != null) {
                    body = br.lines().reduce("", (a, b) -> a + b);
                }
            } catch (Exception ignored) {
            }

            logger.debug("Status check URL={} code={} body={}", statusUrlStr, code, body);

            if (code < 200 || code >= 300) return false;
            return body.matches("(?s).*\"ready\"\\s*:\\s*true.*");
        } catch (Exception e) {
            logger.debug("Status check failed for {} => {}", statusUrlStr, e.getMessage());
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String trimTrailingSlash(String s) {
        if (s == null) return "";
        return s.replaceAll("/+$", "");
    }

    public static void waitUntilReady(URL serverUrl, Duration timeout) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout.toMillis()) {
            if (StartAppiumWithPlugin.isReadyAt(serverUrl)) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Appium at " + serverUrl, e);
            }
        }
        throw new RuntimeException("Appium is not ready at: " + serverUrl + " after " + timeout.getSeconds() + "s");
    }

    // ----------------- Networking -----------------

    private static boolean isPortOpen(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, port), 500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private static int findFreePort() {
        try (ServerSocket ss = new ServerSocket(0)) {
            ss.setReuseAddress(true);
            return ss.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Could not find a free port", e);
        }
    }

    /**
     * Try to kill the LISTENING process on the given port.
     * Returns true if we *believe* we killed something (best-effort).
     * If access denied (common on Windows services), returns false.
     */
    private static boolean tryKillProcessOnPort(int port) {
        try {
            Integer pid = findListeningPid(port);
            if (pid == null) {
                logger.info("No LISTENING PID found for port {}", port);
                return true; // nothing to kill
            }

            logger.info("Found PID {} listening on port {}. Attempting kill...", pid, port);

            Process kill = IS_WINDOWS
                    ? new ProcessBuilder("cmd", "/c", "taskkill /PID " + pid + " /F")
                    .redirectErrorStream(true).start()
                    : new ProcessBuilder("sh", "-c", "kill -15 " + pid + " || kill -9 " + pid)
                    .redirectErrorStream(true).start();

            String out = readAll(kill);
            int exit = kill.waitFor();

            if (exit == 0) {
                logger.info("Killed PID {} on port {}", pid, port);
                return true;
            }

            logger.error("Kill failed (exit {}). Output: {}", exit, out);
            return false;

        } catch (Exception e) {
            logger.error("Kill attempt failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Finds the PID that is LISTENING on the given TCP port (best effort).
     * Windows: netstat -ano, then filter by port and LISTENING in Java.
     * Linux/macOS: lsof -iTCP:<port> -sTCP:LISTEN -t
     */
    private static Integer findListeningPid(int port) throws IOException, InterruptedException {
        if (IS_WINDOWS) {
            Process p = new ProcessBuilder("cmd", "/c", "netstat -ano")
                    .redirectErrorStream(true).start();

            String out = readAll(p);
            p.waitFor();

            String portSuffix = ":" + port;
            for (String line : out.split("\\R")) {
                line = line.trim();
                if (line.isEmpty() || !line.contains(portSuffix) || !line.contains("LISTENING")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    String pidStr = parts[parts.length - 1];
                    try {
                        return Integer.parseInt(pidStr);
                    } catch (NumberFormatException ignored) {
                        // skip malformed line
                    }
                }
            }
            return null;
        } else {
            Process p = new ProcessBuilder("sh", "-c",
                    "lsof -iTCP:" + port + " -sTCP:LISTEN -t 2>/dev/null | head -n 1")
                    .redirectErrorStream(true).start();

            String out = readAll(p).trim();
            p.waitFor();

            if (out.isEmpty()) return null;
            try {
                return Integer.parseInt(out.split("\\s+")[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static String readAll(Process p) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append(System.lineSeparator());
            return sb.toString();
        }
    }

    private static String ts() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // ----------------- Prisma + logging -----------------

    /**
     * NOTE:
     * - On Windows, your old "~/.appium/..." path won't exist.
     * - Device-farm plugin may not require prisma generate on every machine.
     * This method is best-effort; if directory doesn't exist, it skips.
     */
    private static void ensurePrismaGenerated() {
        // OS-neutral path: <user.home>/.appium/node_modules/appium-device-farm
        String home = System.getProperty("user.home");
        Path pluginDir = Paths.get(home, ".appium", "node_modules", "appium-device-farm");

        if (!Files.isDirectory(pluginDir)) {
            logger.info("Device-farm plugin dir not found ({}). Skipping Prisma generate.", pluginDir);
            return;
        }

        try {
            logger.info("Ensuring Prisma client in {} ...", pluginDir);

            String[] command = IS_WINDOWS
                    ? new String[]{"cmd", "/c", "cd /d \"" + pluginDir.toString() + "\" && npx prisma generate"}
                    : new String[]{"sh", "-c", "cd \"" + pluginDir.toString() + "\" && npx prisma generate"};

            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            int exitCode = process.waitFor();
            String out = readAll(process);

            if (!out.isBlank()) {
                for (String line : out.split("\\R")) {
                    logger.info("Prisma: {}", line);
                }
            }

            if (exitCode != 0) {
                throw new RuntimeException("Prisma failed (code " + exitCode + "). Manual: cd " + pluginDir + " && npx prisma generate");
            }
            logger.info("Prisma ready.");
        } catch (Exception e) {
            throw new RuntimeException("Prisma error: " + e.getMessage(), e);
        }
    }

    private static void startLogStreaming(Process process, String key, int port, Deque<String> tail) {
        new Thread(() -> logStream(process.getInputStream(), key, port, "Appium Log", tail)).start();
        new Thread(() -> logStream(process.getErrorStream(), key, port, "Appium Error", tail)).start();
    }

    private static void logStream(java.io.InputStream stream, String key, int port, String prefix, Deque<String> tail) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String msg = ts() + " [" + key + ":" + port + "][" + prefix + "] " + line;
                logger.info("{}", msg);
                appendLogTail(tail, msg);
            }
        } catch (Exception e) {
            String msg = ts() + " [" + key + ":" + port + "][" + prefix + "] stream failed: " + e.getMessage();
            logger.error("{}", msg);
            appendLogTail(tail, msg);
        }
    }

    private static synchronized void appendLogTail(Deque<String> tail, String line) {
        if (tail.size() >= LOG_TAIL_SIZE) tail.removeFirst();
        tail.addLast(line);
    }

    private static synchronized void dumpAppiumLogTail(String key, Deque<String> tail) {
        logger.error("========== APPIUM LOG TAIL [{}] (last {} lines) ==========", key, tail.size());
        for (String s : tail) logger.error("{}", s);
        logger.error("==========================================================================");
    }

    // ----------------- File tail helpers (new) -----------------

    private static void appendFileTailToDeque(Path logFile, Deque<String> tail, int maxLines) {
        if (logFile == null) return;
        if (!Files.exists(logFile)) {
            appendLogTail(tail, ts() + " [logfile] Not found: " + logFile.toAbsolutePath());
            return;
        }

        try {
            for (String line : readLastLines(logFile, maxLines)) {
                appendLogTail(tail, ts() + " [logfile] " + line);
            }
        } catch (Exception e) {
            appendLogTail(tail, ts() + " [logfile] Failed to read tail: " + e.getMessage());
        }
    }

    /**
     * Efficient-ish "tail -n" for log files without loading whole file.
     */
    private static Deque<String> readLastLines(Path file, int lines) throws IOException {
        Deque<String> result = new ArrayDeque<>(lines);
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long length = raf.length();
            long pointer = length - 1;
            int lineCount = 0;
            StringBuilder sb = new StringBuilder();

            while (pointer >= 0 && lineCount < lines) {
                raf.seek(pointer);
                int readByte = raf.read();
                if (readByte == '\n') {
                    if (sb.length() > 0) {
                        result.addFirst(sb.reverse().toString().replace("\r", ""));
                        sb.setLength(0);
                        lineCount++;
                    }
                } else {
                    sb.append((char) readByte);
                }
                pointer--;
            }

            if (sb.length() > 0) {
                result.addFirst(sb.reverse().toString().replace("\r", ""));
            }
        }
        return result;
    }

    private static void startMergedLogStreaming(Process process, String key, int port, Deque<String> tail) {
        Thread t = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String msg = ts() + " [" + key + ":" + port + "][APPIUM] " + line;
                    logger.info("{}", msg);
                    appendLogTail(tail, msg);

                    // direct console print too
                    System.out.println(msg);
                }
            } catch (Exception e) {
                String msg = ts() + " [" + key + ":" + port + "][APPIUM] stream failed: " + e.getMessage();
                logger.error("{}", msg, e);
                appendLogTail(tail, msg);
                System.err.println(msg);
            }
        }, "appium-log-" + key + "-" + port);

        t.setDaemon(true);
        t.start();
    }
}