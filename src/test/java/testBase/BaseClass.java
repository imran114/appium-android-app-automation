package testBase;

import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import utilities.email.EmailBody;
import utilities.email.EmailTableSender;
import utilities.file_reader.PropertiesFileReader;
import utilities.reporter.ExtentReport;

import java.util.Map;


public class BaseClass {

    protected static PropertiesFileReader propertiesFileReader;
    public static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    @Getter
    public static String deviceName;
    public static EmailBody emailBody;
    @Getter
    public static String env;
    public String platformVersion;
    public String udid;
    protected static String language = "";
    private static final ThreadLocal<String> loginID = new ThreadLocal<>();
    public static ExtentReport extentReport;
    private static final Logger logger = LogManager.getLogger(BaseClass.class);
    private DriverManager driverManager;

    // ----------------------------------------------------------------------------
    // start one Appium session for the entire suite
    // ----------------------------------------------------------------------------
    @BeforeTest(alwaysRun = true)
    public void setUpSuite(ITestContext context) {
//        StartAppiumWithPlugin.startAppiumDeviceFarmServer();
        logger.info("Setting up test suite…");
        propertiesFileReader = new PropertiesFileReader();
        emailBody = new EmailBody();
        EmailBody.clearEmailBody();
        // 1) Read TestNG parameters
        Map<String, String> params = context.getCurrentXmlTest().getAllParameters();
        env = params.get("env");
        deviceName = params.get("deviceName");
        platformVersion = params.get("platformVersion");
        udid = params.get("udid");
        language = params.get("language");
        loginID.set(params.get("loginID"));
        System.out.println("Login user type: " + loginID);
        logger.info("📦 TestNG Params → UDID: {}, Version: {}", udid, platformVersion);
//        PropertiesFileWriter.writeProperty("src/test/resources/testDataFiles/device_info.properties", "udid", udid);
        driverManager = new DriverManager();
        driver.set(driverManager.getDriver(env, udid));
        getDriver();
    }

    @BeforeSuite
    public void beforeSuite() {
        // 2) Initialize Extent report
        extentReport = new ExtentReport();
        extentReport.createReport();
    }

    // ----------------------------------------------------------------------------
    // quit that one session only after all tests
    // ----------------------------------------------------------------------------
    @AfterTest
    public void tearDownSuite() {
        logger.info("Tearing down test suite…");
        driverManager.quitDriver();
        if (udid != null && !udid.isBlank()) {
//            StartAppiumWithPlugin.stopAppiumServer(udid);
//            PortAllocator.release("systemPort:" + udid);
        }
        driver.remove();
    }

    @AfterSuite
    public void afterSuite() {
        extentReport.flushReport();
        logger.info("🔖 Extent report flushed.");
        // get current user name
        String osType = System.getProperty("os.name").toLowerCase();
//        EmailTableSender tableSender = new EmailTableSender(getDriver(), extentReport);
//        if (osType.contains("windows")) {
//            tableSender.sendEmail();
//        }
    }


    public AppiumDriver getDriver() {
        return driver.get();
    }

    public ExtentReport getExtentReport() {
        return extentReport;
    }

    public String getLanguage() {
        return language;
    }

    public String getLoginID() {
        return loginID.get();
    }

}
