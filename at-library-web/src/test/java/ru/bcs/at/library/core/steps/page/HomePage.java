package ru.bcs.at.library.core.steps.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;
import ru.bcs.at.library.core.steps.block.Navigation;

@Name("BCS Брокер")
public class HomePage extends CorePage {

    @Name("Навигация")
    @FindBy(xpath = ".//nav")
    private Navigation navigation;

    @Name("Демо-счет")
    @FindBy(css = "[class=\"header__demoaccount btn btn-secondary _small\"]")
    private SelenideElement buttonDemoAccount;
}
