package utilities.server;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AppiumEnvironmentInfo {

    // 🔒 Toggle this to enable/disable printing
    private static final boolean ENABLED = false; // Set to false to disable

    public static void printEnvironmentInfo() {
        // ⛔ Early return if disabled
        if (!ENABLED) return;

        System.out.println("========== 📦 APPIUM ENVIRONMENT INFO ==========");

        run("node -v", "🟢 Node Version");
        run("appium -v", "🟣 Appium Version");

        // Installed drivers (Appium 2 compatible)
        run("appium driver list --installed", "🚗 Installed Appium Drivers");

        // Show driver versions via npm (reliable)
        run("npm list -g --depth=0 | grep appium-", "🧩 Appium Driver Packages");

        // Optional Appium Doctor (only if installed)
        runIfExists("appium-doctor --version", "🩺 Appium Doctor Version");
        runIfExists("appium-doctor --android", "🍎 iOS Environment Check");
//        runIfExists("appium-doctor --android", "🤖 Android Environment Check");

        System.out.println("================================================");
    }

    private static void run(String command, String title) {
        try {
            System.out.println("\n" + title + ":");

            Process process = new ProcessBuilder("/bin/bash", "-c", command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip noisy Appium warnings
                if (!line.startsWith("WARN Appium")) {
                    System.out.println("  " + line);
                }
            }
            process.waitFor();

        } catch (Exception e) {
            System.out.println("  ❌ Failed to run: " + command);
        }
    }

    private static void runIfExists(String command, String title) {
        try {
            Process check = new ProcessBuilder("/bin/bash", "-c",
                    "command -v " + command.split(" ")[0])
                    .start();

            if (check.waitFor() == 0) {
                run(command, title);
            } else {
                System.out.println("\n" + title + ":");
                System.out.println("  ⚠ Not installed");
            }
        } catch (Exception ignored) {
        }
    }
}
