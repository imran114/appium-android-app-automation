package pages.espresso;

import io.appium.java_client.AppiumDriver;
import utils.commonComponents.buttonActions.ButtonActions;
import utils.fileWriter.PropertiesFileWriter;
import utils.file_reader.PropertiesFileReader;
import utils.seleniumUtils.SeleniumUtils;

/**
 * Page object for the Basic Espresso sample app: input, persist text, change label, assert.
 */
public class EspressoPage extends SeleniumUtils {

    private static final String ESPRESSO_SAMPLE_PROPS = "src/test/resources/testDataFiles/espresso_sample.properties";
    private static final String KEY_LAST_ENTERED_TEXT = "lastEnteredText";

    private final ButtonActions buttonActions;

    public EspressoPage(AppiumDriver driver) {
        super(driver);
        buttonActions = new ButtonActions(driver);
    }

    public String enterTextInSampleInput(String text) {
        try {
            if (!waitForVisibility(EspressoLocators.inputField, 15)) {
                return "Fail, sample input field is not visible";
            }
            String cleared = clearTextField(EspressoLocators.inputField);
            if (cleared.startsWith("Fail")) {
                return cleared;
            }
            sendKeys(EspressoLocators.inputField, text);
            return "Pass, user entered text in the sample input field";
        } catch (Exception e) {
            return "Fail, unable to enter text in sample input: " + e.getMessage();
        }
    }

    public String persistEnteredTextToPropertyFile(String text) {
        try {
            PropertiesFileWriter.writeProperty(ESPRESSO_SAMPLE_PROPS, KEY_LAST_ENTERED_TEXT, text);
            return "Pass, entered text saved to property file";
        } catch (Exception e) {
            return "Fail, unable to save text to property file: " + e.getMessage();
        }
    }

    public String verifyPersistedTextInPropertyFile(String expectedText) {
        String absolutePath = PropertiesFileReader.returnFilePath(
                "/src/test/resources/testDataFiles/espresso_sample.properties");
        String actual = PropertiesFileReader.readProperty(absolutePath, KEY_LAST_ENTERED_TEXT);
        if (actual == null) {
            return "Fail, property key not found or file unreadable";
        }
        if (expectedText.equals(actual)) {
            return "Pass, property file contains the expected entered text";
        }
        return "Fail, expected property value '" + expectedText + "' but was '" + actual + "'";
    }

    public String tapChangeTextButton() {
        return buttonActions.clickOnButton(EspressoLocators.changeTextButton, "Change Text");
    }

    public String verifyStatusTextDisplays(String expectedText) {
        if (!waitForVisibility(EspressoLocators.displayText, 15)) {
            return "Fail, status text view is not visible";
        }
        String actual = returnText(EspressoLocators.displayText);
        if (actual == null) {
            return "Fail, could not read status text";
        }
        String trimmed = actual.trim();
        if (expectedText.equals(trimmed)) {
            return "Pass, displayed text matches the entered text";
        }
        return "Fail, expected displayed text '" + expectedText + "' but found '" + trimmed + "'";
    }

    /**
     * End-to-end: type, save to properties, tap Change Text, assert label updated.
     */
    public String enterSaveTapAndVerifyDisplayedText(String text) {
        String step = enterTextInSampleInput(text);
        if (!step.startsWith("Pass")) {
            return step;
        }
        step = persistEnteredTextToPropertyFile(text);
        if (!step.startsWith("Pass")) {
            return step;
        }
        step = tapChangeTextButton();
        if (!step.startsWith("Pass")) {
            return step;
        }
        return verifyStatusTextDisplays(text);
    }
}
