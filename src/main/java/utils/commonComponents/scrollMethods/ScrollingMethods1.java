package utils.commonComponents.scrollMethods;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.commonComponents.waits_for_scrolling.WaitUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class ScrollingMethods1 extends WaitUtils {
    private static final Logger logger = LogManager.getLogger(ScrollingMethods1.class);
    private final PointerInput finger;

    private final AppiumDriver driver;

    public ScrollingMethods1(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
        finger  = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    }

    public void swipeUp(By element, int duration) {
        logger.info("Swiping up...");
        waitForVisibility(element,10);
        WebElement element1 = driver.findElement(element);
        swipe(element1, Direction.UP, duration);
    }

    public void swipeUp(By element) {
        logger.info("Swiping up...");
        waitForVisibility(element,10);
        WebElement element1 = driver.findElement(element);
        swipe(element1, Direction.UP, 500);
    }


    public void swipeUp(WebElement element) {
        logger.info("Swiping up...");
        swipe(element, Direction.UP, 500);
    }


    public void swipeDown(By element) {
        waitForVisibility(element,10);
        logger.info("Swiping down...");
        swipe(driver.findElement(element), Direction.DOWN, 500);
    }

    public void swipeDown(By element, int duration) {
        waitForVisibility(element,10);
        logger.info("Swiping down...");
        swipe(driver.findElement(element), Direction.DOWN, 500);
    }



    public void swipeLeft(WebElement element) {
        logger.info("Swiping left...");
        swipe(element, Direction.LEFT, 500);
    }
    public void swipeLeft(WebElement element, int duration) {
        logger.info("Swiping left...");
        swipe(element, Direction.LEFT, duration);
    }

    public void swipeRight(WebElement element) {
        logger.info("Swiping right...");
        swipe(element, Direction.RIGHT, 500);
    }

    public void performScroll(By targetElement, By scrollAreaElement, Direction direction) {
        int attempts = 0;
        int maxScrollAttempts = 13;

        // Check if the target element is visible initially
        boolean isVisible = isDisplayed(targetElement);

        while (!isVisible && attempts < maxScrollAttempts) {
            swipe(driver.findElement(scrollAreaElement), direction, 500);
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }

        // Final visibility check and result
        isDisplayed(targetElement);
    }


    public void swipeUsingElementBounds(By firstElement, By secondElement, By targetElement) {
        waitForVisibility(firstElement, 10);
        WebElement element1 = driver.findElement(firstElement);
        WebElement element2 = driver.findElement(secondElement);
        Point element1Location = element1.getLocation();
        Point element2Location = element2.getLocation();
        int startX = element2Location.getX() + 100; // adjustments for MYOB   100, -40, 270, 100
        int startY = element2Location.getY() - 40;
        int endX = element1Location.getX() + 180;  // + 180   // 210,100,150 not worked
        int endY = element1Location.getY() - 150;   // - 150
        Sequence sequence = pointerInput(startX, startY, endX, endY);
        driver.perform(Collections.singletonList(sequence));
        boolean isVisbile = isDisplayed(targetElement, 3);
        int attempts = 0;
        int maxAttempts = 2;
        while (!isVisbile && attempts < maxAttempts) {
            driver.perform(Collections.singletonList(sequence));
            isVisbile = isDisplayed(targetElement, 2);
            attempts++;
        }
    }


    public void swipeUsingElementBounds(By firstElement, By secondElement, By targetElement, int x1, int y1, int x2, int y2) {
        waitForVisibility(firstElement, 10);
        WebElement element1 = driver.findElement(firstElement);
        WebElement element2 = driver.findElement(secondElement);
        Point element1Location = element1.getLocation();
        Point element2Location = element2.getLocation();
        int startX = element2Location.getX() + x1;
        int startY = element2Location.getY() - y1;
        int endX = element1Location.getX() + x2;
        int endY = element1Location.getY() - y2;
        Sequence sequence = pointerInput(startX, startY, endX, endY);
        boolean isVisbile = isDisplayed(targetElement, 1);
        int attempts = 0;
        int maxAttempts = 15;
        driver.perform(Collections.singletonList(sequence));
        while (!isVisbile && attempts < maxAttempts) {
            driver.perform(Collections.singletonList(sequence));
            isVisbile = isDisplayed(targetElement);
            attempts++;
        }
    }

   public void swipeUsingElementBounds(By firstElement, By secondElement) {
        if (!isDisplayed(firstElement,1) || !isDisplayed(secondElement,1)) {
            return;
        }

        WebElement element1 = driver.findElement(firstElement);
        WebElement element2 = driver.findElement(secondElement);
        Point s = element1.getLocation();
        Point s1 = element2.getLocation();
        int startX = s1.getX();
        int startY = s1.getY();
        int endX = s.getX();
        int endY = s.getY();
        Sequence sequence = pointerInput(startX, startY, endX, endY);
        driver.perform(Collections.singletonList(sequence));
    }


    public void swipe(WebElement element, Direction direction, int pointerMoveDuration) {
        Rectangle rect = element.getRect();
        int startX;
        int startY;
        int endX;
        int endY;

        switch (direction) {
            case UP:


                 startX = rect.x + (rect.width / 2);
                 startY = rect.y + (int) (rect.height * 0.7);  // Start near bottom of element
                 endY = rect.y + (int) (rect.height * 0.4);    // End higher up (finger moves up)


//                startX = rect.x + rect.getWidth() / 2;
//                startY = rect.getY() + rect.getHeight() - 300;
                endX = startX;
//                endY = rect.getY() + 300;

                break;
            case DOWN:
                startX = rect.getX() + rect.getWidth() / 2;
                startY = rect.getY() + 10;
                endX = startX;
                endY = rect.getY() + rect.getHeight() - 10;
                break;
            case LEFT:
                startX = rect.getX() + rect.getWidth() - 180;
                startY = rect.getY() + rect.getHeight() / 2;
                endX = rect.getX() + 180;
                endY = startY;
                break;
            case RIGHT:
                startX = rect.getX() + 100;
                startY = rect.getY() + rect.getHeight() / 2;
                endX = rect.getX() + rect.getWidth() - 100;
                endY = startY;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        Sequence sequence = pointerInput(startX, startY, endX, endY, pointerMoveDuration); // 700ms for swipe duration
        driver.perform(Collections.singletonList(sequence));
    }


    public void swipe(By scrollElement, By targetElement, Direction direction) {
        int attempts = 0;
        // keep going until we see the target or exhaust our retries
        while (attempts < 5 && !isDisplayed(targetElement, 1)) {
            WebElement carousel = driver.findElement(scrollElement);
            Rectangle rect = carousel.getRect();

            int startX, startY, endX, endY;
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                // horizontal swipe: use 0.8/0.2 fractions of the element width
                int left = rect.getX();
                int width = rect.getWidth();
                int centerY = rect.getY() + rect.getHeight() / 2;

                // for a LEFT swipe, start at 80%→20%; for RIGHT, 20%→80%
                startX = left + (int) (width * (direction == Direction.LEFT ? 0.8 : 0.2));
                endX = left + (int) (width * (direction == Direction.LEFT ? 0.2 : 0.8));
                startY = endY = centerY;
            } else {
                // vertical swipe: fall back to margin logic
                int top = rect.getY();
                int height = rect.getHeight();
                int centerX = rect.getX() + rect.getWidth() / 2;
                int margin = 20;

                if (direction == Direction.UP) {
                    startX = centerX;
                    startY = top + height - margin;
                    endX = centerX;
                    endY = top + margin;
                } else { // DOWN
                    startX = centerX;
                    startY = top + margin;
                    endX = centerX;
                    endY = top + height - margin;
                }
            }

            // build & fire a fresh Sequence each iteration
            Sequence swipe = pointerInput(startX, startY, endX, endY);
            driver.perform(Collections.singletonList(swipe));

            attempts++;
        }
    }


    public boolean scrollFilters(By filterScrollLocator, By targetLocator) {
        waitForVisibility(filterScrollLocator, 10);
        WebElement carousel = driver.findElement(filterScrollLocator);

        int left = carousel.getLocation().getX();
        int top = carousel.getLocation().getY();
        int width = carousel.getSize().getWidth();
        int height = carousel.getSize().getHeight();

        int startX = left + (int) (width * 0.8);
        int endX = left + (int) (width * 0.2);
        int centerY = top + height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        boolean isTargetVisible = isDisplayed(targetLocator, 1);
        int attempts = 0;

        while (!isTargetVisible && attempts < 5) {
            // build a brand-new sequence each time
            Sequence swipe = new Sequence(finger, attempts + 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO,
                    PointerInput.Origin.viewport(), startX, centerY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(300),
                    PointerInput.Origin.viewport(), endX, centerY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(Collections.singletonList(swipe));

            isTargetVisible = isDisplayed(targetLocator, 1);
            attempts++;
        }
        return isTargetVisible;
    }


    public void performDragAndDrop(By locator, Direction direction) {
        WebElement element = driver.findElement(locator);
        Rectangle rect = element.getRect();
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;

        switch (direction) {
            case UP:
                startX = rect.getX() + rect.getWidth() / 2;
                startY = rect.getY() + rect.getHeight() - 300;
                endX = startX;
                endY = rect.getY() + 300;
                break;
            case DOWN:
                startX = rect.getX() + rect.getWidth() / 2;
                startY = rect.getY() + 10;
                endX = startX;
                endY = rect.getY() + rect.getHeight() - 10;
                break;
        }

        Sequence sequence = pointerInput(startX, startY, endX, endY);
        driver.perform(Collections.singletonList(sequence));
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }


    public void tapOnElementUsingCoordinates(Point tapPoint) {
        var tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), tapPoint.x, tapPoint.y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(50)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public Sequence pointerInput(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(new Pause(finger, Duration.ofMillis(700))); // optional finger hold before move
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), endX, endY)); // slower swipe
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return sequence;
    }

    public Sequence pointerInput(int startX, int startY, int endX, int endY, int pointerMoveDuration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);


