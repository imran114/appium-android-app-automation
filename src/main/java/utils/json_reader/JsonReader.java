package utils.json_reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JsonReader {

    /**
     * Parses a JSON object from the given file path.
     */
    public static JSONObject readJsonFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser jsonParser = new JSONParser();
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("Error reading JSON from file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses a JSON array from the given file path.
     */

    public static List<String> getOffersForFilter(String filePath, String filterName) {
        JSONObject jsonObject = readJsonFromFile(filePath);
        if (jsonObject == null || !jsonObject.containsKey(filterName)) {
            return Collections.emptyList();
        }

        Object filterObject = jsonObject.get(filterName);

        List<String> offers = new ArrayList<>();

        // Case 1: filter is a JSONObject (like your "roaming", "voice", etc.)
        if (filterObject instanceof JSONObject) {
            JSONObject offersJson = (JSONObject) filterObject;
            for (Object key : offersJson.keySet()) {
                if (key != null) {
                    String offer = key.toString().trim();
                    if (!offer.isEmpty()) {
                        offers.add(offer);
                    }
                }
            }
        }

        // Case 2: filter is a JSONArray (future possibility)
        else if (filterObject instanceof JSONArray) {
            JSONArray offersArray = (JSONArray) filterObject;
            for (Object o : offersArray) {
                if (o != null) {
                    String offer = o.toString().trim();
                    if (!offer.isEmpty()) {
                        offers.add(offer);
                    }
                }
            }
        }

        return offers;
    }


    public static Object readValue(String filePath, String keyPath) {
        JSONObject root = readJsonFromFile(filePath);
        if (root == null || keyPath == null || keyPath.trim().isEmpty()) return null;
        return getByPath(root, keyPath.trim());
    }

    /** Convenience: read String value (returns null if not found) */
    public static String readString(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        return v != null ? String.valueOf(v) : null;
    }

    /** Convenience: read Double value (tries number first, then parses string) */
    public static Double readDouble(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString().trim()); } catch (Exception e) { return null; }
    }

    /** Convenience: read Long value */
    public static Long readLong(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString().trim()); } catch (Exception e) { return null; }
    }

    /** Convenience: read Boolean value */
    public static Boolean readBoolean(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        if (v == null) return null;
        if (v instanceof Boolean) return (Boolean) v;
        String s = v.toString().trim().toLowerCase();
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        return null;
    }

    /** Get a JSONObject at path (null if not found or not an object) */
    public static JSONObject readObject(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        return (v instanceof JSONObject) ? (JSONObject) v : null;
    }

    /** Get a JSONArray at path (null if not found or not an array) */
    public static JSONArray readArray(String filePath, String keyPath) {
        Object v = readValue(filePath, keyPath);
        return (v instanceof JSONArray) ? (JSONArray) v : null;
    }

    /** List keys at an object path (empty set if not an object) */
    public static Set<?> keysAt(String filePath, String keyPath) {
        JSONObject obj = keyPath == null || keyPath.isEmpty()
                ? readJsonFromFile(filePath)
                : readObject(filePath, keyPath);
        return (obj != null) ? obj.keySet() : Collections.emptySet();
    }

    /**
     * Returns the offer name from Punjab with the lowest price.
     * Returns null if Punjab section not found or no offers.
     */
        public static String getLowestPriceOfferInPunjab() {
        JSONObject root = readJsonFromFile("src/test/resources/testDataFiles/location_based_offers.json");
        if (root == null || !root.containsKey("punjab")) {
            return null;
        }

        Object punjabObject = root.get("punjab");
        if (!(punjabObject instanceof JSONArray)) {
            return null;
        }

        JSONArray punjabArray = (JSONArray) punjabObject;
        String lowestOffer = null;
        double minPrice = Double.MAX_VALUE;

        for (Object item : punjabArray) {
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject offerObj = (JSONObject) item;
            if (offerObj.size() != 1) {
                continue;
            }
            Object offerKey = offerObj.keySet().iterator().next();
            Object priceObj = offerObj.get(offerKey);
            if (!(priceObj instanceof Number)) {
                continue;
            }
            double price = ((Number) priceObj).doubleValue();
            String offerName = offerKey.toString().trim();
            if (price < minPrice && !offerName.isEmpty()) {
                minPrice = price;
                lowestOffer = offerName;
            }
        }

        return lowestOffer;
    }

    /* -------------------- internal helpers -------------------- */

    private static Object getByPath(Object current, String keyPath) {
        String[] tokens = keyPath.split("\\.");
        Object node = current;

        for (String token : tokens) {
            if (node == null) return null;

            // token may include array index: e.g., "items[0]"
            String key = token;
            Integer arrayIndex = null;

            int idxStart = token.indexOf('[');
            if (idxStart >= 0 && token.endsWith("]")) {
                key = token.substring(0, idxStart);
                String idxStr = token.substring(idxStart + 1, token.length() - 1).trim();
                try {
                    arrayIndex = Integer.parseInt(idxStr);
                } catch (NumberFormatException ignore) {
                    return null; // invalid index format
                }
            }

            // navigate object key if key is non-empty
            if (!key.isEmpty()) {
                if (!(node instanceof JSONObject)) return null;
                node = ((JSONObject) node).get(key);
            }

            // then, if an array index was provided, navigate array element
            if (arrayIndex != null) {
                if (!(node instanceof JSONArray)) return null;
                JSONArray arr = (JSONArray) node;
                if (arrayIndex < 0 || arrayIndex >= arr.size()) return null;
                node = arr.get(arrayIndex);
            }
        }
        return node;
    }


}