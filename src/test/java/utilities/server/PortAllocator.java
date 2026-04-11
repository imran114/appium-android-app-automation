package utilities.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Small JVM-local port allocator for parallel test execution.
 *
 * Notes:
 * - This only prevents collisions inside the current JVM.
 * - It does not guarantee that external processes won't grab a port later.
 * - Appium start logic still double-checks port availability and can kill/fallback.
 */
public final class PortAllocator {
    private static final Logger logger = LogManager.getLogger(PortAllocator.class);
    private static final String HOST = "127.0.0.1";

    private static final Map<String, Integer> allocated = new HashMap<>();
    private static final Set<Integer> reservedPorts = new HashSet<>();

    private PortAllocator() {}

    public static synchronized Integer getIfAllocated(String key) {
        return allocated.get(key);
    }

    public static synchronized int allocate(String key) {
        Integer existing = allocated.get(key);
        if (existing != null) return existing;

        for (int attempts = 0; attempts < 50; attempts++) {
            int port = findEphemeralPort();
            if (reservedPorts.contains(port)) continue;
            if (isPortOpen(port)) continue; // best-effort: avoid currently used ports

            reservedPorts.add(port);
            allocated.put(key, port);
            logger.info("Allocated port {} for key={}", port, key);
            return port;
        }
        throw new RuntimeException("Could not allocate a free port for key=" + key);
    }

    /**
     * Reserve an explicit port for a key (e.g. a user-provided port).
     * This prevents JVM-local collisions in parallel runs.
     *
     * Returns true if reserved; false if already reserved or currently in use.
     */
    public static synchronized boolean reserve(String key, int port) {
        Integer existing = allocated.get(key);
        if (existing != null) return existing == port;

        if (reservedPorts.contains(port)) return false;
        if (isPortOpen(port)) return false;

        reservedPorts.add(port);
        allocated.put(key, port);
        logger.info("Reserved port {} for key={}", port, key);
        return true;
    }

    public static synchronized void release(String key) {
        Integer port = allocated.remove(key);
        if (port != null) {
            reservedPorts.remove(port);
            logger.info("Released port {} for key={}", port, key);
        }
    }

    public static boolean isPortOpen(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, port), 300);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int findEphemeralPort() {
        try (ServerSocket ss = new ServerSocket(0)) {
            ss.setReuseAddress(true);
            return ss.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Could not find a free port", e);
        }
    }
}

