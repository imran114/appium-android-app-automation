package utils.loader_utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.seleniumUtils.SeleniumUtils;

import java.time.Duration;


public class LoaderUtils extends SeleniumUtils {

    public LoaderUtils(AppiumDriver driver) {
        super(driver);
    }


    public void waitForLoader(int maxWaitForLoaderToAppear, int maxWaitForLoaderToDisappear) {
        System.out.println("IN LOADER METHOD ----> ");

        By loaderLocator = By.xpath("//android.view.View[@content-desc='loader']");
        final Duration POLLING_INTERVAL = Duration.ofMillis(500);

        boolean loaderAppeared = waitForCondition(
                "Loader to appear",
                loaderLocator,
                maxWaitForLoaderToAppear,
                true,
                POLLING_INTERVAL
        );

        if (loaderAppeared) {
            waitForCondition("Loader to disappear", loaderLocator, maxWaitForLoaderToDisappear, false, POLLING_INTERVAL);
        } else {
            System.out.println(" Skipping wait for loader to disappear since it never appeared.");
        }
    }

    private boolean waitForCondition(String actionDescription, By locator, int timeoutSeconds, boolean shouldAppear, Duration pollingInterval) {
        System.out.printf("Waiting for %s. Max wait time: %d seconds.%n", actionDescription.toLowerCase(), timeoutSeconds);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.pollingEvery(pollingInterval).ignoring(NoSuchElementException.class);

            if (shouldAppear) {
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            } else {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            }

            System.out.printf(" %s.%n", actionDescription);
            return true;

        } catch (TimeoutException e) {
            System.out.printf(" %s within %d seconds.%n",
                    shouldAppear ? "Loader did not appear" : "Loader did not disappear",
                    timeoutSeconds
            );
            if (!shouldAppear) {
                getFailedElementScreenShot();
            }
        } catch (Exception e) {
            System.out.printf(" Unexpected error while waiting for %s: %s%n", actionDescription.toLowerCase(), e.getMessage());
            e.getMessage();
        }
        return false;
    }



}
