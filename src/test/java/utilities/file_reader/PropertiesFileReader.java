package utilities.file_reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


// This class reads properties from configuration and email configuration files.
public class PropertiesFileReader {
    private static final Logger logger = LogManager.getLogger(PropertiesFileReader.class);

    private final Properties configurationProperties;
    private final static String userDir = System.getProperty("user.dir");
    private final Properties emailProperties;
    private final Properties englishProperties;
    private final Properties urduProperties;
    private final Properties appiumFilePathProperties;
    private final Properties apkFolderPathProperties;
    private String configFilePath = "/src/test/resources/testDataFiles/login_credentials/configuration.properties";
    private String emailFilePath = "/src/test/resources/testDataFiles/emailConfiguration.properties";
    private String englishFilePath = "/src/test/resources/translation/english_translation.properties";
    private String urduFilePath = "/src/test/resources/translation/urdu_translation.properties";
    private String apkFilePath = "/src/test/resources/testDataFiles/apkFilePath.properties";
    private String appiumFilePath = "/src/test/resources/testDataFiles/server.properties";
    private String apkFolderFilePath = "/src/test/resources/testDataFiles/apkFileProjectDirectory.properties";


    public static String returnFilePath(String path) {
        return userDir + path;
    }

    public PropertiesFileReader() {
        configurationProperties = readProperties(PropertiesFileReader.returnFilePath(configFilePath));
        emailProperties = readProperties(PropertiesFileReader.returnFilePath(emailFilePath));
        englishProperties = readProperties(PropertiesFileReader.returnFilePath(englishFilePath));
        urduProperties = readProperties(PropertiesFileReader.returnFilePath(urduFilePath));
        appiumFilePathProperties = readProperties(PropertiesFileReader.returnFilePath(appiumFilePath));
        apkFolderPathProperties = readProperties(PropertiesFileReader.returnFilePath(apkFolderFilePath));
    }


    public String getTranslation(String key, String lang) {
        if (lang.contains("en")) {
            return englishProperties.getProperty(key);
        } else if (lang.contains("ur")) {
            return urduTextViewer(urduProperties.getProperty(key));
        } else {
            return "Key Is not Present in the property file";

        }

    }


    private String urduTextViewer(String text) {
        text = new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return text;

    }

    // This method reads properties from the configuration file.
    public void readConfigurationProperties() {
        configFilePath = "/src/test/resources/testDataFiles/configuration.properties";
        logger.info("Reading configuration properties from file: " + configFilePath);

//        configurationProperties = readProperties(configFilePath);
        logger.info("Configuration properties read successfully.");

    }

    // This method reads properties from the email configuration file.
    public void readEmailProperties() {
        emailFilePath = "/src/test/resources/testDataFiles/emailConfiguration.properties";
        logger.info("Reading email configuration properties from file: " + configFilePath);

//        emailProperties = readProperties(emailFilePath);
        logger.info("Email configuration properties read successfully.");
    }

    public String getptcluserType() {
        return getProperty(configurationProperties, "ptcl");
    }
    public String getGuestuserType() {
        return getProperty(configurationProperties, "guest_prepaid");
    }

    public String getflashfiberLoginID() {
        return getProperty(configurationProperties, "flashfiberLoginNumber");
    }

    public String getCharjiLoginID() {
        return getProperty(configurationProperties, "charji");
    }

    public String getBlazeNumber() {
        return getProperty(configurationProperties, "blaze");
    }

    public String getPtclAccountID() {
        return getProperty(configurationProperties, "ptclAccountNumber");
    }

    public String getUfonePrepaidNumber() {
        return getProperty(configurationProperties, "prepaid");
    }

    public String getUfonePostpaidNumber() {
        return getProperty(configurationProperties, "postpaid");
    }

    public String getEmail() {
        return getProperty(emailProperties, "userEmail");
    }

    public String getPort() {
        return getProperty(emailProperties, "port");
    }

    public String getHost() {
        return getProperty(emailProperties, "host");
    }

    public String getEmailPassword() {
        return getProperty(emailProperties, "password");
    }

    public String getPasswordPins() {
        return getProperty(configurationProperties, "passwordPins");
    }

    public String getAppiumPort() {
        return getProperty(appiumFilePathProperties, "appiumPort");
    }


    public String getProjectEnvVariable() {
        return getProperty(apkFolderPathProperties, "apkFolderDirectory");
    }

    public String getApkFilePath() {
        return getProperty(configurationProperties, "apkPath");
    }

    private Properties readProperties(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.getMessage();
        }
        return properties;
    }

    public String readProperty(String filePath, String key) {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
            e.getMessage();
            return null; // Or handle the exception according to your requirements
        }
    }


    private String getProperty(Properties properties, String propertyName) {
        if (properties != null) {
            return properties.getProperty(propertyName);
        }
        return null;
    }

    private String arabicTextViewer(String text) {

        text = new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        System.out.println(text);
        return text;
    }

    public String readUsageDetailsValue(String key, String filePath) {
        String value = null;
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            // Retrieve the value associated with the given key
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.getMessage();
        }
        return value;
    }

}


