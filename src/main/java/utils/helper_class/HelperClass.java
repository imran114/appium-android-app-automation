package utils.helper_class;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.seleniumUtils.SeleniumUtils;

public class HelperClass extends SeleniumUtils {
    private final By dashboardScrollAreaLocator = By.xpath("//android.widget.ScrollView");
    public HelperClass(AppiumDriver driver) {
        super(driver);
    }

    public void scrollDownIfNotDisplayed(By locator) {
        waitForVisibility(dashboardScrollAreaLocator,50);
        if (!isDisplayed(locator, 2)) {
            scrollingMethods.swipeDown(dashboardScrollAreaLocator);
            waitForVisibility(dashboardScrollAreaLocator);
            scrollingMethods.swipeDown(dashboardScrollAreaLocator);
        }
    }

}
