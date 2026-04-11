package utils.seleniumUtils;

import com.google.common.io.Files;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.bounds.MobileElementAttributes;
import utils.commonComponents.scrollMethods.ScrollingMethods1;
import utils.file_reader.PropertiesFileReader;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static utils.file_reader.PropertiesFileReader.returnFilePath;


public class SeleniumUtils {
    protected String result;
    private WebDriverWait wait;
    public final By menuScreenLocator = By.xpath("(//android.view.View)/android.widget.TextView[contains(@text,'Menu') or contains(@text,'مینو')]");
    public final By rechargeButton = By.xpath("//android.view.View[contains(@content-desc,'Recharge') or contains(@text,'Ø±ÛÚØ§Ø±Ø¬')]");
    protected final By logoLocator = By.xpath("//android.widget.ImageView[@content-desc='Logo']");
    protected final By notificationMsgLocator = By.xpath("//android.widget.TextView[@content-desc='showMessage']");
    protected final By continueButton = By.xpath("//android.view.View[@content-desc=\"Continue\"]");
    protected PropertiesFileReader propertiesFileReader;
    protected final ScrollingMethods1 scrollingMethods;
    public static final Logger logger = LogManager.getLogger(SeleniumUtils.class);
    protected AppiumDriver driver;
    protected AndroidDriver androidDriver;

    Logger log = LogManager.getLogger(SeleniumUtils.class);

    public SeleniumUtils(AppiumDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        scrollingMethods = new ScrollingMethods1(driver);
        propertiesFileReader = new PropertiesFileReader();
        androidDriver = (AndroidDriver) driver;
    }

    public String clearTextField(By locator) {
        try {
            WebElement element = returnWebElement(locator);
            element.click();
            element.clear();
        } catch (Exception e) {
            logger.error("Element not found. Cannot perform click.", e);
            e.getMessage();
            System.out.println(e.getMessage());
            return "Fail, unable to clear text field: " + e.getMessage();
        }
        return "Pass, Text field cleared";
    }


    private boolean isKeyboardVisible() {
        return ((AndroidDriver) driver).isKeyboardShown();

    }


