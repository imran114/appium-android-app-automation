package utilities.device_manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DeviceManager {
    public static String getDeviceDetail(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.getMessage();
        }
        return output.toString().trim();
    }

    public static String getDeviceName() {
        return getDeviceDetail("adb shell getprop ro.product.model");
    }

    public static String getPlatformVersion() {
        return getDeviceDetail("adb shell getprop ro.build.version.release");
    }

    public static String getUDID() {
        String output = getDeviceDetail("adb devices");
        String[] lines = output.split("\n");

        for (String line : lines) {
            if (line.trim().endsWith("device") && !line.startsWith("List")) {
                return line.split("\t")[0]; // Extract the UDID before the tab character
            }
        }

        throw new RuntimeException("No connected devices found or unable to fetch UDID.");
    }
}

