package ru.at.library.mobile.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import ru.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static junit.framework.TestCase.assertEquals;
import static org.testng.Assert.fail;

public class AssertMobile {

    public static void expectedText(WebElement element, String expectedText, int second) {
        String actualText = null;
        for (int i = 0; i < second; i++) {
            actualText = element.getText();
            if (actualText.equals(expectedText)) {
                return;
            }
            sleep(1000);
        }
        screenshot();
        assertEquals(expectedText, actualText);
    }

    public static void containsText(WebElement element, String containText, int second) {
        String actualText = null;
        for (int i = 0; i < second; i++) {
            actualText = element.getText();
            if (actualText.contains(containText)) {
                return;
            }
            sleep(1000);
        }
        screenshot();
        fail("Текст: " + actualText + " не содержит: " + containText);
    }

    public static void display(WebElement element, boolean expectedDisplayed, int second) {
        boolean actualDisplayed = !expectedDisplayed;
        for (int i = 0; i < second; i++) {
            actualDisplayed = element.isDisplayed();
            if (actualDisplayed == expectedDisplayed) {
                return;
            }
            sleep(1000);
        }
        screenshot();
        fail("Displayed элемента должен быть:" + expectedDisplayed);
    }


    private static void screenshot() {
        final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        CoreScenario.getInstance().getScenario().attach(screenshot, "image/png","скриншот");
    }
}
