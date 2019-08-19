package ru.bcs.at.mobile.example.steps;

import cucumber.api.java.ru.И;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;
import ru.bcs.at.mobile.example.page.HomePage;

import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.bcs.at.library.mobile.MobileTestConfig.driverWait;
import static ru.bcs.at.library.mobile.MobileTestConfig.getWebElementInCurrentPage;

public class HomePageSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

    @И("выбор всех товаров и проверка обшей суммы: \"([^\"]*)\"")
    public void loginSystem(String money) {
        money = getPropertyOrStringVariableOrValue(money);
        HomePage homePage =
                (HomePage) coreScenario.getPage("Главный");

        homePage.getElement("iphone").click();
        homePage.getElement("mouse").click();
        homePage.getElement("ps4").click();
        homePage.getElement("photo").click();
        homePage.getElement("keyboard").click();
        homePage.getElement("notebook").click();

        WebElement element = getWebElementInCurrentPage("money");
        driverWait().until(ExpectedConditions.textToBePresentInElement(element, money));
    }
}
