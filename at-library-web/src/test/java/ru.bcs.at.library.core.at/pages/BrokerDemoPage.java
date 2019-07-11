package ru.bcs.at.library.core.at.pages;


import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("BCS demo аккаунт")
public class BrokerDemoPage extends CorePage {

    private static final String demoAccountForm = "[class='become-demo__form form js-demo-quik-form'] ";

    @Name("ФИО")
    @FindBy(css = demoAccountForm + "[name=\"name\"]")
    private SelenideElement inputFIO;

    @Name("Номер телефона")
    @FindBy(css = demoAccountForm + "[name=\"phone\"]")
    private SelenideElement inputPhone;

    @Name("Email")
    @FindBy(css = demoAccountForm + "[name=\"email\"]")
    private SelenideElement inputEmail;

    @Name("Открыть счет")
    @FindBy(css = demoAccountForm + "[class='become-demo__form-submit'] button")
    private SelenideElement buttonOpenScore;
}

