package utilities.reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.file_reader.PropertiesFileReader;
import utilities.find_apk.FindAPKFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentReport {

    private static final Logger logger = LogManager.getLogger(ExtentReport.class);

    public ExtentReports getExtentReports() {
        return extentReports;
    }

    private final ExtentReports extentReports;
    private final Map<String, ExtentTest> parentTestsMap = new ConcurrentHashMap<>();
    private static final ThreadLocal<ExtentTest> TL_TEST = new ThreadLocal<>();

    public ExtentReport() {
        extentReports = new ExtentReports();
    }

    // Creates the report (call once, e.g., @BeforeSuite)
    public void createReport() {
        String cssFilePath = "/extentReport.css";
        String jsFilePath = "/reportConfigJs.js";
        String cssContent = "";
        String jsContent = "";

        try {
            cssContent = new String(Files.readAllBytes(Paths.get(PropertiesFileReader.returnFilePath(cssFilePath))));
            jsContent = new String(Files.readAllBytes(Paths.get(PropertiesFileReader.returnFilePath(jsFilePath))));
        } catch (IOException e) {
            System.out.println("In Extent report");
            System.out.println(e.getMessage());
        }

        String projectDir = System.getProperty("user.dir");
        String relativeReports = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "reports";
        String reportsDir = projectDir + File.separator + relativeReports;

        System.out.println(">>> [DEBUG] Project dir:      " + projectDir);
        System.out.println(">>> [DEBUG] Reports directory: " + reportsDir);

        File dir = new File(reportsDir);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println(">>> [ERROR] Could NOT create reports dir. Check permissions!");
        }

        deleteExistingReports();

        String timestamp = new SimpleDateFormat("d_MMMM_yyyy").format(new Date());
        String appReportPrefix = FindAPKFiles.getReportFileNamePrefix();
        String reportFile = appReportPrefix + "_" + timestamp + ".html";
        String fullPath = reportsDir + File.separator + reportFile;
        System.out.println(">>> [DEBUG] Full report path:   " + fullPath);

        String documentTitle = capitalizeWord(appReportPrefix) + " — Android automation report";
        try {
            ExtentSparkReporter spark = new ExtentSparkReporter(fullPath);
            spark.config().setDocumentTitle(documentTitle);
            spark.config().setTheme(Theme.DARK);
            spark.config().setCss(cssContent);
            spark.config().setJs(jsContent);
            extentReports.attachReporter(spark);
        } catch (Exception e) {
            System.err.println(">>> [ERROR] Failed to initialize ExtentSparkReporter:");
            e.getMessage();
        }
    }

    private static String capitalizeWord(String s) {
        if (s == null || s.isEmpty()) {
            return "App";
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // Create (or get) a parent test safely; do NOT bind it to the thread
    public ExtentTest createTest(String testName, String testDescription) {
        ExtentTest parent = parentTestsMap.computeIfAbsent(testName, k ->
                extentReports.createTest(testName, testDescription)
                        .assignCategory(testName)
                        .assignDevice("Android")
        );
        System.out.println("This is parent test map " + parentTestsMap);
        return parent;
    }

    /**
     * Bind a child node to the current thread.
     * Call this at @BeforeMethod (or wherever you start an individual test method),
     * passing the parent test name and the child test (e.g., method) name.
     */
    public void logChildTestNameAndDescription(String parentTestName, String childTestName) {
        ExtentTest parent = parentTestsMap.computeIfAbsent(parentTestName, k ->
                extentReports.createTest(parentTestName, parentTestName + " - Auto created")
                        .assignCategory(parentTestName)
                        .assignDevice("Android")
        );

        ExtentTest child = parent.createNode(childTestName).assignCategory(parentTestName);
        TL_TEST.set(child); // <-- CRITICAL for parallel: bind node to thread
    }

    private void deleteExistingReports() {
        String reportsDirectory = "src/test/resources/reports/";
        File reportsDir = new File(reportsDirectory);
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            File[] files = reportsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".html")) {
                        if (file.delete()) {
                            System.out.println("Deleted old report: " + file.getName());
                        }
                    }
                }
            }
        }
    }

    // --- Logging helpers (thread-safe via ThreadLocal) ---

    private ExtentTest getCurrent() {
        ExtentTest t = TL_TEST.get();
        if (t == null) {
            // Defensive; helps catch missing bindings in parallel runs
            throw new IllegalStateException(
                    "No ExtentTest bound to current thread. " +
                            "Call logChildTestNameAndDescription(...) before logging steps."
            );
        }
        return t;
    }

    public void testPass(String passInfo) {
        getCurrent().pass(passInfo);
    }

    public void testFail(String failInfo) {
        try {
            String screenshotPath = PropertiesFileReader.returnFilePath("/src/main/resources/screenShots/screenShot.png");
            try (InputStream in = new FileInputStream(screenshotPath)) {
                byte[] imageBytes = IOUtils.toByteArray(in);
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                // Use the correct API for base64:
                getCurrent().fail(
                        failInfo,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build()
                );
            }
        } catch (IOException e) {
            logger.error("Error while adding screenshot for failed test", e);
            getCurrent().fail(failInfo);
        }
    }

    public void testSkip(String reasonToSkip) {
        getCurrent().skip(reasonToSkip);
    }

    public void testInfo(String testInfo) {
        getCurrent().info(testInfo);
    }

    public void logStepResult(String stepResult) {
        if (stepResult == null || stepResult.isEmpty()) {
            testInfo("No result provided");
            return;
        }
        String status = stepResult.split(",")[0].trim();
        switch (status) {
            case "Pass":
                testPass(stepResult);
                break;
            case "Fail":
                testFail(stepResult);
                break;
            case "Skip":
                testSkip(stepResult);
                break;
            default:
                testInfo(stepResult);
        }
    }


    public void flushReport() {
        extentReports.flush();

        // Optional: if you really have a single shared screenshot path, deleting it here
        // can cause races when tests fail in parallel. Prefer unique filenames per test.
        String screenshotPath = PropertiesFileReader.returnFilePath("/src/main/resources/screenShots/screenShot.png");
        File screenshotFile = new File(screenshotPath);
        if (screenshotFile.exists() && !screenshotFile.delete()) {
            logger.warn("Failed to delete the screenshot file (may be in use by another test).");
        }

    }

}