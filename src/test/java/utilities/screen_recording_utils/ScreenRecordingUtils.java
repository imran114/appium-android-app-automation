package utilities.screen_recording_utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ScreenRecordingUtils {

    private static final String RECORDINGS_FOLDER = "recordings";

    private static String sanitize(String s) {
        return (s == null ? "unknown" : s).replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static boolean hasNoSession(AppiumDriver driver) {
        try {
            return driver == null || driver.getSessionId() == null;
        } catch (Throwable t) {
            return true;
        }
    }

    // -------------------- START RECORDING --------------------
    public static void startRecording(AppiumDriver driver, String bucket) {

        if (hasNoSession(driver)) {
            System.out.println("[REC] Skipped – no Appium session");
            return;
        }

        try {
            Files.createDirectories(Paths.get(RECORDINGS_FOLDER, sanitize(bucket)));
        } catch (Exception ignore) {}

        try {
            // Android 14+ (Xiaomi especially) needs time before screenrecord can attach
            Thread.sleep(5000);

            ((CanRecordScreen) driver).startRecordingScreen();
            System.out.println("[REC] Recording started for: " + bucket);

        } catch (Exception e) {
            System.err.println("[REC] Start failed (Android 15 safe ignore): " + e.getMessage());
        }
    }

    // -------------------- STOP & SAVE --------------------
    public static void stopAndSaveRecording(AppiumDriver driver, String className, String bucket) {

        if (hasNoSession(driver)) {
            System.out.println("[REC] Skipped save – no Appium session");
            return;
        }

        String base64 = "";

        try {
            base64 = ((CanRecordScreen) driver).stopRecordingScreen();
        } catch (Exception e) {
            // This is where Xiaomi / Android 15 fails killall
            System.err.println("[REC] Stop failed safely ignored: " + e.getMessage());
            return;
        }

        if (base64 == null || base64.trim().isEmpty()) {
            System.err.println("[REC] No video data returned");
            return;
        }

        Path bucketDir = Paths.get(RECORDINGS_FOLDER, sanitize(bucket));
        String prefix = sanitize(className) + "_";

        try {
            Files.createDirectories(bucketDir);
        } catch (Exception ignore) {}

        // Delete old recordings of this class
        try {
            Files.list(bucketDir)
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (Exception ignore) {}
                    });
        } catch (Exception ignore) {}

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        File out = bucketDir.resolve(prefix + timestamp + ".mp4").toFile();

        try (FileOutputStream fos = new FileOutputStream(out)) {
            byte[] decoded = Base64.getDecoder().decode(base64);
            fos.write(decoded);

            if (out.length() == 0) {
                System.err.println("[REC] Video file empty – Android blocked screenrecord");
            } else {
                System.out.println("[REC] Saved: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
            }

        } catch (Exception e) {
            System.err.println("[REC] Save failed: " + e.getMessage());
        }
    }
}
