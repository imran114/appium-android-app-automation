package utils.ad_handler;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import utils.seleniumUtils.SeleniumUtils;

import java.util.Map;

public class AdHandlerUtil extends SeleniumUtils {

    public AdHandlerUtil(AppiumDriver driver) {
        super(driver);
    }

    private final By adDismissButton = By.xpath("//XCUIElementTypeLink[@name='Dismiss']");
    private final By adCloseButton = AppiumBy.accessibilityId("Close");
    private final By adCloseButton2 = AppiumBy.accessibilityId("Close Advertisement");
    private final By adCloseButton3 = AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"CLOSE\"]");

    private int attempts = 0;

    public void dismissAd() {
        if (isDisplayed(adDismissButton, 2)) {
            closeAdWhileDisplayed(adDismissButton);
        } else if (isDisplayed(adCloseButton, 2)) {
            System.out.println("adCloseButton clicked, Ad closed");
            closeAdWhileDisplayed(adCloseButton);
        } else if (isDisplayed(adCloseButton2, 2)) {
            System.out.println("adCloseButton2 clicked, Ad closed");
            closeAdViaCoordinates(adCloseButton2);
        } else if (isDisplayed(adCloseButton3, 2)) {
            System.out.println("//XCUIElementTypeStaticText[@name=\"CLOSE\"] Displayed");
            System.out.println("adCloseButton3 clicked, Ad closed");
            closeAdWhileDisplayed(adCloseButton3);
        }
    }


    public void dismissAd(int timeout) {
        if (isDisplayed(adDismissButton, timeout)) {
            closeAdWhileDisplayed(adDismissButton);
        } else if (isDisplayed(adCloseButton, timeout)) {
            System.out.println("adCloseButton clicked, Ad closed");
            closeAdWhileDisplayed(adCloseButton);
        } else if (isDisplayed(adCloseButton2, timeout)) {
            System.out.println("adCloseButton2 clicked, Ad closed");
            closeAdViaCoordinates(adCloseButton2);
        } else if (isDisplayed(adCloseButton3, timeout)) {
            System.out.println("adCloseButton3 clicked, Ad closed");
            closeAdWhileDisplayed(adCloseButton3);
        }
    }

    private void closeAdWhileDisplayed(By button) {
        click(button);
        boolean isAdDisplayed = isDisplayed(button, 5);
        int maxAttempts = 10;
        while (isAdDisplayed && attempts < maxAttempts) {
            System.out.println(button.toString()+": displayed");
            click(button);
            System.out.println(button+":  clicked, Ad closed");
            isAdDisplayed = isDisplayed(button, 5);
            attempts++;
        }
    }



    private void closeAdViaCoordinates(By closeButtonBy) {
        int attempts = 0;
        int maxAttempts = 15;

        while (attempts < maxAttempts) {
            if (!isDisplayed(closeButtonBy, 3)) {
                System.out.println("No ad detected");
                return;
            }
                WebElement btn = returnWebElement(closeButtonBy);
                Point p = btn.getLocation();
                Dimension s = btn.getSize();
                int x = p.getX() + s.getWidth() / 2;
                int y = p.getY() + s.getHeight() / 2;

                try {
//                    tapByCoordinates(x, y);
                    System.out.println("Closed ad with W3C PointerInput tap at (" + x + ", " + y + ")");
                } catch (Exception e1) {
                    // Fallback: mobile: tap (XCUITest extension—bypasses WDA entirely)
                    Map<String, Object> params = ImmutableMap.of("x", x, "y", y);
                    driver.executeScript("mobile: tap", params);
                    System.out.println("Closed ad with mobile: tap fallback at (" + x + ", " + y + ")");
                }

                sleep(500); // Wait for ad dismissal animation
                if (!isDisplayed(closeButtonBy, 2)) {
                    System.out.println("Ad successfully closed");
                    return;
                }

            attempts++;
            sleep(300);
        }
        System.out.println("Could not close ad after " + maxAttempts + " attempts—consider longer pauses or ad network specifics");
    }

}
