package ru.bcs.at.mobile.example.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("Главный")
public class HomePage extends CorePage {

    @Name("money")
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]")
    private SelenideElement money;

    @Name("iphone")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"iphone\"]")
    private SelenideElement iphone;

    @Name("mouse")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"mouse\"]")
    private SelenideElement mouse;

    @Name("ps4")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"ps4\"]")
    private SelenideElement ps4;

    @Name("photo")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"photo\"]")
    private SelenideElement photo;

    @Name("keyboard")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"keyboard\"]")
    private SelenideElement keyboard;

    @Name("notebook")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"notebook\"]\n")
    private SelenideElement notebook;

    @Name("В корзину")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"add_to_cart\"]")
    private SelenideElement add_to_cart;

    @Name("Купить")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"buy_now_button\"]")
    private SelenideElement buy_now_button;

}

