package ru.at.library.core.block.atpractice;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Блок заголовка")
public class HeaderBlock extends CorePage {

    @Name("Логотип")
    @FindBy(css = "#header_logo")
    public SelenideElement logoBtn;

    @Name("Строка поиска")
    @FindBy(css = "#search_query_top")
    public SelenideElement searchInput;

    @Name("Кнопка поиск")
    @FindBy(css = "#searchbox button[type=\"submit\"]")
    public SelenideElement searchBtn;

}