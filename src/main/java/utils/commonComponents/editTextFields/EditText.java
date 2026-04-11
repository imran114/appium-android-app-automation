package utils.commonComponents.editTextFields;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import locators.AndroidLocatorRegistry;
import org.testng.SkipException;
import utils.seleniumUtils.SeleniumUtils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditText extends SeleniumUtils {
    private final By cardHolderNameLocator;
    private final By cardHolderNumberLocator;
    private final By cardCvvLocator;
    private final By cardExpiryMonthLocator;
    private final By cardExpiryYearLocator;
    private final By payBillCardHolderNumberLocator;
    private final By payBillCardExpiryYearLocator;
    private final By payBillCardYearDropDownLocator;
    private final By payBillCardCvvLocator;
    private final By cardErrorMsgLocator;
    private final By cardErrorMsg2Locator;
    private final String deviceInfoFilePath = "src/test/resources/testDataFiles/device_info.properties";
    private static final String REAL_DEVICE_KEY = "isRealDevice";

    public EditText(AppiumDriver driver) {
        super(driver);
        cardHolderNameLocator = AndroidLocatorRegistry.cardHolderNameLocator;
        cardHolderNumberLocator = AndroidLocatorRegistry.cardHolderNumberLocator;
        cardExpiryMonthLocator = AndroidLocatorRegistry.cardExpiryMonthLocator;
        cardExpiryYearLocator = AndroidLocatorRegistry.cardExpiryYearLocator;
        cardCvvLocator = AndroidLocatorRegistry.cardCvvLocator;
        payBillCardHolderNumberLocator = AndroidLocatorRegistry.payBillCardHolderNumberLocator;
        payBillCardExpiryYearLocator = AndroidLocatorRegistry.payBillCardExpiryYearLocator;
        payBillCardYearDropDownLocator = AndroidLocatorRegistry.payBillCardYearDropDownLocator;
        payBillCardCvvLocator = AndroidLocatorRegistry.payBillCardCvvLocator;
        cardErrorMsgLocator = AndroidLocatorRegistry.cardErrorMsgLocator;
        cardErrorMsg2Locator = AndroidLocatorRegistry.cardErrorMsg2Locator;
    }

    public String enterText(By locator, String valueToEnter, String textBoxName) {
        try {
            hideKeyboard(locator);
            clearTextField(locator);
            sendKeys(locator, valueToEnter);
            return "Pass, user is able to enter the text " + valueToEnter + " in the " + textBoxName;
        } catch (Exception e) {
            return "Fail, user is unable to enter the text " + valueToEnter + " in the " + textBoxName;
        }
    }



    public String checkInputField(By locator) {
        try {
            WebElement inputField = returnWebElement(locator);
            String text = inputField.getText();
            Pattern specialCharPattern = Pattern.compile("[!\\-\\/:-@\\[-`\\{-~]");
            Matcher specialCharMatcher = specialCharPattern.matcher(text);
            boolean containsSpecialCharacters = specialCharMatcher.find();

            Pattern spacePattern = Pattern.compile("\\s");
            Matcher spaceMatcher = spacePattern.matcher(text);
            boolean containsSpaces = spaceMatcher.find();

            Pattern numberPattern = Pattern.compile("[0-9]");
            Matcher numberMatcher = numberPattern.matcher(text);
            boolean containNumbers = numberMatcher.find();

            Pattern alphabetPattern = Pattern.compile("[A-Za-z]");
            Matcher alphabetMatcher = alphabetPattern.matcher(text);
            boolean containAlphabets = alphabetMatcher.find();

            if (containsSpecialCharacters) {
                result = "Fail, user is able to enter special characters in the input field";
            } else if (containsSpaces) {
                result = "Fail, user is able to enter spaces in the input field";
            } else if (containNumbers) {
                result = "Fail, user is able to enter numbers in the input field";
            } else if (containAlphabets) {
                result = "Fail, user is able to enter alphabets in the input field";
            } else {
                result = "Pass, input field meets the required criteria";
            }
        } catch (Exception e) {
            result = "Error, Unable to check input field";
        }

        return result;
    }

    public String enterCardHolderName(String user) {
//        loaderUtils.waitForLoader(2,120);
        if (!user.contains("ptcl") && isDisplayed(cardHolderNameLocator, 30)) {
            result = enterText(cardHolderNameLocator, "name", "Card Holder Name");
        } else throw new SkipException("Skipping");

        return result;
    }

    public String enterCardCvv(String user) {
        if (user.contains("postpaid") || user.contains("prepaid")) {
            return enterText(cardCvvLocator, "asd", "Card Cvv");
        } else {
            scrollingMethods.swipeUp(returnWebElement(By.xpath("//android.webkit.WebView")));
            return enterText(payBillCardCvvLocator, "ewrt", "Card Cvv");
        }
    }

    public String enterCardHolderNumber(String number, String user) {
        if (user.contains("postpaid") || user.contains("prepaid")) {
            result = enterText(cardHolderNumberLocator, number, "Card Number");
        } else if (user.contains("ptcl")) {
            result = isDisplayed(payBillCardHolderNumberLocator, 30)
                    ? enterText(payBillCardHolderNumberLocator, number, "Card Number")
                    : "Fail, unable to enter card holder number";
        } else {
            result = "Fail, invalid user type";
        }
        return result;
    }

    public String enterCardExpiryMonth(String user) {
        int maxAttempts = 4;
        int count = 0;
        if (user.contains("postpaid") || user.contains("prepaid")) {
            boolean isVisible = isElementDisplayed(cardExpiryMonthLocator);
            while (!isVisible && count < maxAttempts) {
                tapBackButton();
                click(By.xpath("(//android.view.View)[contains(@content-desc,'RECHARGE_SCREEN_btn_recharge') or contains(@content-desc,'Recharge')]"));
                isVisible = isElementDisplayed(cardExpiryMonthLocator);
                count++;
            }
            return enterText(cardExpiryMonthLocator, "er", "Card Expiry Month");
        } else {
            throw new SkipException("Skipping");
        }
    }

    public String enterCardExpiryYear(String user) {
        if (user.contains("postpaid") || user.contains("prepaid")) {
            return enterText(cardExpiryYearLocator, "as", "Card Expiry Month");
        } else
//            click(payBillCardYearDropDownLocator);
//            click(payBillCardExpiryYearLocator);
            throw new SkipException("Skipping");
    }


    public String enterTextBoxValueWithRetry(By locator, String valueToEnter, String textBoxName) {
        int repeat = 0; // Counter to limit retries
        int maxRetries = 5; // Maximum retry attempts

        while (repeat <= maxRetries) {
            try {
                // Clear the field before entering the text
                clearTextField(locator);
                // Enter the value
                sendKeys(locator, valueToEnter);
                return "Pass, user is able to enter the text '" + valueToEnter + "' in the " + textBoxName;
            } catch (StaleElementReferenceException exc) {
                System.out.println("Attempt " + (repeat + 1) + " failed due to StaleElementReferenceException.");
                exc.printStackTrace();
            } catch (Exception e) {
                System.out.println("Attempt " + (repeat + 1) + " failed due to an unexpected exception.");
                e.getMessage();
            }

            repeat++; // Increment the counter
            try {
                Thread.sleep(500); // Add a small delay before retrying
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }

        // If all retries fail
        return "Fail, user is unable to enter the text '" + valueToEnter + "' in the " + textBoxName;
    }

}
