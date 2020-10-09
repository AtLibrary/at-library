package ru.at.library.web.page.atpractice;

import com.codeborne.selenide.ElementsCollection;
import org.openqa.selenium.support.FindBy;
import ru.at.library.web.block.atpractice.HeaderBlock;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Практика автоматизации - главная")
public class ATPractice extends CorePage {

    @Name("Блок заголовка")
    @FindBy(css = "#header")
    public HeaderBlock headerBlock;

    @Name("Категории товаров")
    @FindBy(css = "[class=\"sf-menu clearfix menu-content sf-js-enabled sf-arrows\"]>li")
    public ElementsCollection shopCategoriesList;

}
