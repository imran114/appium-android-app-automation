package locators;

import org.openqa.selenium.By;

/**
 * Locators previously supplied by {@code ReadLocatorPropertyFile} / {@code androidLocator.properties}.
 * Replace these with your per-page locator classes and wire those into the helpers, or update the
 * {@link By} definitions here until you migrate.
 */
public final class AndroidLocatorRegistry {

    private AndroidLocatorRegistry() {
    }

    // --- Shared with SeleniumUtils-style dashboard checks ---
    public static final By logoLocator =
            By.xpath("//android.widget.ImageView[@content-desc='Logo']");

    // --- ButtonActions (navigation & common chrome) ---
    public static final By closeButton = By.xpath("//*[@content-desc='__REPLACE_closeButtonXpath__']");
    public static final By cancelButton = By.xpath("//*[@content-desc='__REPLACE_cancelButtonXpath__']");
    public static final By okButton = By.xpath("//*[@content-desc='__REPLACE_okButtonLocatorXpath__']");
    public static final By crossIconBtn = By.xpath("//*[@content-desc='__REPLACE_crossButtonXpath__']");
    public static final By menuButton = By.xpath("//*[@content-desc='__REPLACE_menuButtonXpath__']");
    public static final By helpButton = By.xpath("//*[@content-desc='__REPLACE_helpTabXpath__']");
    public static final By dashboardScrollAreaLocator =
            By.xpath("//*[@content-desc='__REPLACE_dashboardScrollerAreaXpath__']");
    public static final By backButtonLocator = By.xpath("//*[@content-desc='__REPLACE_backButtonXpath__']");
    public static final By menuScreenLocator = By.xpath("//*[@content-desc='__REPLACE_menuScreenXpath__']");
    public static final By homeButton = By.xpath("//*[@content-desc='__REPLACE_homeButtonXpath__']");
    public static final By payNowButton = By.xpath("//*[@content-desc='__REPLACE_payNowXpath__']");
    public static final By cardScreenCloseButtonLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardScreenCloseButtonXpath__']");
    public static final By manageFavoritesButtonLocator =
            By.xpath("//*[@content-desc='__REPLACE_manageFavoritesButtonXpath__']");
    public static final By removeNumberButtonLocator =
            By.xpath("//*[@content-desc='__REPLACE_removeNumberButtonXpath__']");
    public static final By payBillPayNowButtonLocator =
            By.xpath("//*[@content-desc='__REPLACE_payBillPayNowButtonXpath__']");
    public static final By lowBalanceMsgLocator =
            By.xpath("//*[@content-desc='__REPLACE_lowBalanceMsgXpath__']");

    // --- EditText / card & pay-bill fields ---
    public static final By cardHolderNameLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardHolderNameXpath__']");
    public static final By cardHolderNumberLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardHolderNumberXpath__']");
    public static final By cardExpiryMonthLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardExpiryMonthXpath__']");
    public static final By cardExpiryYearLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardExpiryYearXpath__']");
    public static final By cardCvvLocator = By.xpath("//*[@content-desc='__REPLACE_cardCvvXpath__']");
    public static final By payBillCardHolderNumberLocator =
            By.xpath("//*[@content-desc='__REPLACE_payBillCardNumberXpath__']");
    public static final By payBillCardExpiryYearLocator =
            By.xpath("//*[@content-desc='__REPLACE_payBillCardExpiryYearXpath__']");
    public static final By payBillCardYearDropDownLocator =
            By.xpath("//*[@content-desc='__REPLACE_payBillCardExpiryYearDropDownXpath__']");
    public static final By payBillCardCvvLocator =
            By.xpath("//*[@content-desc='__REPLACE_payBillCardCvvXpath__']");
    public static final By cardErrorMsgLocator =
            By.xpath("//*[@content-desc='__REPLACE_cardErrorMsg1Xpath__']");
    public static final By cardErrorMsg2Locator =
            By.xpath("//*[@content-desc='__REPLACE_cardErrorMsg2Xpath__']");
}
