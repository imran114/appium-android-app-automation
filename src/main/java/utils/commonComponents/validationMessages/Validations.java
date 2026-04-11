package utils.commonComponents.validationMessages;

import io.appium.java_client.AppiumDriver;
import locators.AndroidLocatorRegistry;
import org.openqa.selenium.By;
import utils.commonComponents.buttonActions.ButtonActions;
import utils.seleniumUtils.SeleniumUtils;



public class Validations extends SeleniumUtils {
    private final By cardErrorMsgLocator;
    private final By cardErrorMsg2Locator;
    private final By okButtonLocator;
    private final ButtonActions buttonActions;

    public Validations(AppiumDriver driver) {
        super(driver);
        cardErrorMsgLocator = AndroidLocatorRegistry.cardErrorMsgLocator;
        cardErrorMsg2Locator = AndroidLocatorRegistry.cardErrorMsg2Locator;
        okButtonLocator = AndroidLocatorRegistry.okButton;
        buttonActions = new ButtonActions(driver);
    }
    public String cardErrorMsgDisplayCheck(By locator, boolean condition) {
        if (isDisplayed(cardErrorMsg2Locator,15)){
            click(okButtonLocator);
        }
        else if (isDisplayed(cardErrorMsgLocator,13)){
            buttonActions.tapOnCloseButtonOnCardErrorMsg();
            if (isDisplayed(cardErrorMsg2Locator,13)){
                buttonActions.tapOnOkButton();
            }
        }
        if (condition) buttonActions.tapOnBackButton(locator);
        return "Skip, Unable to complete transaction";
    }


}
