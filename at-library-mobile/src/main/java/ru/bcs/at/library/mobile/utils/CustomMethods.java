package ru.bcs.at.library.mobile.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.Dimension;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class CustomMethods {
    /**
     * Свайп на сенсорном экране с помощью событий движения пальцем
     * @param direction Направление действия Свайпа
     */
    public static void swipe(String direction) {
        int startx = 0, endx = 0, starty = 0, endy = 0;
        Dimension size = WebDriverRunner.getWebDriver().manage()
                .window().getSize();
        switch (direction) {
            case "UP":
                starty = (int) (size.height * 0.90);
                endy = (int) (size.height * 0.10);
                startx = size.width / 2;
                endx = startx;
                break;
            case "DOWN":
                starty = (int) (size.height * 0.10);
                endy = (int) (size.height * 0.90);
                startx = size.width / 2;
                endx = startx;
                break;
            case "LEFT":
                startx = (int) (size.height * 0.10);
                endx = (int) (size.height * 0.90);
                starty = size.height / 2;
                endy = starty;
                break;
            case "RIGHT":
                startx = (int) (size.width * 0.90);
                endx = (int) (size.width * 0.10);
                starty = size.height / 2;
                endy = starty;
                break;
        }

        new TouchAction((AppiumDriver) WebDriverRunner.getWebDriver())
                .press(point(startx, starty))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(endx, endy))
                .release().perform();
    }

}
