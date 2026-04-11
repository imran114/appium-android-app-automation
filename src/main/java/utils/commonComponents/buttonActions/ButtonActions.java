package utils.commonComponents.buttonActions;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.SkipException;
import utils.commonComponents.scrollMethods.ScrollingMethods1;
import locators.AndroidLocatorRegistry;
import utils.fileWriter.PropertiesFileWriter;
import utils.loader_utils.LoaderUtils;
import utils.seleniumUtils.SeleniumUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ButtonActions extends SeleniumUtils {
    private static final String SHOP_GUEST_STATE_FILE_PATH = "src/test/resources/testDataFiles/shop.properties";
    private static final By CONTINUE_AS_GUEST_BUTTON = By.xpath(
            "//*[contains(@text,'Continue as Guest') or contains(@content-desc,'Continue as Guest')]");
    private final By closeButton;
    private final String shopFilePath;
    private final By cancelButton;
    private final By menuButton;
    private final LoaderUtils loaderUtils;
    private final By helpButton;
    private final By homeButton;
    private final By backButtonLocator;
    private final By okButton;
    private final By crossIconBtn;
    private final By menuScreenLocator;
    private final By payNowButton;
    private final By cardScreenCloseButtonLocator;
    private final By managefavoritesButtonLocator;
    private final ScrollingMethods1 scrollingMethods;
    private final By removeNumberButtonLocator;
    private final By lowBalanceMsgLocator;
    private final By dashboardScrollAreaLocator;
    private final By animationsCloseButtonLocator;
    private final By payBillPayNowButtonLocator;

    public ButtonActions(AppiumDriver driver) {
        super(driver);
        loaderUtils = new LoaderUtils(driver);
        scrollingMethods = new ScrollingMethods1(driver);
        shopFilePath = "src/test/resources/testDataFiles/shop.properties";
        closeButton = AndroidLocatorRegistry.closeButton;
        animationsCloseButtonLocator = AndroidLocatorRegistry.closeButton;
        cancelButton = AndroidLocatorRegistry.cancelButton;
        okButton = AndroidLocatorRegistry.okButton;
        crossIconBtn = AndroidLocatorRegistry.crossIconBtn;
        menuButton = AndroidLocatorRegistry.menuButton;
        helpButton = AndroidLocatorRegistry.helpButton;
        dashboardScrollAreaLocator = AndroidLocatorRegistry.dashboardScrollAreaLocator;
        backButtonLocator = AndroidLocatorRegistry.backButtonLocator;
        menuScreenLocator = AndroidLocatorRegistry.menuScreenLocator;
        homeButton = AndroidLocatorRegistry.homeButton;
        payNowButton = AndroidLocatorRegistry.payNowButton;
        cardScreenCloseButtonLocator = AndroidLocatorRegistry.cardScreenCloseButtonLocator;
        managefavoritesButtonLocator = AndroidLocatorRegistry.manageFavoritesButtonLocator;
        removeNumberButtonLocator = AndroidLocatorRegistry.removeNumberButtonLocator;
        payBillPayNowButtonLocator = AndroidLocatorRegistry.payBillPayNowButtonLocator;
        lowBalanceMsgLocator = AndroidLocatorRegistry.lowBalanceMsgLocator;
    }

    Logger log = org.apache.logging.log4j.LogManager.getLogger(ButtonActions.class);

    public void tapOnOkButton() {
        if (isDisplayed(okButton, 2)) {
            clickOnButton(okButton, "Ok");
        }
    }

    public void tapOnContinueButton() {
        if (isDisplayed(continueButton, 5)) {
            click(continueButton);
        }
    }

    public void tapOnCrossIcon() {
        clickOnButton(crossIconBtn, "Cross");
    }

    public String tapOnBackButton() {
        return clickOnButton(backButtonLocator, "Back Button");
    }

    public String tapOnBackButton(By locator) {
        if (isDisplayed(continueButton, 1)) click(continueButton);
        int attempts = 0;
        int maxAttempts = 5;
        boolean isVisible = isDisplayed(locator, 3);
        if (isDisplayed(AndroidLocatorRegistry.logoLocator, 2) || isVisible) {
            return "Pass, back button clicked";
        }

        while (!isVisible && attempts < maxAttempts) {
            click(backButtonLocator);
            loaderUtils.waitForLoader(2, 120);
            if (isPromotionBannerVisible()) {
                System.out.println("Stopped: Banner was visible and closed.");
                break;
            }
            if (isDisplayed(notificationMsgLocator, 1) && returnText(notificationMsgLocator).contains("Error")) {
                String errorMsg = returnText(notificationMsgLocator);
                getFailedElementScreenShot();
                click(continueButton);
                result = String.format("Fail, Error occurred: %s", errorMsg);
            }
            isVisible = isDisplayed(locator, 2);
            attempts++;
        }

        return isDisplayed(locator) ? "Pass, Back button clicked" : "Fail, Back button not found after retries";
    }

    public String tapBackButton(By locator) {
        int attempts = 0;
        int maxAttempts = 15;
        boolean isVisible = isDisplayed(locator, 3);
        System.out.println("isVisible: " + isVisible);
        if (isDisplayed(AndroidLocatorRegistry.logoLocator)) return "Pass, back button clicked";
        // Attempt to locate the element within max attempts
        while (!isVisible && attempts < maxAttempts) {
            System.out.println("tapping Android back button to navigate back: attempt #" + attempts);
            tapBackButton();
            loaderUtils.waitForLoader(2, 120);
            if (isPromotionBannerVisible()) {
                System.out.println("Stopped: Banner was visible and closed.");
                break; // Stop the loop if the banner was handled
            }
            isVisible = isDisplayed(locator, 2);
            attempts++;
        }

        // Return success or failure message
        return isDisplayed(locator) ? "Pass, Back button clicked" : "Fail, Back button not found after retries";
    }

    /**
     * Handles the promotion banner by checking if it is visible and clicking the close button.
     */
    private boolean isPromotionBannerVisible() {
        By promotionImageLocator = By.xpath("//android.widget.ImageView[@content-desc='promotionImage']");
        By closeIconLocator = By.xpath("//android.widget.ImageView[@content-desc='closeIcon']");

        if (isDisplayed(promotionImageLocator, 1)) {
            click(closeIconLocator); // Close the promotion banner
            System.out.println("Banner was visible and closed.");
            return true; // Indicate that the banner was handled
        }
        return false; // No banner was visible
    }

    /*******************
     * General Button Method
     * */
    public String clickOnButton(By locator, String buttonName, boolean hideKeyboard) {
        try {
            if (hideKeyboard) {
                //        hideKeyboard();
            }
            click(locator);
            return "Pass, user is able to click on the " + buttonName;
        } catch (Exception e) {
            return "Fail, user is unable to click on the " + buttonName;
        }
    }


    public String clickOnButton(By locator, String buttonName) {
        try {
            hideKeyboard(locator);
            click(locator);
            return "Pass, user is able to click on the " + buttonName;
        } catch (Exception e) {
            return "Fail, user is unable to click on the " + buttonName;
        }
    }


    public String clickRandomElement(List<By> locators, String buttonName) {
        if (locators == null || locators.isEmpty()) {
            return "Fail, no locators provided for " + buttonName;
        }
        // pick one locator at random
        int idx = ThreadLocalRandom.current().nextInt(locators.size());
        By chosenLocator = locators.get(idx);

        String key = buttonName.toLowerCase().replaceAll("\\s+", "_");
        try {
            // wait until clickable
            waitForClickable(chosenLocator);

            // get a friendly description
            String desc = returnAttribute(chosenLocator, "content-desc");
            if (desc == null || desc.isEmpty()) {
                desc = returnText(chosenLocator).trim();
            }
            if (desc.isEmpty()) {
                desc = chosenLocator.toString();
            }

            // click and record
            click(chosenLocator);
            PropertiesFileWriter.writeProperty(shopFilePath, key, desc);

            return String.format("Pass, clicked %s: %s", buttonName, desc);
        } catch (Exception e) {
            getFailedElementScreenShot();
            return String.format("Fail, unable to click %s (locator #%d: %s): %s",
                    buttonName, idx, chosenLocator, e.getMessage());
        }
    }

    public void tapByCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), x, y));

        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(tap));
    }


    public String clickRandomByElement(By listLocator, String buttonName) {
        String key = buttonName.toLowerCase().replaceAll("\\s+", "_");
        try {

            // 2) fetch them all
            List<WebElement> items = returnWebElements(listLocator);
            if (items.isEmpty()) {
                getFailedElementScreenShot();
                return String.format("Fail, no %s elements found", buttonName);
            }

            // 3) pick one at random
            int idx = ThreadLocalRandom.current().nextInt(items.size());
            WebElement chosen = items.get(idx);

            // 4) read its friendly description
            String desc = chosen.getAttribute("content-desc");
            if (desc.contains("null") || desc.isEmpty()) {
                desc = chosen.getText().trim();
            }
            if (desc.isEmpty()) {
                desc = String.format("%s[%d]", listLocator, idx).trim();
            }

            // 5) click the element
            chosen.click();
            desc = desc.replace("-", "");
            // 6) persist and return success
            PropertiesFileWriter.writeProperty(shopFilePath, key, desc);
            return String.format("Pass, clicked %s: %s", buttonName, desc);

        } catch (Exception e) {
            getFailedElementScreenShot();
            return String.format("Fail, unable to click %s: %s",
                    buttonName, e.getMessage());
        }
    }

    public void retryWhileVisible(By locator) {

        int attempts = 0;
        boolean isVisible = isDisplayed(locator);
        while (isVisible && attempts < 5) {
            click(locator);
            isVisible = isDisplayed(locator);
            attempts++;
        }
    }

    public void clickOnCloseButton() {
        scrollingMethods.performScroll(AndroidLocatorRegistry.logoLocator, dashboardScrollAreaLocator, ScrollingMethods1.Direction.DOWN, 500);
        clickOnButton(animationsCloseButtonLocator, "Close");
    }

    public String checkBackButtonDisplay() {
        return isElementDisplayed(backButtonLocator) ? "Pass, back button displayed" : "Fail, back button not displayed";
    }

    public String checkMenuScreenDisplay() {
        tapOnBackButton(menuScreenLocator);
        return isElementDisplayed(menuScreenLocator) ? "Pass, menu screen is displayed" : "Fail, menu screen is not displayed";
    }


    public String checkOtpScreenDisplay() {
        if (isDisplayed(lowBalanceMsgLocator, 10)) {
            return "Skip, unable to subscribe package because of low balance";
        }
        return "Pass, offer subscribed successfully";
    }


    public void tapOnCloseButtonOnCardErrorMsg() {
        clickOnButton(cardScreenCloseButtonLocator, "Card Screen Close button");
    }


    public String tapOnPayNowButton(String user) {
        if (user.contains("prepaid") || user.contains("postpaid")) {
            return clickOnButton(payNowButton, "Pay Now");
        } else if (user.contains("ptcl")) {
            scrollingMethods.swipeUp(returnWebElement(By.xpath("//android.webkit.WebView")));
            return clickOnButton(payBillPayNowButtonLocator, "Pay Now");
        }
        return null;
    }

    public String tapOnCloseButton() {
        return clickOnButton(closeButton, "Close");
    }

    public String tapOnCancelButton() {
        return clickOnButton(cancelButton, "Cancel");
    }


    public String tapOnMenuButton() {
        if (!isDisplayed(menuScreenLocator, 3)) {
            return clickOnButton(menuButton, "Menu");
        }
        return "Pass, Already on Menu screen";
    }

    public String tapOnManageFavoritesButton() {
        return clickOnButton(managefavoritesButtonLocator, "Manage Favorites");
    }

    public String tapOnRemoveNumberButton() {
        return clickOnButton(removeNumberButtonLocator, "Remove Number");
    }


    public String tapOnHelpButton() {
        boolean isVisible = isDisplayed(helpButton, 10);
        int attempts = 0;
        int maxAttempts = 3;
        while (isVisible && attempts < maxAttempts) {
            result = clickOnButton(helpButton, "Help");
            isVisible = isDisplayed(helpButton, 2);
            attempts++;
        }

        return result;
    }


    public String clickOnContinueAsGuestButton(String user) {
        loaderUtils.waitForLoader(2, 120);
        if (user.contains("guest")) {
            PropertiesFileWriter.writeProperty(SHOP_GUEST_STATE_FILE_PATH, "is_guest_account", "true");
            return clickOnButton(CONTINUE_AS_GUEST_BUTTON, "Continue as Guest button");
        } else throw new SkipException("Shop tab not displayed");
    }


    public void tapOnCloseButtonOfBanner() {
        loaderUtils.waitForLoader(2, 120);
        By billPaymentPromotionImageLocator = By.xpath("//android.widget.ImageView[@content-desc='promotionImage']");
        By closeIconLocator = By.xpath("//android.widget.ImageView[@content-desc='closeIcon']");

        // Check if the promotion image is displayed within the timeout
        if (isDisplayed(billPaymentPromotionImageLocator, 5)) {
            try {
                // Attempt to click the close icon
                click(closeIconLocator);
            } catch (Exception e) {
                log.error("Failed to click on the close icon. Retrying...", e);

            }
        }
    }

    public String tapOnHomeButton() {
        if (isDisplayed(homeButton, 3)) result = clickOnButton(homeButton, "Home");
        loaderUtils.waitForLoader(2, 120);
        tapOnCloseButtonOfBanner();
        return isDisplayed(AndroidLocatorRegistry.logoLocator, 2) ? "Pass, user is able to click on Home button" : "Fail, user not redirected to Dashboard";
    }

}
