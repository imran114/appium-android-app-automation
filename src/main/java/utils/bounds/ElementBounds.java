package utils.bounds;


import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.seleniumUtils.SeleniumUtils;

import java.util.ArrayList;
import java.util.List;

public class ElementBounds extends SeleniumUtils {
    public ElementBounds(AppiumDriver driver) {
        super(driver);
    }

    private List<Integer> returnCoordinateList(String getBound) {
        List<Integer> arrToReturn = new ArrayList<>();
        String[] arrToGet = getBound.split("[\\[\\]]");
        for (String getFirstX : arrToGet) {
            if (!getFirstX.isEmpty()) {
                String xCoordinate = getFirstX.substring(0, getFirstX.indexOf(","));
                int getValueX = Integer.parseInt(xCoordinate);
                arrToReturn.add(getValueX);
                String yCoordinate = getFirstX.substring(getFirstX.indexOf(",") + 1);
                int getValueY = Integer.parseInt(yCoordinate);
                arrToReturn.add(getValueY);
            }
        }
        return arrToReturn;
    }

    public List<Integer> getCoordinates(By locator) {
        waitForClickable(locator);
        String coordinateString = returnAttribute(locator, MobileElementAttributes.MOBILE_ATTRIBUTE_BOUNDS);
        return returnCoordinateList(coordinateString);
    }
}
