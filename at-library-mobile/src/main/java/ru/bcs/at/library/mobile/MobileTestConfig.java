package ru.bcs.at.library.mobile;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadPropertyInt;

public class MobileTestConfig {
    public static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10);

    public static WebElement getWebElementInCurrentPage(String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getElement(elementName).getWrappedElement();
    }

    public static  WebElement getWebElementInBlockCurrentPage(String blockName, String elementName) {
        return CoreScenario.getInstance().getCurrentPage().getBlock(blockName).getElement(elementName).getWrappedElement();
    }

    public static  WebDriverWait driverWait() {
        return driverWait(DEFAULT_TIMEOUT);
    }

    public static  WebDriverWait driverWait(int second) {
        return new WebDriverWait(WebDriverRunner.getWebDriver(), second);
    }
}
