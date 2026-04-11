package utils.commonComponents.checkBoxs;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.seleniumUtils.SeleniumUtils;

import java.util.Random;

public class CheckBox extends SeleniumUtils {

    public CheckBox(AppiumDriver driver) {
        super(driver);
    }

    public String clickRandomCheckbox() {
        try {
            int randomIndex = new Random().nextInt(2) + 5;

            // Construct the xpath for the checkbox based on the random index
            String checkboxXpath = String.format("(//android.widget.TextView)[%d]/preceding-sibling::android.view.View", randomIndex);

            waitForClickable(By.xpath(checkboxXpath));
            // Find the checkbox element using the constructed xpath
            WebElement checkbox = driver.findElement(By.xpath(checkboxXpath));
            if (checkbox.isDisplayed()) {
                // Click on the selected checkbox
                checkbox.click();
            }
            return "Pass, successfully clicked on random checkbox at index " + randomIndex + ".";
        } catch (Exception e) {
            return "Fail, error occurred while clicking a random checkbox: " + e.getMessage();
        }
    }
}
