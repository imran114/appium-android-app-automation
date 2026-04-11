package utils.app_manager;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import lombok.extern.slf4j.Slf4j;
import utils.seleniumUtils.SeleniumUtils;

import java.util.function.Consumer;
@Slf4j
public class AppManager extends SeleniumUtils {

//    private static LoaderUtils loaderUtils;
    public AppManager(AppiumDriver driver) {
        super(driver);
//        loaderUtils = new LoaderUtils(driver, platform);
    }

    public static void ensureAppIsOpen(AppiumDriver driver) {
        System.out.println("Check App State");
        AndroidDriver androidDriver = (AndroidDriver) driver;
        ApplicationState state = androidDriver.queryAppState("com.ufoneselfcare");

        if (state == ApplicationState.RUNNING_IN_FOREGROUND) {
            System.out.println("The app is already running in the foreground.");
        } else {
            System.out.println("The app is not running in the foreground. Launching the app...");
            try {
                androidDriver.activateApp("com.ufoneselfcare");
                state = androidDriver.queryAppState("com.ufoneselfcare");
                if (state == ApplicationState.RUNNING_IN_FOREGROUND) {

                    System.out.println("The app was successfully launched.");
                } else {
                    System.err.println("The app could not be launched successfully.");
                }
            } catch (Exception e) {
                System.err.println("Failed to launch the app: " + e.getMessage());

            }
        }
    }




    public static void ensureAppIsOpen(AppiumDriver driver, Consumer<AppiumDriver> navigateToScreen) {
        System.out.println("ensureAppIsOpen called to check state of app");
        AndroidDriver androidDriver = (AndroidDriver) driver;
        ApplicationState state = androidDriver.queryAppState("com.ufoneselfcare");

        try {

            if (state == ApplicationState.RUNNING_IN_FOREGROUND) {
                System.out.println("The app is already running in the foreground.");
                return;
            }

            System.out.println("The app is not running in the foreground. Launching the app...");
            androidDriver.activateApp("com.ufoneselfcare");

            // Re-check after potential activation
            state = androidDriver.queryAppState("com.ufoneselfcare");
            if (state == ApplicationState.RUNNING_IN_FOREGROUND) {
                System.out.println("App is in the foreground. Executing navigation flow...");
                if (navigateToScreen != null) {
                    navigateToScreen.accept(driver);
                }
            } else {
                System.err.println("The app could not be launched successfully.");
            }
        } catch (Exception e) {
            System.err.println("Failed to launch the app: " + e.getMessage());
        }
    }



}
