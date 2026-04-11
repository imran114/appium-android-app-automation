package pages.espresso;

import org.openqa.selenium.By;

/**
 * Locators for the "Basic Espresso sample" screen. Replace DUMMY values with real
 * resource-ids, text, or hints from your APK (Layout Inspector / Appium Inspector).
 */
public final class EspressoLocators {

    private EspressoLocators() {
    }

    /** Large label showing "Hello Espresso!" before change, then the typed value. */
    public static final By displayText =
            By.xpath("//android.widget.EditText[@resource-id=\"com.example.android.testing.espresso.BasicSample:id/editTextUserInput\"]/preceding-sibling::android.widget.TextView");

    /** EditText with hint "type something..." */
    public static final By inputField =
            By.xpath("//android.widget.EditText[@resource-id=\"com.example.android.testing.espresso.BasicSample:id/editTextUserInput\"]");

    /** "CHANGE TEXT" */
    public static final By changeTextButton =
            By.xpath("//android.widget.Button[@resource-id=\"com.example.android.testing.espresso.BasicSample:id/changeTextBt\"]");

    /** "OPEN ACTIVITY AND CHANGE TEXT" (optional for future tests) */
    public static final By openActivityAndChangeTextButton =
            By.xpath("//android.widget.Button[@resource-id=\"com.example.android.testing.espresso.BasicSample:id/activityChangeTextBtn\"]");
}
