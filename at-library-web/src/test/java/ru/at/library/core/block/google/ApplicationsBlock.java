package ru.at.library.core.block.google;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Меню")
public class ApplicationsBlock extends CorePage {

    @Name("Приложения Google")
    @FindBy(css = "[class=\"j1ei8c\"]")
    public ElementsCollection googleAppsList;

    @Name("Другие приложения Google")
    @FindBy(css = "a[class=\"NQV3m\"]")
    public SelenideElement otherAppsBtn;

}
