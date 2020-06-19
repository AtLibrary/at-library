package ru.at.library.core.block.old;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.NavigationBlock;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("BCS demo аккаунт")
public class BrokerDemoPage extends CorePage {

    private static final String demoAccountForm = "[class*='become-demo__form form'] ";

    @Name("Навигация")
    @FindBy(xpath = ".//nav")
    public NavigationBlock navigationBlock;

    @Name("ФИО")
    @FindBy(css = demoAccountForm + "[name=\"name\"]")
    public SelenideElement inputFIO;

    @Name("Номер телефона")
    @FindBy(css = demoAccountForm + "[name=\"phone\"]")
    public SelenideElement inputPhone;

    @Name("Email")
    @FindBy(css = demoAccountForm + "[name=\"email\"]")
    public SelenideElement inputEmail;

    @Name("Открыть счет")
    @FindBy(css = demoAccountForm + "[class='become-demo__form-submit'] button")
    public SelenideElement buttonOpenScore;
}

