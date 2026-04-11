package utilities.find_apk;

import utils.fileWriter.PropertiesFileWriter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FindAPKFiles {

    /** APK path relative to project root (see repo `apps/app.apk`). */
    public static final String PROJECT_APK_RELATIVE = "apps/app.apk";

    public static String getApkFilePath() {
        String projectRoot = getProjectDirectory();
        new PropertiesFileWriter().updateApkFolderDirPath(projectRoot);

        Path apk = Paths.get(projectRoot, PROJECT_APK_RELATIVE.split("/"));
        String absolute = apk.normalize().toAbsolutePath().toString();

        if (apk.toFile().exists()) {
            new PropertiesFileWriter().updateApkFilePort(absolute);
        } else {
            System.err.println("APK not found at: " + absolute);
        }
        return absolute;
    }

    public static String getProjectDirectory() {
        String myDir = System.getProperty("user.dir");
        String toStrip = File.separator + "qa";
        int idx = myDir.indexOf(toStrip);
        return (idx > 0) ? myDir.substring(0, idx) : myDir;
    }

    /**
     * Basename of the configured APK (no extension), safe for report filenames.
     * Example: {@code apps/app.apk} → {@code app}. Used by Extent report naming.
     */
    public static String getReportFileNamePrefix() {
        Path p = Paths.get(PROJECT_APK_RELATIVE);
        String fileName = p.getFileName().toString();
        String base = fileName.toLowerCase().endsWith(".apk")
                ? fileName.substring(0, fileName.length() - 4)
                : fileName;
        String sanitized = base.replaceAll("[^a-zA-Z0-9_-]+", "_");
        return sanitized.isBlank() ? "AndroidApp" : sanitized;
    }
}
