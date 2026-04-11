package utilities.device_data_Reader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.file_reader.PropertiesFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;



// This class reads device data from a JSON file.
public class DeviceDataReader {
    private static final Logger logger = LogManager.getLogger(DeviceDataReader.class);

    private static final String DEVICES_JSON_FILE_PATH = "/src/test/resources/testDataFiles/devices.json"; // Update with the correct file path

    public static List<DeviceData> getDeviceData() {
        logger.info("Reading device data from JSON file.");
        Gson gson = new Gson();
        File jsonFile = new File(PropertiesFileReader.returnFilePath(DEVICES_JSON_FILE_PATH));
        System.out.println(jsonFile.exists());
        List<DeviceData> deviceDataList = null;
        try {
            deviceDataList = gson.fromJson(new FileReader(jsonFile), new TypeToken<List<DeviceData>>(){}.getType());
        } catch (FileNotFoundException e) {
            logger.error("devices.json not found: {}", e.getMessage());
            return Collections.emptyList();
        }

        logger.info("Device data read successfully.");
        return deviceDataList != null ? deviceDataList : Collections.emptyList();
    }

}
