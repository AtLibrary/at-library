package ru.appavlov.at.library.core.block.old;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.appavlov.at.library.core.block.NavigationBlock;
import ru.appavlov.at.library.core.cucumber.annotations.Name;
import ru.appavlov.at.library.core.cucumber.api.CorePage;

@Name("BCS Брокер")
public class HomePage extends CorePage {

    @Name("Навигация")
    @FindBy(xpath = ".//nav")
    public NavigationBlock navigationBlock;

    @Name("Демо-счет")
    @FindBy(css = "[class=\"header__demoaccount btn btn-secondary _small\"]")
    public SelenideElement buttonDemoAccount;
}
