package utilities.fileWriter;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.Properties;

public class PropertiesFileWriter {
    private static final Logger logger = LogManager.getLogger(PropertiesFileWriter.class);

    /**
     * Core helper: load, set, store.
     */
    private void writeSingleProperty(String filePath, String key, String value) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            props.load(in);
        } catch (IOException e) {
            logger.warn("Could not load existing props, will create new: {}", filePath, e);
        }
        props.setProperty(key, value);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            props.store(out, null);
            logger.info("Wrote {}={} to {}", key, value, filePath);
        } catch (IOException e) {
            logger.error("Failed to write props to: {}", filePath, e);
        }
    }

    public void updateApkFilePort(String apkFilePath) {
        String apkPathFile = "src/test/resources/testDataFiles/apkFilePath.properties";
        writeSingleProperty(apkPathFile,   "apkPath",          apkFilePath);
    }

    public void updateApkFolderDirPath(String apkFolderDirPath) {
        String apkFolderFile = "src/test/resources/testDataFiles/apkFileProjectDirectory.properties";
        writeSingleProperty(apkFolderFile, "apkFolderDirectory", apkFolderDirPath);
    }
}
