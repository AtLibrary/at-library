package ru.appavlov.at.library.core.page;


import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.appavlov.at.library.core.block.NavigationBlock;
import ru.appavlov.at.library.core.cucumber.annotations.Name;
import ru.appavlov.at.library.core.cucumber.api.CorePage;

@Name("BCS demo аккаунт")
public class BrokerDemoPage extends CorePage {

    private static final String demoAccountForm = "[class*='become-demo__form form'] ";

    @Name("Навигация")
    @FindBy(xpath = ".//nav")
    private NavigationBlock navigationBlock;

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

