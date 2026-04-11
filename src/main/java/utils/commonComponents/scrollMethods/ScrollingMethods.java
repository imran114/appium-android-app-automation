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

public class ScrollingMethods extends WaitUtils {
    private static final Logger logger = LogManager.getLogger(ScrollingMethods.class);

    // Durations / attempts kept as constants to remove duplication (values unchanged from original)
    private static final int DEFAULT_SWIPE_DURATION = 500;
    private static final int DEFAULT_MAX_SCROLL_ATTEMPTS = 13;

    // Reused touch input source (avoid re-allocating identical PointerInput)
    private final PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    private final AppiumDriver driver;

    public ScrollingMethods(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
    }

    // ---------------------------
    // Public API (unchanged)
    // ---------------------------

    public void swipeUp(By element, int duration) {
        logSwipe("up");
        swipe(findVisible(element, 10), Direction.UP, duration);
    }

    public void swipeUp(By element) {
        logSwipe("up");
        swipe(findVisible(element, 10), Direction.UP, DEFAULT_SWIPE_DURATION);
    }

    public void swipeUp(WebElement element) {
        logSwipe("up");
        swipe(element, Direction.UP, DEFAULT_SWIPE_DURATION);
    }

    public void swipeDown(By element) {
        logSwipe("down");
        swipe(findVisible(element, 10), Direction.DOWN, DEFAULT_SWIPE_DURATION);
    }

    public void swipeDown(By element, int duration) {
        // NOTE: Preserved original behavior: ignores 'duration' and uses 500ms.
        logSwipe("down");
        swipe(findVisible(element, 10), Direction.DOWN, DEFAULT_SWIPE_DURATION);
    }

    public void swipeLeft(WebElement element) {
        logSwipe("left");
        swipe(element, Direction.LEFT, DEFAULT_SWIPE_DURATION);
    }

    public void swipeRight(WebElement element) {
        logSwipe("right");
        swipe(element, Direction.RIGHT, DEFAULT_SWIPE_DURATION);
    }

    public void performScroll(By targetElement, By scrollAreaElement, Direction direction) {
        int attempts = 0;
        boolean isVisible = isDisplayed(targetElement);

        while (!isVisible && attempts < DEFAULT_MAX_SCROLL_ATTEMPTS) {
            swipe(driver.findElement(scrollAreaElement), direction, DEFAULT_SWIPE_DURATION);
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }
        // Final visibility check and result
        isDisplayed(targetElement);
    }

    public void swipeUsingElementBounds(By firstElement, By secondElement, By targetElement) {
        performSwipeBetweenElements(firstElement, secondElement,
                /*x1+*/100, /*y1-*/40, /*x2+*/180, /*y2-*/150,
                /*moveDurationMs*/700, /*pauseMs*/700);

        boolean isVisible = isDisplayed(targetElement, 3);
        int attempts = 0, maxAttempts = 2;
        while (!isVisible && attempts < maxAttempts) {
            performSwipeBetweenElements(firstElement, secondElement,
                    100, 40, 180, 150, 700, 700);
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }
    }

    public void swipeUsingElementBounds(By firstElement, By secondElement, By targetElement,
                                        int x1, int y1, int x2, int y2) {
        boolean isVisible = isDisplayed(targetElement, 1);
        int attempts = 0, maxAttempts = 15;

        performSwipeBetweenElements(firstElement, secondElement,
                x1, y1, x2, y2, 700, 700);

        while (!isVisible && attempts < maxAttempts) {
            performSwipeBetweenElements(firstElement, secondElement,
                    x1, y1, x2, y2, 700, 700);
            isVisible = isDisplayed(targetElement);
            attempts++;
        }
    }

    public void swipeUsingElementBounds(By firstElement, By secondElement) {
        Point p1 = driver.findElement(firstElement).getLocation();
        Point p2 = driver.findElement(secondElement).getLocation();
        perform(buildSequence(1, p2.getX(), p2.getY(), p1.getX(), p1.getY(), 700, 700));
    }

    public void swipe(WebElement element, Direction direction, int pointerMoveDuration) {
        Rectangle r = element.getRect();
        int[] pts = computeSwipePoints(r, direction);
        perform(buildSequence(1, pts[0], pts[1], pts[2], pts[3], 100, pointerMoveDuration));
    }

