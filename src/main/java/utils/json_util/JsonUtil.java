package utils.json_util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtil {

    public static void saveJsonToFile(JSONObject jsonObject, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toJSONString());
            System.out.println("JSON saved successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving JSON to file: " + e.getMessage());
        }
    }

    public static void saveJsonArrayToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toJSONString());
            System.out.println("JSON Array saved successfully to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving JSON Array to file: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public static void addObjectToJson(JSONObject parentObject, String key, JSONObject childObject) {
        parentObject.put(key, childObject);
    }

    /**
     * Creates a JSON object with key-value pairs.
     *
     * @param keys   The array of keys.
     * @param values The array of values corresponding to the keys.
     * @return A JSON object containing the key-value pairs.
     */
    @SuppressWarnings("unchecked")
    public static JSONObject createJsonObject(String[] keys, String[] values) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < keys.length; i++) {
            jsonObject.put(keys[i], values[i]);
        }
        return jsonObject;
    }


}

