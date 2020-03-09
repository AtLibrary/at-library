package ru.appavlov.at.library.mobile.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class CustomMethods {

    private final static Integer DEFAULT_LESS_PERCENT = 10;
    private final static Integer DEFAULT_LARGE_PERCENT = 90;
    private final static Integer DEFAULT_OTHER_AXIS_PERCENT = 50;

    /**
     * Свайп на сенсорном экране с помощью событий движения пальцем
     *
     * @param direction Направление действия Свайпа
     */
    public static void swipe(String direction) {
        swipe(direction, DEFAULT_LESS_PERCENT, DEFAULT_LARGE_PERCENT, DEFAULT_OTHER_AXIS_PERCENT);
    }

    /**
     * Свайп на сенсорном экране с помощью событий движения пальцем
     *
     * @param direction    Направление действия Свайпа
     * @param lessPercent  Меньший процент экрана по оси свайпа
     * @param largePercent Больший процент экрана по оси свайпа
     */
    public static void swipe(String direction, Integer lessPercent, Integer largePercent) {
        swipe(direction, lessPercent, largePercent, DEFAULT_OTHER_AXIS_PERCENT);
    }

    /**
     * Свайп на сенсорном экране с помощью событий движения пальцем
     *
     * @param direction        Направление действия Свайпа
     * @param otherAxisPercent Процент экрана по оси перпендикулярной свайпу
     */
    public static void swipe(String direction, Integer otherAxisPercent) {
        swipe(direction, DEFAULT_LESS_PERCENT, DEFAULT_LARGE_PERCENT, otherAxisPercent);
    }

    /**
     * Свайп на сенсорном экране с помощью событий движения пальцем
     *
     * @param direction        Направление действия Свайпа
     * @param lessPercent      Меньший процент экрана по оси свайпа
     * @param largePercent     Больший процент экрана по оси свайпа
     * @param otherAxisPercent Процент экрана по оси перпендикулярной свайпу
     */
    public static void swipe(String direction, Integer lessPercent, Integer largePercent, Integer otherAxisPercent) {
        if (lessPercent == null) {
            lessPercent = DEFAULT_LESS_PERCENT;
        }
        if (largePercent == null) {
            largePercent = DEFAULT_LARGE_PERCENT;
        }
        if (otherAxisPercent == null) {
            otherAxisPercent = DEFAULT_OTHER_AXIS_PERCENT;
        }
        int startx = 0, endx = 0, starty = 0, endy = 0;
        Dimension size = WebDriverRunner.getWebDriver().manage()
                .window().getSize();
        switch (direction) {
            case "UP":
                starty = (int) (size.height * 0.01 * largePercent);
                endy = (int) (size.height * 0.01 * lessPercent);
                startx = (int) (size.width * 0.01 * otherAxisPercent);
                endx = startx;
                break;
            case "DOWN":
                starty = (int) (size.height * 0.01 * lessPercent);
                endy = (int) (size.height * 0.01 * largePercent);
                startx = (int) (size.width * 0.01 * otherAxisPercent);
                endx = startx;
                break;
            case "LEFT":
                startx = (int) (size.width * 0.01 * largePercent);
                endx = (int) (size.width * 0.01 * lessPercent);
                starty = (int) (size.height * 0.01 * otherAxisPercent);
                endy = starty;
                break;
            case "RIGHT":
                startx = (int) (size.width * 0.01 * lessPercent);
                endx = (int) (size.width * 0.01 * largePercent);
                starty = (int) (size.height * 0.01 * otherAxisPercent);
                endy = starty;
                break;
        }

        new TouchAction((AppiumDriver) WebDriverRunner.getWebDriver())
                .press(point(startx, starty))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(endx, endy))
                .release().perform();
    }

    /**
     * скрыть системное уведомление с экрана
     */
    public static void hideNotification() {
        Dimension screenSize = WebDriverRunner.getWebDriver().manage().window().getSize();
        int xMid = Math.toIntExact(Math.round(0.6 * screenSize.width));
        int yMid = Math.toIntExact(Math.round(0.6 * screenSize.height));
        PointOption allowNotificationButton = PointOption.point(xMid, yMid);

        TouchAction action = new TouchAction((AppiumDriver) getWebDriver());
        action.tap(allowNotificationButton).perform();
    }

}
