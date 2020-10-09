package ru.at.library.web.block.google;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.annotations.Optional;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Google Меню")
public class ApplicationsBlock extends CorePage {

    @Name("Приложения Google")
    @FindBy(css = "li[class=\"j1ei8c\"]")
    public ElementsCollection googleAppsList;

    @Optional
    @Name("Другие приложения Google")
    @FindBy(css = "a[class=\"NQV3m\"]")
    public SelenideElement otherAppsBtn;

}
