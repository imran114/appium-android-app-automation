// File: testBase/DriverManager.java
package testBase;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.options.BaseOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverManager {

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<AndroidDriver> androidDriver = new ThreadLocal<>();

    public AppiumDriver getDriver(String env, String udid) {
        System.out.println("Getting the driver...");
        System.out.println("Env: " + env + ", UDID: " + udid);

        if (driver.get() == null) {

            // ✅ Parallel-safe: start server for this device (UDID) on its own port
//            URL serverUrl = StartAppiumWithPlugin.startAppiumDeviceFarmServer(udid);
            URL serverUrl = null;
            try {
                serverUrl = new URL("http://127.0.0.1:4723/wd/hub");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            BaseOptions<?> capabilities =
                    DesiredCapabilitiesManager.getDesiredCapabilities(env, udid);
            System.out.println("Appium server URL being used: " + serverUrl);

            // ✅ MUST: wait until ready (don’t do one probe then throw)
//            StartAppiumWithPlugin.waitUntilReady(serverUrl, Duration.ofSeconds(120));

            driver.set(getAndroidDriver(serverUrl, capabilities));
        }

        return driver.get();
    }

    private AndroidDriver getAndroidDriver(URL serverUrl, BaseOptions<?> capabilities) {
        try {
            if (androidDriver.get() == null) {
                System.out.println("Initializing AndroidDriver...");
                androidDriver.set(new AndroidDriver(serverUrl, capabilities));
            }
            return androidDriver.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void quitDriver() {
        System.out.println("Quitting the driver...");
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            androidDriver.remove();
            System.out.println("Driver quit successfully.");
        }
    }
}