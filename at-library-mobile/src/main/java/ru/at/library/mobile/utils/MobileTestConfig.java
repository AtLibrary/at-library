package ru.at.library.mobile.utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;
import java.util.stream.Collectors;

import static ru.at.library.core.utils.helpers.PropertyLoader.loadPropertyInt;

public class MobileTestConfig {
    public static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10);
    public static final int DEFAULT_SWIPE_NUMBER = 15;

    public static boolean isDisplayedSelenideElementInCurrentPage(String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getElement(elementName).isDisplayed();
    }

    public static WebElement getWebElementInCurrentPage(String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getElement(elementName).getWrappedElement();
    }

    public static List<WebElement> getWebElementsInCurrentPage(String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getElementsList(elementName)
                .stream()
                .map(SelenideElement::getWrappedElement)
                .collect(Collectors.toList());
    }

    public static WebElement getWebElementInBlockCurrentPage(String blockName, String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getBlock(blockName).getElement(elementName).getWrappedElement();
    }

    public static List<WebElement> getWebElementsInBlockCurrentPage(String blockName, String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getBlock(blockName).getElementsList(elementName)
                .stream()
                .map(SelenideElement::getWrappedElement)
                .collect(Collectors.toList());
    }

    public static WebDriverWait driverWait() {
        return driverWait(DEFAULT_TIMEOUT);
    }

    public static WebDriverWait driverWait(int second) {
        return new WebDriverWait(WebDriverRunner.getWebDriver(), second);
    }
}
