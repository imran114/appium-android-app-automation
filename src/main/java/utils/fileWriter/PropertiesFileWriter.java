package utils.fileWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PropertiesFileWriter {
    private static final Logger logger = LogManager.getLogger(PropertiesFileWriter.class);

    // 1) Compute project root once
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));

    // 2) Base folder for testDataFiles under qa/src/test/resources
    private static final Path TEST_DATA_DIR = PROJECT_ROOT.resolve("qa")
            .resolve("src")
            .resolve("test")
            .resolve("resources")
            .resolve("testDataFiles");

    // 3) All your property files as absolute Paths
    private static final Path SHOP_FILE_PATH = TEST_DATA_DIR.resolve("shop.properties");
    private static final Path CONFIGURATION_FILE_PATH = TEST_DATA_DIR.resolve("configuration.properties");
    private static final Path EMAIL_CONFIGURATION_FILE_PATH = TEST_DATA_DIR.resolve("emailConfiguration.properties");
    private static final Path SERVER_CONFIGURATION_FILE_PATH = TEST_DATA_DIR.resolve("server.properties");
    private static final Path APK_CONFIGURATION_FILE_PATH = TEST_DATA_DIR.resolve("apkFilePath.properties");
    private static final Path APK_FOLDER_CONFIG_FILE_PATH = TEST_DATA_DIR.resolve("apkFileProjectDirectory.properties");
    private static final Path PROFILE_DETAILS_FILE_PATH = TEST_DATA_DIR.resolve("profile_details.properties");

    private Properties properties;

    public PropertiesFileWriter() {
        this.properties = new Properties();
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // Generic helper to load/update/save a single property to disk
    private void writeSingleProperty(Path file, String key, String value) {
        try {
            // ensure parent directory exists
            Files.createDirectories(file.getParent());

            Properties props = new Properties();
            if (Files.exists(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    props.load(in);
                }
            }
            props.setProperty(key, value);
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, null);
            }
            logger.info("Wrote {}={} to {}", key, value, file);
        } catch (IOException e) {
            logger.error("Failed to write {}={} to {}", key, value, file, e);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────────
    //  Public API methods (all original updateXYZ methods)

    public void updateUserName(String userName) {
        writeSingleProperty(CONFIGURATION_FILE_PATH, "userName", userName);
    }

    public void updateHost(String host) {
        writeSingleProperty(EMAIL_CONFIGURATION_FILE_PATH, "host", host);
    }

    public void updatePort(String port) {
        writeSingleProperty(EMAIL_CONFIGURATION_FILE_PATH, "port", port);
    }

    public void updateUserEmail(String userEmail) {
        writeSingleProperty(EMAIL_CONFIGURATION_FILE_PATH, "userEmail", userEmail);
    }

    public void updatePasswordEmail(String passwordEmail) {
        writeSingleProperty(EMAIL_CONFIGURATION_FILE_PATH, "password", passwordEmail);
    }

    public void updateAppiumPort(String appiumPort) {
        writeSingleProperty(SERVER_CONFIGURATION_FILE_PATH, "appiumPort", appiumPort);
    }

    public void updateApkFilePort(String apkPath) {
        writeSingleProperty(APK_CONFIGURATION_FILE_PATH, "apkPath", apkPath);
    }

    public void updateApkFolderDirPath(String apkFolderDirPath) {
        writeSingleProperty(APK_FOLDER_CONFIG_FILE_PATH, "apkFolderDirectory", apkFolderDirPath);
    }

    public void updateUserType(String userType) {
        writeSingleProperty(PROFILE_DETAILS_FILE_PATH, "userType", userType);
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // Original helper methods—now pointing at absolute paths under TEST_DATA_DIR

    public static List<String> getDeviceContentDescList() {
        Properties props = new Properties();
        List<String> list = new ArrayList<>();
        try (InputStream in = Files.newInputStream(SHOP_FILE_PATH)) {
            props.load(in);
            for (String key : props.stringPropertyNames()) {
                list.add(props.getProperty(key));
            }
        } catch (IOException ignored) {
        }
        return list;
    }

    public static void saveDeviceContentDescList(List<String> descList) {
        try {
            Files.createDirectories(SHOP_FILE_PATH.getParent());
            Properties props = new Properties();
            for (int i = 0; i < descList.size(); i++) {
                props.setProperty("device" + i, descList.get(i));
            }
            try (OutputStream out = Files.newOutputStream(SHOP_FILE_PATH)) {
                props.store(out, null);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void writeUsageDetailsToFile(String key, String usageDetails, String filePath) {
        // filePath can be relative to PROJECT_ROOT or absolute
        Path file = PROJECT_ROOT.resolve(filePath);
        try {
            Files.createDirectories(file.getParent());
            Properties props = new Properties();
            if (Files.exists(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    props.load(in);
                }
            }

            if (key.contains("validity") || key.contains("offerPrice") || key.contains("version")) {
                props.setProperty(key, usageDetails);
            } else {
                int intValue = 0;
                try {
                    for (String part : usageDetails.split("\\s+")) {
                        if (!part.isEmpty()) {
                            intValue = Integer.parseInt(part);
                            break;
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Error parsing usage details: " + ex.getMessage());
                }
                props.setProperty(key, String.valueOf(intValue));
            }

            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, "Usage details");
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static List<String> writeListOfProperties(String path, String key, List<String> selectedCategories) {
        String joined = String.join(",", selectedCategories);
        writeProperty(path, key, joined);
        return selectedCategories;
    }

    public static Set<String> writeListOfProperties(String path, String key, Set<String> selectedCategories) {
        String joined = String.join(",", selectedCategories);
        writeProperty(path, key, joined);
        return selectedCategories;
    }
    public static LinkedList<String> writeListOfProperties(String path, String key, LinkedList<String> selectedCategories) {
        String joined = String.join(",", selectedCategories);
        writeProperty(path, key, joined);
        return selectedCategories;
    }

    public static void writeProperty(String filePath, String key, String value) {
        Path file = PROJECT_ROOT.resolve(filePath);
        try {
            Files.createDirectories(file.getParent());
            Properties props = new Properties();
            if (Files.exists(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    props.load(in);
                }
            }
            props.setProperty(key, value);
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, null);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private Properties loadProperties(String filePath) {
        Properties p = new Properties();
        Path file = PROJECT_ROOT.resolve(filePath);
        try (Reader r = Files.newBufferedReader(file)) {
            p.load(r);
        } catch (IOException ignored) {
        }
        return p;
    }

    private void writeProperties(String filePath, Properties props) {
        Path file = PROJECT_ROOT.resolve(filePath);
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, null);
            }
            logger.info("Properties written to file: " + file);
        } catch (IOException e) {
            logger.error("Error writing properties to file: " + file, e);
        }
    }

    public void updateProperty(String filePath, String key, String value) {
        Path file = PROJECT_ROOT.resolve(filePath);
        try {
            Files.createDirectories(file.getParent());
            // load existing
            try (InputStream in = Files.newInputStream(file)) {
                properties.load(in);
            } catch (IOException ignored) {
            }
            // update + save
            properties.setProperty(key, value);
            try (OutputStream out = Files.newOutputStream(file)) {
                properties.store(out, null);
            }
            logger.info("Updated property: {}={} in file: {}", key, value, file);
        } catch (IOException e) {
            logger.error("Failed to update property in file: " + file, e);
        }
    }
}
