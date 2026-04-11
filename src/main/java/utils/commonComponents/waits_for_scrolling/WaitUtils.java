package utils.commonComponents.waits_for_scrolling;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    private WebDriverWait wait;
    protected AppiumDriver driver;

    public WaitUtils(AppiumDriver driver){
        wait = new WebDriverWait(driver, Duration.ofSeconds(80));
        this.driver = driver;
    }


    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayed(By locator, int seconds) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                waitForVisibility(locator, seconds); // Wait for visibility of the element
                return driver.findElement(locator).isDisplayed(); // Check if the element is displayed
            } catch (StaleElementReferenceException e) {
                attempts++; // Increment the attempt counter and retry
                System.out.println("StaleElementReferenceException occurred. Retrying... Attempt: " + attempts);
            } catch (NoSuchElementException | TimeoutException e) {
                return false; // Return false if the element is not found or times out
            }
        }
        return false; // Return false if all attempts fail
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
}
