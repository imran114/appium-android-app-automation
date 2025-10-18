package pages.logout;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.commonComponents.buttonActions.ButtonActions;
import utils.commonComponents.scrollMethods.ScrollingMethods;
import utils.seleniumUtils.SeleniumUtils;

public class LogoutPage extends SeleniumUtils {

    // utils classes object
    private final ButtonActions buttonActions;
    private final ScrollingMethods scrollingMethods;

// locators
    private final By profileIconLocator = By.xpath("//android.widget.FrameLayout[@content-desc='Profile']");
    private final By menuIconLocator = By.xpath("//android.widget.Button[@content-desc=\"Options\"]");
    private final By settingsAndActivityScrollerAreaLocator = By.xpath("//androidx.compose.ui.platform.ComposeView");
    private final By logoutButtonLocator = By.xpath("//android.widget.Button[@text='Log out']");
    private final By notNowButtonLocator = By.xpath("//android.widget.Button[contains(@text,'Not now')]");
    private final By profileScrollAraLocator = By.xpath("//android.view.ViewGroup[@resource-id=\"com.instagram.android:id/similar_accounts_carousel_header\"]/parent::android.widget.LinearLayout");



    public LogoutPage(AppiumDriver driver, String platform) {
        super(driver, platform);
        buttonActions = new ButtonActions(driver, platform);
        scrollingMethods = new ScrollingMethods(driver);
    }

    public String clickOnProfileIcon(){
        return buttonActions.clickOnButton(profileIconLocator,"Profile Icon");
    }

    public String clickOnMenuIcon(){
        swipeToEnableMenuButton();
        return buttonActions.clickOnButton(menuIconLocator,"Menu Icon");
    }

    private void swipeToEnableMenuButton() {
        System.out.println("Swiping Up and down to enable menu button, it will take some time...");
        scrollingMethods.swipeUp(profileScrollAraLocator,500);
        scrollingMethods.swipeDown(profileScrollAraLocator,500);
    }

    public String clickOnLogoutButton(){
        scrollingMethods.performScroll(logoutButtonLocator, settingsAndActivityScrollerAreaLocator, ScrollingMethods.Direction.UP, 500);
        result = buttonActions.clickOnButton(logoutButtonLocator,"Logout Button");
        clickNotNowIfSaveLoginInfoDisplayed();
        return result;
    }

    private void clickNotNowIfSaveLoginInfoDisplayed(){
        if(isDisplayed(notNowButtonLocator,5)){
            click(notNowButtonLocator);
        }
    }
}
