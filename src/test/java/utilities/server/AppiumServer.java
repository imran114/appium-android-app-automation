package utilities.server;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class AppiumServer {

    private static final int PORT = 1234;
    private static final String BASE_PATH = "/wd/hub";
    private static final int KEEP_ALIVE_TIMEOUT = 800; // seconds
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int PORT_FREE_WAIT_MS = 5000;
    private static final int SERVER_STARTUP_TIMEOUT_MS = 30000; // 30s

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static AppiumDriverLocalService appiumService;

    // ===== PATHS =====
    private static final String MAC_NODE_PATH = System.getenv("NVM_BIN") != null
            ? System.getenv("NVM_BIN") + "/node"
            : "/usr/local/bin/node";

    private static final String MAC_APPIUM_PATH = System.getenv("NVM_BIN") != null
            ? System.getenv("NVM_BIN") + "/appium"
            : "/usr/local/bin/appium";

    private static final String WIN_NODE_PATH = "C:\\Program Files\\nodejs\\node.exe";
    private static final String WIN_APPIUM_PATH = "C:\\Users\\QA\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js";


    // ===== SERVER MANAGEMENT =====
    public static void startAppium() {
        stopIfRunning();

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .usingPort(PORT)
                .withArgument(GeneralServerFlag.BASEPATH, BASE_PATH)
                .withArgument(() -> "--keep-alive-timeout", String.valueOf(KEEP_ALIVE_TIMEOUT))
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                .withArgument(() -> "--log-level", "error")
                .withArgument(() -> "--log-no-colors");

        setPlatformPaths(builder);

        appiumService = AppiumDriverLocalService.buildService(builder);

        new Thread(() -> {
            try {
                appiumService.start();
            } catch (Exception e) {
                System.err.println("❌ Error starting Appium server: " + e.getMessage());
            }
        }).start();

        long startTime = System.currentTimeMillis();
        while (!appiumService.isRunning() &&
                (System.currentTimeMillis() - startTime) < SERVER_STARTUP_TIMEOUT_MS) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        if (appiumService.isRunning()) {
            System.out.println("🚀 Appium server started successfully on port " + PORT);
        } else {
            throw new RuntimeException("❌ Appium server failed to start within timeout");
        }
    }

    public static void stopAppium() {
        if (appiumService != null && appiumService.isRunning()) {
            appiumService.stop();
            System.out.println("🛑 Appium server stopped.");
        }
    }

    private static void stopIfRunning() {
        if (!isAppiumRunning()) {
            System.out.println("No Appium server running on port " + PORT);
            return;
        }

        System.out.println("Appium server detected on port " + PORT + ". Stopping it...");
        try {
            if (appiumService != null && appiumService.isRunning()) {
                appiumService.stop();
                System.out.println("Stopped Appium server via Java service.");
            } else {
                killProcessByPort();
                System.out.println("Stopped Appium server via OS command.");
            }
            Thread.sleep(PORT_FREE_WAIT_MS);
        } catch (Exception e) {
            System.err.println("Error stopping Appium server: " + e.getMessage());
        }
    }

    // ===== HELPER METHODS =====
    private static boolean isAppiumRunning() {
        try {
            URL url = new URL("http://127.0.0.1:" + PORT + BASE_PATH + "/status");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.connect();
            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private static void killProcessByPort() throws IOException, InterruptedException {
        String[] cmd;
        if (IS_WINDOWS) {
            cmd = new String[]{"cmd", "/c", "for /f \"tokens=5\" %a in ('netstat -ano ^| findstr " + PORT + "') do taskkill /PID %a /F"};
        } else {
            cmd = new String[]{"/bin/bash", "-c",
                    "lsof -i :" + PORT + " -sTCP:LISTEN -t | xargs -r kill -15"};
        }
        Process process = new ProcessBuilder(cmd).inheritIO().start();
        process.waitFor();
    }

    private static void setPlatformPaths(AppiumServiceBuilder builder) {
        if (IS_WINDOWS) {
            builder.withAppiumJS(new File(WIN_APPIUM_PATH))
                    .usingDriverExecutable(new File(WIN_NODE_PATH));
        } else {
            builder.withAppiumJS(new File(MAC_APPIUM_PATH))
                    .usingDriverExecutable(new File(MAC_NODE_PATH));
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        startAppium();
        try {
            TimeUnit.MILLISECONDS.sleep(SERVER_STARTUP_TIMEOUT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            stopAppium();
        }
    }
}