    public void swipe(By scrollElement, By targetElement, Direction direction) {
        int attempts = 0;
        while (attempts < 5 && !isDisplayed(targetElement, 1)) {
            Rectangle rect = driver.findElement(scrollElement).getRect();

            int startX, startY, endX, endY;
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                int left = rect.getX();
                int width = rect.getWidth();
                int centerY = rect.getY() + rect.getHeight() / 2;
                startX = left + (int) (width * (direction == Direction.LEFT ? 0.8 : 0.2));
                endX = left + (int) (width * (direction == Direction.LEFT ? 0.2 : 0.8));
                startY = endY = centerY;
            } else {
                int top = rect.getY();
                int height = rect.getHeight();
                int centerX = rect.getX() + rect.getWidth() / 2;
                int margin = 20;
                if (direction == Direction.UP) {
                    startX = centerX; startY = top + height - margin;
                    endX = centerX;   endY = top + margin;
                } else { // DOWN
                    startX = centerX; startY = top + margin;
                    endX = centerX;   endY = top + height - margin;
                }
            }
            perform(buildSequence(1, startX, startY, endX, endY, 700, 700));
            attempts++;
        }
    }

    public boolean scrollFilters(By filterScrollLocator, By targetLocator) {
        WebElement carousel = findVisible(filterScrollLocator, 10);
        int left = carousel.getLocation().getX();
        int top = carousel.getLocation().getY();
        int width = carousel.getSize().getWidth();
        int height = carousel.getSize().getHeight();

        int startX = left + (int) (width * 0.8);
        int endX = left + (int) (width * 0.2);
        int centerY = top + height / 2;

        boolean isTargetVisible = isDisplayed(targetLocator, 1);
        int attempts = 0;

        while (!isTargetVisible && attempts < 5) {
            // exact original timing: no extra pause, 300ms move
            perform(buildSequence(attempts + 1, startX, centerY, endX, centerY, 0, 300));
            isTargetVisible = isDisplayed(targetLocator, 1);
            attempts++;
        }
        return isTargetVisible;
    }

    public void performDragAndDrop(By locator, Direction direction) {
        Rectangle r = driver.findElement(locator).getRect();
        int[] pts = computeSwipePoints(r, direction); // same geometry as swipe()
        perform(buildSequence(1, pts[0], pts[1], pts[2], pts[3], 700, 700));
    }

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    public void tapOnElementUsingCoordinates(Point tapPoint) {
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), tapPoint.x, tapPoint.y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(50)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(tap);
    }

    public Sequence pointerInput(int startX, int startY, int endX, int endY) {
        // original behavior: 700ms pause then 700ms move
        return buildSequence(1, startX, startY, endX, endY, 700, 700);
    }

    public Sequence pointerInput(int startX, int startY, int endX, int endY, int pointerMoveDuration) {
        // original behavior: 100ms pause then variable-duration move
        return buildSequence(1, startX, startY, endX, endY, 100, pointerMoveDuration);
    }

    /**
     * Swipe left across the screen using PointerInput (W3C Actions API).
     * Useful for horizontally scrollable carousels/lists where no element reference is required.
     */
    public void swipeLeftOnScreen(int durationMillis) {
        logger.info("Swiping left on screen...");
        Dimension size = driver.manage().window().getSize();
        int width = size.width, height = size.height;
        int startX = (int) (width * 0.8);
        int endX = (int) (width * 0.2);
        int y = height / 2;
        perform(buildSequence(1, startX, y, endX, y, 100, durationMillis));
    }

    public void performScroll(By targetElement, By scrollAreaElement, Direction direction,
                              int pointerMoveDuration, int maxScrollAttempts) {
        findVisible(scrollAreaElement, 10); // wait the area to exist/visible
        int attempts = 0;
        boolean isVisible = isDisplayed(targetElement, 2);

        while (!isVisible && attempts < maxScrollAttempts) {
            swipe(driver.findElement(scrollAreaElement), direction, pointerMoveDuration);
            waitForLoader();
            isVisible = isDisplayed(targetElement, 2);
            attempts++;
        }
        isDisplayed(targetElement, 2);
    }

    public void performScroll(By targetElement, By scrollAreaElement, Direction direction,
                              int pointerMoveDuration) {
        performScroll(targetElement, scrollAreaElement, direction, pointerMoveDuration, DEFAULT_MAX_SCROLL_ATTEMPTS);
    }

    public void waitForLoader() {
        By loader = By.xpath("//android.view.View[contains(@content-desc,'loader')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(loader));
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.invisibilityOfElementLocated(loader));
        } catch (TimeoutException ignored) {
            // Loader didn't appear or didn't disappear – kept original handling
        }
    }

    // ---------------------------
    // Private helpers (new)
    // ---------------------------

    private void logSwipe(String dir) { logger.info("Swiping " + dir + "..."); }

    private WebElement findVisible(By by, int timeoutSeconds) {
        waitForVisibility(by, timeoutSeconds);
        return driver.findElement(by);
    }

    private void perform(Sequence sequence) {
        driver.perform(Collections.singletonList(sequence));
    }

    private void performSwipeBetweenElements(By firstElement, By secondElement,
                                             int plusX2, int minusY2, int plusX1, int minusY1,
                                             int moveDurationMs, int pauseMs) {
        Point e1 = driver.findElement(firstElement).getLocation();
        Point e2 = driver.findElement(secondElement).getLocation();

        int startX = e2.getX() + plusX2;
        int startY = e2.getY() - minusY2;
        int endX   = e1.getX() + plusX1;
        int endY   = e1.getY() - minusY1;

        perform(buildSequence(1, startX, startY, endX, endY, pauseMs, moveDurationMs));
    }

    // Returns [startX, startY, endX, endY] with the SAME geometry as the original code.
    private static int[] computeSwipePoints(Rectangle rect, Direction direction) {
        int startX, startY, endX, endY;
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
        return new int[]{startX, startY, endX, endY};
    }

    /**
     * Unified sequence builder to eliminate duplicated low-level W3C actions assembly.
     * @param id sequence id (kept customizable to mirror original cases that used varying ids)
     */
    private Sequence buildSequence(int id, int startX, int startY, int endX, int endY, int pauseMs, int moveMs) {
        Sequence seq = new Sequence(finger, id);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        if (pauseMs > 0) {
            seq.addAction(new Pause(finger, Duration.ofMillis(pauseMs)));
        }
        seq.addAction(finger.createPointerMove(Duration.ofMillis(moveMs), PointerInput.Origin.viewport(), endX, endY));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return seq;
    }
}

