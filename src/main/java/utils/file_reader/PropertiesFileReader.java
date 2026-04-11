package utils.file_reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


// This class reads properties from configuration and email configuration files.
public class PropertiesFileReader {
    private static final Logger logger = LogManager.getLogger(PropertiesFileReader.class);

    private final Properties configurationProperties;
    private final Properties emailProperties;
    private final Properties englishProperties;
    private final Properties urduProperties;
    private final static String userDir = System.getProperty("user.dir");
    private String configFilePath = "/src/test/resources/testDataFiles/login_credentials/configuration.properties";
    private String emailFilePath = "/src/test/resources/testDataFiles/emailConfiguration.properties";

    public static String returnFilePath(String path) {
        return userDir + path;
    }
    public PropertiesFileReader() {
        configurationProperties = readProperties(returnFilePath(configFilePath));
        emailProperties = readProperties(returnFilePath(emailFilePath));
        String englishFilePath = "/src/test/resources/translation/english_translation.properties";
        englishProperties = readProperties(returnFilePath(englishFilePath));
        String urduFilePath = "/src/test/resources/translation/urdu_translation.properties";
        urduProperties = readProperties(returnFilePath(urduFilePath));
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

        try {

            text = new String(text.getBytes("ISO-8859-1"), "UTF-8");

        } catch (UnsupportedEncodingException e) {


            e.getMessage();

        }

        return text;

    }

    // This method reads properties from the configuration file.
    public void readConfigurationProperties() {
        configFilePath = "src/test/resources/testDataFiles/login_credentials/configuration.properties";
        logger.info("Reading configuration properties from file: {}", configFilePath);

    }

    // This method reads properties from the email configuration file.
    public void readEmailProperties() {
        emailFilePath = "/src/test/resources/testDataFiles/emailConfiguration.properties";
        logger.info("Reading email configuration properties from file: " + configFilePath);

    }

    public String getptclLoginID() {
        return getProperty(configurationProperties, "ptcl");
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


    private Properties readProperties(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.getMessage();
        }
        return properties;
    }
    public static String readProperty(String filePath, String key) {
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

    public String readUsageDetailsValue(String key,String filePath) {
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

    public static List<String> readListFromPropertiesFile(String filePath, String key) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            e.getMessage();
            return null; // or handle the error as needed
        }

        String propertyValue = properties.getProperty(key, "");
        return Arrays.asList(propertyValue.split(","));
    }
}


