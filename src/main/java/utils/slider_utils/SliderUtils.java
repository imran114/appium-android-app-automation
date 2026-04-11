package utils.slider_utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import utils.seleniumUtils.SeleniumUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SliderUtils extends SeleniumUtils {
    private static final int DEFAULT_DRAG_MS = 300;

    public SliderUtils(AppiumDriver driver) {
        super(driver);
    }

    /** your original method, preserved */
    public String slidePriceSlider() {
        return slidePriceSlider(DEFAULT_DRAG_MS);
    }

    /** same as before, but you can override the drag duration */
    public String slidePriceSlider(int dragMs) {
        try {
            List<WebElement> bars = returnWebElements(By.xpath(
                    "//android.view.View[@content-desc='range slider']/android.widget.SeekBar"
            ));
            if (bars.size() != 2) {
                return "Fail, expected 2 SeekBars but found " + bars.size();
            }

            float f1 = ThreadLocalRandom.current().nextFloat();
            float f2 = ThreadLocalRandom.current().nextFloat();
            if (f1 > f2) { float t = f1; f1 = f2; f2 = t; }

            slideElement(bars.get(0), f1, dragMs);
            slideElement(bars.get(1), f2, dragMs);

            return String.format("Pass, range slider moved to [%.2f, %.2f] in %dms",
                    f1, f2, dragMs);
        } catch (Exception e) {
            logger.error("Error randomizing range slider", e);
            return "Fail, exception while randomizing range slider: " + e.getMessage();
        }
    }


    private void slideElement(WebElement el,
                              float fraction,
                              int dragMs) {
        // 1) get the track’s bounds
        Point loc      = el.getLocation();
        Dimension size = el.getSize();
        int leftX      = loc.getX();
        int width      = size.getWidth();
        int centerY    = loc.getY() + size.getHeight() / 2;

        // 2) choose a tap‐start a few pixels in from the left edge
        int startX = leftX + 10;  // +10px so we actually hit the thumb region

        // 3) compute the target position along the track
        //    note: fraction=0.0 → far left,    fraction=1.0 → far right
        int targetX = leftX + Math.round(width * fraction);

        // 4) build & perform the W3C touch sequence
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(
                Duration.ZERO,
                PointerInput.Origin.viewport(),
                startX, centerY
        ));
        swipe.addAction(finger.createPointerDown(
                PointerInput.MouseButton.LEFT.asArg()
        ));
        swipe.addAction(finger.createPointerMove(
                Duration.ofMillis(dragMs),
                PointerInput.Origin.viewport(),
                targetX, centerY
        ));
        swipe.addAction(finger.createPointerUp(
                PointerInput.MouseButton.LEFT.asArg()
        ));

        driver.perform(Collections.singletonList(swipe));
    }

}
