package ru.at.library.core.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.annotations.Optional;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Главная")
public class GooglePage extends CorePage {

    @Name("Поиск")
    @FindBy(css = "input[name=\"q\"]")
    public SelenideElement searchInput;

    @Name("Кнопка Меню")
    @FindBy(css = "a[role=\"button\"][href*=\"products\"]")
    public SelenideElement menuBtn;

    @Optional
    @Name("Меню")
    @FindBy(css = "iframe[role=presentation]")
    public SelenideElement applicationsBlock;

}
