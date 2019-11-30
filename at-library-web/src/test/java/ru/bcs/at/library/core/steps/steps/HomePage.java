package ru.bcs.at.library.core.steps.steps;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("BCS Брокер")
public class HomePage extends CorePage {

    @Name("Демо-счет")
    @FindBy(css = "[class=\"header__demoaccount btn btn-secondary _small\"]")
    private SelenideElement buttonDemoAccount;
}
