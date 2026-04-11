package testBase;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;
import utilities.device_data_Reader.DeviceData;
import utilities.device_data_Reader.DeviceDataReader;
import utilities.find_apk.FindAPKFiles;
import utilities.server.PortAllocator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class DesiredCapabilitiesManager {

    public static BaseOptions<?> getDesiredCapabilities(String env, String udid) {
        return getAndroidMobileCapabilities(udid);
    }


    private static UiAutomator2Options getAndroidMobileCapabilities(String udid) {

        DeviceData.Device device = getDevice(udid);
        String platformName = nonBlankOrDefault(device.getPlatformName(), "Android");
        String appPath = resolveAppPath(device.getApp());

        System.out.println("App path: " + appPath);
        System.out.println("device.getPlatformVersion(): " + device.getPlatformVersion());
        System.out.println(System.getProperty("user.dir"));

        int newCommandSec = intOrDefault(device.getNewCommandTimeoutSeconds(), 3600);
        int u2InstallMs = intOrDefault(device.getUiautomator2ServerInstallTimeout(), 120000);
        int u2LaunchMs = intOrDefault(device.getUiautomator2ServerLaunchTimeout(), 120000);
        int adbExecMs = intOrDefault(device.getAdbExecTimeout(), 180000);

        UiAutomator2Options opts = new UiAutomator2Options()
                .setPlatformName(platformName)
                .setPlatformVersion(device.getPlatformVersion())
                .setAutomationName(device.getAutomationName())
                .setUdid(device.getUdid())
                .setDeviceName(device.getDeviceName())
                .setNewCommandTimeout(Duration.ofSeconds(newCommandSec))
                .amend("uiautomator2ServerInstallTimeout", u2InstallMs)
                .amend("uiautomator2ServerLaunchTimeout", u2LaunchMs)
                .amend("adbExecTimeout", adbExecMs)
                .amend("autoGrantPermissions", boolOrDefault(device.getAutoGrantPermissions(), true))
                .setApp(appPath)
                .setAppWaitForLaunch(boolOrDefault(device.getAppWaitForLaunch(), true))
                .setFullReset(boolOrDefault(device.getFullReset(), false))
                .amend("ignoreHiddenApiPolicyError", boolOrDefault(device.getIgnoreHiddenApiPolicyError(), true))
                .amend("instrumentationKeepAlive", boolOrDefault(device.getInstrumentationKeepAlive(), true));

        if (Boolean.TRUE.equals(device.getNoReset())) {
            opts.setNoReset(true);
        }

        // Parallel execution: each device/session must have a unique UiAutomator2 systemPort.
        int resolvedSystemPort = PortAllocator.allocate("systemPort:" + udid);
        opts = opts.amend("systemPort", resolvedSystemPort);
        return opts;
    }

    /** Uses {@link FindAPKFiles#PROJECT_APK_RELATIVE} when JSON {@code app} is blank; otherwise resolves relative paths from project root. */
    private static String resolveAppPath(String appFromDevice) {
        if (appFromDevice == null || appFromDevice.isBlank()) {
            return FindAPKFiles.getApkFilePath();
        }
        Path p = Paths.get(appFromDevice.trim());
        if (p.isAbsolute()) {
            return p.normalize().toString();
        }
        return Paths.get(FindAPKFiles.getProjectDirectory(), appFromDevice.trim())
                .normalize()
                .toAbsolutePath()
                .toString();
    }

    private static XCUITestOptions getiOSMobileCapabilities() {
        return new XCUITestOptions().setPlatformName("iOS");
    }

    private static DeviceData.Device getDevice(String udid) {
        List<DeviceData> devices = DeviceDataReader.getDeviceData();
        return devices.stream()
                .map(DeviceData::getDevice)
                .filter(d -> d != null && udid.equals(d.getUdid()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Device with UDID " + udid + " not found."));
    }

    private static String nonBlankOrDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private static int intOrDefault(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static boolean boolOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }


}
