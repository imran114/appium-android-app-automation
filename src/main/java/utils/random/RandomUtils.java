package utils.random;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.seleniumUtils.SeleniumUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils extends SeleniumUtils {

    private static final Random random = new Random();

    public RandomUtils(AppiumDriver driver) {
        super(driver);
    }

    public static By chooseRandomElement(By[] elementsList ) {
        return elementsList[random.nextInt(elementsList.length)];
    }

    public static int getRandomIndex(int bound) {
        return random.nextInt(bound);
    }



    public String clickMultipleElementsRandomly(By checkboxParentLocator, By countLocator, String checkboxType) {
        String key = checkboxType.replace(" ", "_").toLowerCase();
        // grab all the labels and count spans
        List<WebElement> boxes = returnWebElements(checkboxParentLocator);
        List<WebElement> counts = returnWebElements(countLocator);

        int size = Math.min(boxes.size(), counts.size());
        List<Integer> availableIndexes = new ArrayList<>();
        List<String> skippedFilters = new ArrayList<>();

        // classify each filter as available or skipped
        for (int i = 0; i < size; i++) {
            String numTxt = counts.get(i).getText().replaceAll("\\D+", "");
            int count = numTxt.isEmpty() ? 0 : Integer.parseInt(numTxt);
            String rawLabel = boxes.get(i).getText();
            String countText = counts.get(i).getText();
            String filterLabel = rawLabel.replace(countText, "").trim();

            if (count > 0) {
                availableIndexes.add(i);
            } else {
                skippedFilters.add(filterLabel);
            }
        }

        // if nothing to click, short-circuit
        if (availableIndexes.isEmpty()) {
            return "Skipped filters (no products): " + skippedFilters;
        }

        // pick a random # between 1 and availableIndexes.size()
        Random rand = new Random();
        int toClick = rand.nextInt(availableIndexes.size()) + 1;

        // shuffle & pick that many
        Collections.shuffle(availableIndexes);
        List<Integer> pick = availableIndexes.subList(0, toClick);

        // keep track of which ones actually got clicked
        List<String> clickedFilters = new ArrayList<>();

        for (int idx : pick) {
            WebElement cb = boxes.get(idx);
            // re-extract the label for reporting
            String rawLabel = cb.getText();
            String countText = counts.get(idx).getText();
            String filterLabel = rawLabel.replace(countText, "").trim();

            cb.click();
            if (!filterLabel.isEmpty()) clickedFilters.add(filterLabel);

        }
        return "Pass, Clicked checkboxes successfully: " + clickedFilters;
    }
}