//        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
//        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
//        swipe.addAction(finger.createPointerMove(ofMillis(500), PointerInput.Origin.viewport(), startX, endY));
//        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));


        sequence.addAction(
                finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(new Pause(finger, Duration.ofMillis(300))); // little pause for realism
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(pointerMoveDuration), PointerInput.Origin.viewport(), endX, endY));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return sequence;
    }


    public void performScroll(By targetElement, By scrollAreaElement, Direction direction,
                              int pointerMoveDuration, int maxScrollAttempts) {
        waitForVisibility(scrollAreaElement, 10);

        int attempts = 0;
        boolean isVisible = isDisplayed(targetElement, 2);

        while (!isVisible && attempts < maxScrollAttempts) {
            WebElement scrollArea = driver.findElement(scrollAreaElement);
            swipe(scrollArea, direction, pointerMoveDuration);
            if (direction == Direction.DOWN) waitForLoader();
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }

        // Final visibility check (acts as assertion or wait)
        isDisplayed(targetElement, 2);
    }

    /**
     * Overloaded version with default max scroll attempts (13).
     */
    public void performScroll(By targetElement, By scrollAreaElement, Direction direction,
                              int pointerMoveDuration) {
        performScroll(targetElement, scrollAreaElement, direction, pointerMoveDuration, 13);
    }

    public void waitForLoader() {
        System.out.println("Waiting for loader to disappear if present...");
        By loader = By.xpath("//android.view.View[contains(@content-desc,'loader')]");
        boolean loaderAppeared =  isDisplayed(loader, 2);
        if (loaderAppeared) {
            System.out.println("Loader appeared, waiting for it to disappear...");
        } else {
            System.out.println("Loader did not appear.");
            return;
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(loader));

            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.invisibilityOfElementLocated(loader));
            System.out.println("Loader disappeared");
        } catch (TimeoutException ignored) {
            // Loader didn't appear or didn't disappear – handle accordingly if needed
        }
    }



}
