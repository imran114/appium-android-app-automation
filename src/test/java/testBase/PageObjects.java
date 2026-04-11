package testBase;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utilities.file_reader.PropertiesFileReader;
import utilities.screen_recording_utils.ScreenRecordingUtils;
import utils.app_manager.AppManager;
import utils.commonComponents.buttonActions.ButtonActions;

// Base for test classes: shared hooks and helpers.
public class PageObjects extends BaseClass {
    public String userType;
    protected PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
    protected ButtonActions buttonActions;

    protected String actualResult = "";
    protected String expectedResult = "";
    protected String useCaseName = "";

    @BeforeClass
    public void getBeforeClassParams(ITestContext context) {
        extentReport = getExtentReport();
        buttonActions = new ButtonActions(getDriver());
        userType = getLoginID();
//        ensureAppReady();
//        ScreenRecordingUtils.startRecording(getDriver(), userType);
    }

    private void ensureAppReady() {
        AppManager.ensureAppIsOpen(getDriver());
    }

    @AfterClass
    public void saveVideo() {
        ScreenRecordingUtils.stopAndSaveRecording(getDriver(), this.getClass().getSimpleName(), userType);
    }

    protected void parentTest(String useCaseName, String description) {
        extentReport.createTest(useCaseName, description);
    }
}
