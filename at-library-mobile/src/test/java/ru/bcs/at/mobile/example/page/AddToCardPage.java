package ru.bcs.at.mobile.example.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("Корзина")
public class AddToCardPage extends CorePage {

    @Name("money")
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]")
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
