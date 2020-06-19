package ru.at.library.mobile.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Покупки")
public class BuyPage extends CorePage {

    @Name("money")
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]")
    private SelenideElement money;

    @Name("description")
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"description\"]")
    private SelenideElement description;

    @Name("Назад")
    @FindBy(xpath = "//XCUIElementTypeButton[@name=\"button_back\"]")
    private SelenideElement button_back;

}