    public void click(By locator) {
        hideKeyboard(locator);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            logger.error("Click failed on locator: {}", locator, e);
            getFailedElementScreenShot();
        }
    }


    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
    }


    public void click(WebElement locator) {
        try {
            waitForClickable(locator);
            locator.click();
        } catch (Exception e) {
            logger.error("Element not found. Cannot perform click.", e);
            System.out.println("Element not found. Cannot perform click.");

        }
    }


    public void sendKeys(By locator, String text) {
        try {
//            hideKeyboard(locator);
            WebElement element = returnWebElement(locator);
            element.click();
//            hideKeyboardViaNavigateBack(locator);
            element.sendKeys(text);
        } catch (Exception e) {
            logger.error("Element not found. Cannot perform sendKeys.", e);

        }
    }

    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isDisplayed(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            localWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
        // Return false if all attempts fail
    }


    public boolean waitForVisibility(By locator, int seconds) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isDisplayed(WebElement locator) {
        try {
            return locator.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    public String returnAttribute(By locator, String attribute) {
        try {
            WebElement element = returnWebElement(locator);
            return element.getAttribute(attribute);
        } catch (NoSuchElementException e) {
            logger.error("Element not found. Cannot return attribute.", e);
            return "Element not found. Cannot return attribute." + e;
        }
    }

    public String returnAttribute(By locator, MobileElementAttributes attribute) {
        try {
            WebElement element = returnWebElement(locator);
            return element.getAttribute(attribute.getName());
        } catch (NoSuchElementException e) {
            logger.error("Element not found. Cannot return attribute.", e);
            return "Element not found. Cannot return attribute." + e;
        }
    }

    public String returnText(By locator) {
        try {
            WebElement element = returnWebElement(locator);
            return element.getText();
        } catch (NoSuchElementException e) {
            logger.error("Element not found. Cannot return text.", e);
            return "Element not found. Cannot return text." + e;
        }
    }

    public String returnContentDesc(By locator) {
        WebElement element = returnWebElement(locator, 3);
        return element.getAttribute("content-desc");
    }


    public WebElement returnWebElement(By locator) {
        try {
            waitForVisibility(locator);
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            getFailedElementScreenShot();
            logger.error("Element not found", e);
            System.out.println("Element not found" + e);
            return null;
        }

    }

    public WebElement returnWebElement(By locator, int seconds) {
        try {
            waitForVisibility(locator, seconds);
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            getFailedElementScreenShot();
            logger.error("Element not found.", e);
            return null;
        }
    }

    public List<WebElement> returnWebElements(By locator) {
        try {
            waitForVisibility(locator);
            return driver.findElements(locator);
        } catch (NoSuchElementException e) {
            getFailedElementScreenShot();
            logger.error("Element not found.", e);
            return null;
        }
    }

    public List<By> returnByElements(By locator) {
        try {
            waitForVisibility(locator);
            return List.of(locator);
        } catch (NoSuchElementException e) {
            getFailedElementScreenShot();
            logger.error("Element not found.", e);
            return null;
        }
    }

    public void getFailedElementScreenShot() {
        String fileName = "screenShot.png";
        String pathToSaveFile = returnFilePath("/src/main/resources/screenShots/") + fileName;
        try {
            var getScreenShot = (TakesScreenshot) driver;
            File screenShot = getScreenShot.getScreenshotAs(OutputType.FILE);
            Files.move(screenShot, new File(pathToSaveFile));
        } catch (Exception exception) {
            logger.error("Failed to capture screenshot", exception);
        }
    }


    public List<WebElement> returnWebElements(List<By> locators) {
        return locators.stream()
                .flatMap(loc -> driver.findElements(loc).stream())
                .collect(Collectors.toList());
    }


    public String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void waitForClickable(By locator) {
        try {
            waitForVisibility(locator);
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            getFailedElementScreenShot();
            logger.error("Element not clickable after waiting.", e);

        }
    }

    public void waitForClickable(WebElement locator) {
        try {
            waitForVisibility(locator);
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            getFailedElementScreenShot();
            logger.error("Element not clickable after waiting.", e);

        }
    }

    public void waitForVisibility(By locator) {
        try {
//            hideKeyboardViaNavigateBack(locator);
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            getFailedElementScreenShot();
//            log.error("Element not visible after waiting.", e);
            throw e;
        }
    }


    public void waitForVisibility(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            getFailedElementScreenShot();
            log.error("Element not visible after waiting.", e);
            throw e;
        }
    }


    public String scrollUp(By targetElement, By scrollAreaElement, int pointerMoveDuration, int seconds) {

        try {
            performScroll(targetElement, scrollAreaElement, ScrollingMethods1.Direction.UP);
            if (isElementDisplayed(targetElement)) {
                return "Pass, scrolled up";
            } else {
                return "Fail, unable to scroll up";
            }
        } catch (Exception e) {
            return "Fail, exception occurred while scrolling up: " + e.getMessage();
        }
    }


    public void scrollLeft(By targetElement, By scrollAreaLocator, int pointerMoveDuration) {
        try {
            performScroll(targetElement, scrollAreaLocator, ScrollingMethods1.Direction.LEFT);
            isDisplayed(targetElement, 5);
        } catch (Exception ignored) {
        }
    }

    public String scrollRight(By targetElement, By scrollAreaLocator, int pointerMoveDuration) {
        try {
            performScroll(targetElement, scrollAreaLocator, ScrollingMethods1.Direction.RIGHT);
            return isElementDisplayed(targetElement) ? "Pass, scrolled right" : "Fail, unable to scroll right";
        } catch (Exception e) {
            return "Fail, exception occurred while scrolling up: " + e.getMessage();
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            hideKeyboard(locator);
            waitForVisibility(locator);
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    private void performScroll(By targetElement, By scrollAreaLocator, ScrollingMethods1.Direction direction) {
        waitForVisibility(scrollAreaLocator);
        int attempts = 0;
        boolean isVisible = isDisplayed(targetElement, 1);
        int maxScrollAttempts = 8;
        while (!isVisible && attempts < maxScrollAttempts) {
            scrollingMethods.swipe(returnWebElement(scrollAreaLocator), direction, 500);
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }

    }


    public void tapBackButton() {
        driver.navigate().back();
    }

    public void tapBackButtonToCloseKeyBoard() {
        if (androidDriver.isKeyboardShown()) {
            driver.navigate().back();
        }
    }

    public boolean isInvisible(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            // Wait for the element to become invisible
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            // Log the exception and return false without throwing an exception
            System.out.println("Element did not become invisible within " + 2 + " seconds.");
            return false;
        }
    }

    public void waitForElementInvisibility(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        try {
            // Wait for the element to become invisible
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            // Log the exception and return false without throwing an exception
            System.out.println("Element did not become invisible within " + timeoutInSeconds + " seconds.");
        }
    }


    public boolean isRealDevice() {
        String deviceInfoFilePath = "src/test/resources/testDataFiles/device_info.properties";
        return PropertiesFileReader.readProperty(deviceInfoFilePath, "isRealDevice").equalsIgnoreCase("true");
    }


    public void hideKeyboardViaNavigateBack(By locator) {
        AndroidDriver android = (AndroidDriver) driver;
        if (isKeyboardVisible() && !isDisplayed(locator, 2)) {
//            android.hideKeyboard();
            System.out.println("keyboard detected as " + isKeyboardVisible() + " using android.isKeyboardVisible()");
            System.out.println("Attempting to hide it using navigate back --->  android.navigate().back();");

            android.navigate().back();
        }
    }

    public void hideKeyboardViaNavigateBack() {
        AndroidDriver android = (AndroidDriver) driver;
        if (isKeyboardVisible()) {
            System.out.println("keyboard detected as " + isKeyboardVisible() + " using android.isKeyboardVisible()");
            System.out.println("Attempting to hide it using navigate back --->  android.navigate().back();");
            android.navigate().back();
        }
    }

    public void hideKeyboard(By locator) {
        boolean isLocatorAccessible = isDisplayed(locator, 2);
//        System.out.println("In public void hideKeyboard(By locator) method");
        AndroidDriver android = (AndroidDriver) driver;
//        System.out.println("Is locator accessible: " + isLocatorAccessible);
//        System.out.println("Is keyboard visible: " + isKeyboardVisible());
        if (!isLocatorAccessible && isKeyboardVisible()) {
            System.out.println("keyboard detected as " + isKeyboardVisible() + " using android.isKeyboardVisible()");
            android.hideKeyboard();
        }
    }

    public void hideKeyboard() {
        AndroidDriver android = (AndroidDriver) driver;
        try {
            System.out.println("In hideKeyboard method before android.hideKeyboard();");
            if (isKeyboardVisible()) {
                System.out.println("keyboard detected as OPEN    using android.isKeyboardVisible()");
                android.hideKeyboard();
                System.out.println("keyboard hidden using android.hideKeyboard()");
            }
        } catch (WebDriverException e) {
            // Ignore only if emulator/keyboard issue
            if (e.getMessage().contains("cannot be hidden")) {
                System.out.println("Keyboard not present or cannot be hidden in emulator.");
            } else {
                throw e; // rethrow for other unexpected issues
            }
        }
    }


    public boolean verifyVideoIsPlaying(By videoLocator) {
        WebElement videoElement = returnWebElement(videoLocator);
        if (videoElement == null) {
            logger.error("Video element not found.");
            return false;
        }

        try {
            FluentWait<WebElement> fluentWait = new FluentWait<>(videoElement)
                    .withTimeout(Duration.ofSeconds(15))       // Max wait time
                    .pollingEvery(Duration.ofSeconds(1))       // Polling interval
                    .ignoring(Exception.class);                // Ignore transient errors

            return fluentWait.until(new Function<WebElement, Boolean>() {
                public Boolean apply(WebElement element) {
                    File screenshot1 = takeScreenshot(element);
                    sleep(1000); // Delay between frames to detect motion
                    File screenshot2 = takeScreenshot(element);
                    return compareScreenshots(screenshot1, screenshot2); // true if they are different
                }
            });

        } catch (TimeoutException e) {
            logger.warn("Video did not start playing within expected time.");
            return false;
        }
    }


    // Helper method to take a screenshot of a WebElement
    private File takeScreenshot(WebElement element) {
        return element.getScreenshotAs(OutputType.FILE);
    }

    // Helper method to compare two screenshots
    private boolean compareScreenshots(File screenshot1, File screenshot2) {
        try {
            return !FileUtils.contentEquals(screenshot1, screenshot2);
        } catch (IOException e) {
            throw new RuntimeException("Error comparing screenshots", e);
        }
    }


    /**
     * Finds the first element matching the given content-descs (case-insensitive).
     * Only useful for Mobile (Appium).
     */
    public WebElement findByContentDesc(By baseLocator, String... descs) {
        List<WebElement> elements = driver.findElements(baseLocator);
        for (WebElement el : elements) {
            String contentDesc = null;
            try {
                contentDesc = el.getAttribute("content-desc");
            } catch (Exception ignored) {
            }
            if (contentDesc == null) continue;
            for (String desc : descs) {
                if (contentDesc.toLowerCase().contains(desc.toLowerCase())) {
                    return el;
                }
            }
        }
        return null;
    }


}
