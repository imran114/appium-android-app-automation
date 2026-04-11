package tests.espresso;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.espresso.EspressoPage;
import testBase.PageObjects;
import utilities.test_validator.TestValidator;

public class EspressoTest extends PageObjects {

    private EspressoPage espressoSamplePage;

    @BeforeClass
    public void beforeClass() {
        useCaseName = "Basic Espresso Sample";
        parentTest(useCaseName, "UI actions on Basic Espresso sample screen");
        espressoSamplePage = new EspressoPage(getDriver());
        emailBody.enterTextToEmailBody("\n " + "- " + useCaseName + "\n");
    }

    @Test(priority = 10)
    public void enterTextInSampleInputFieldTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "User should be able to enter text in the sample input");
        extentReport.testInfo("Entering text in type-something field");
        actualResult = espressoSamplePage.enterTextInSampleInput("HelloFromTest");
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 11)
    public void persistEnteredTextToPropertyFileTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "Entered text should be saved to the property file");
        extentReport.testInfo("Writing lastEnteredText to espresso_sample.properties");
        String valueToSave = "PersistedLine_" + System.currentTimeMillis();
        actualResult = espressoSamplePage.persistEnteredTextToPropertyFile(valueToSave);
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 12)
    public void verifyPersistedTextInPropertyFileTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "Property file should match the last saved text");
        extentReport.testInfo("Reading back lastEnteredText from properties");
        String valueToSave = "VerifyProp_" + System.currentTimeMillis();
        espressoSamplePage.persistEnteredTextToPropertyFile(valueToSave);
        actualResult = espressoSamplePage.verifyPersistedTextInPropertyFile(valueToSave);
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 13)
    public void tapChangeTextButtonTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "User should be able to tap Change Text");
        extentReport.testInfo("Enter text then tap CHANGE TEXT");
        actualResult = espressoSamplePage.enterTextInSampleInput("TapFlowText");
        if (actualResult.startsWith("Pass")) {
            actualResult = espressoSamplePage.tapChangeTextButton();
        }
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 14)
    public void verifyDisplayedTextMatchesEnteredAfterChangeTest() {
        extentReport.logChildTestNameAndDescription(useCaseName,
                "Label should change from Hello Espresso! to the text entered after Change Text");
        extentReport.testInfo("Enter, save to properties, tap Change Text, verify label");
        String custom = "MyEspressoText_" + System.currentTimeMillis();
        actualResult = espressoSamplePage.enterSaveTapAndVerifyDisplayedText(custom);
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }
}
