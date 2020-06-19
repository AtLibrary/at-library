package ru.at.library.mobile.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Корзина")
public class AddToCardPage extends CorePage {

    @Name("money")
    @FindAll({
            //iOS
            @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]"),
            // Android
            @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]")
    })
    private SelenideElement money;

    @Name("description")
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"description\"]")
    private SelenideElement description;

    @Name("Назад")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"button_back\"]")
    private SelenideElement button_back;

    @Name("Купить")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"buy_it_here\"]")
    private SelenideElement buy_it_here;

}
