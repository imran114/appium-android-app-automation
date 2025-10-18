package tests.logout;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.logout.LogoutPage;
import testBase.PageObjects;
import utilities.test_validator.TestValidator;


public class LogoutTest extends PageObjects {

    private LogoutPage logoutPage;

    @BeforeClass
    public void beforeClass() {
        useCaseName = "Logout Test";
        parentTest(useCaseName, "Testcase of " + useCaseName);
        logoutPage = new LogoutPage(getDriver(), platform);
        emailBody.enterTextToEmailBody("\n " + "- " + useCaseName + "\n");
    }

    @Test(priority = 10)
    public void clickOnProfileIconTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "User should be able to click on profile icon");
        extentReport.testInfo("Clicking on profile icon");
        actualResult = logoutPage.clickOnProfileIcon();
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 12)
    public void clickOnMenuIconTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "User should be able to click on menu icon");
        extentReport.testInfo("Clicking on menu icon");
        actualResult = logoutPage.clickOnMenuIcon();
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }

    @Test(priority = 13)
    public void clickOnLogoutButtonTest() {
        extentReport.logChildTestNameAndDescription(useCaseName, "User should be able to click on logout button");
        extentReport.testInfo("Clicking on logout button");
        actualResult = logoutPage.clickOnLogoutButton();
        extentReport.logStepResult(actualResult);
        TestValidator.validateTest(actualResult);
    }


}
