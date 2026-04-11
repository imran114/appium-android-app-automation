package utils.commonComponents.actions;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import utils.seleniumUtils.SeleniumUtils;

import java.time.Duration;
import java.util.Collections;

public class ActionsClass extends SeleniumUtils {

    public ActionsClass(AppiumDriver driver) {
        super(driver);
    }

/*    public void performDragAndDrop(AppiumDriver driver, By sourceElement, By targetElement) {
        // Locate the source and target elements
        WebElement source = returnWebElement(sourceElement);
        WebElement target = returnWebElement(targetElement);

        // Perform drag-and-drop using Actions
        Actions actions = new Actions(driver);
        try {
            System.out.println("Attempting drag-and-drop...");
            actions.clickAndHold(source)
                    .pause(Duration.ofSeconds(1)) // Optional: Add a pause to mimic real interaction
                    .moveToElement(target)
                    .pause(Duration.ofSeconds(1)) // Optional: Pause before releasing
                    .release()
                    .build()
                    .perform();
            System.out.println("Drag-and-drop performed successfully.");
        } catch (Exception e) {
            System.err.println("Drag-and-drop failed: " + e.getMessage());
        }

    }*/


    public void performDragAndDrop(AppiumDriver driver, By srcLocator, By tgtLocator) {
        // 1) Find elements
        WebElement src = driver.findElement(srcLocator);
        WebElement tgt = driver.findElement(tgtLocator);

        // 2) Compute center points for each
        Point sLoc = src.getLocation();
        Dimension sSz = src.getSize();
        int srcX = sLoc.getX() + sSz.getWidth() / 2;
        int srcY = sLoc.getY() + sSz.getHeight() / 2;

        Point tLoc = tgt.getLocation();
        Dimension tSz = tgt.getSize();
        int tgtX = tLoc.getX() + tSz.getWidth() / 2;
        int tgtY = tLoc.getY() + tSz.getHeight() / 2;

        // 3) Calculate an 80% move so you stop a bit shy
        int deltaX = tgtX - srcX;
        int deltaY = tgtY - srcY;
        int moveX = (int) (deltaX * 0.8);
        int moveY = (int) (deltaY * 0.8);

        // 4) Build your W3C touch sequence
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(finger, 1);

        // a) jump to source center (absolute)
        drag.addAction(finger.createPointerMove(
                Duration.ZERO, PointerInput.Origin.viewport(), srcX, srcY));
        // b) put finger down
        drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // c) small pause
        drag.addAction(new Pause(finger, Duration.ofMillis(600)));
        // d) first half of the drag
        drag.addAction(finger.createPointerMove(
                Duration.ofMillis(800),
                PointerInput.Origin.viewport(),
                srcX + moveX / 2, srcY + moveY / 2));
        // e) midpoint pause
        drag.addAction(new Pause(finger, Duration.ofMillis(600)));
        // f) second half of the drag (near target)
        drag.addAction(finger.createPointerMove(
                Duration.ofMillis(800),
                PointerInput.Origin.viewport(),
                srcX + moveX, srcY + moveY));
        // g) pause before release
        drag.addAction(new Pause(finger, Duration.ofMillis(800)));
        // h) lift finger
        drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // 5) fire it off!
        driver.perform(Collections.singletonList(drag));
    }


}
