package ru.at.library.web.page.atpractice;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;

@Name("Практика автоматизации - магазин")
public class Shop extends ATPractice{

    @Name("Товары")
    @FindBy(css = "[class=\"product_list grid row\"]>li")
    public ElementsCollection productsList;

    @Name("Первый товар")
    @FindBy(css = "[class=\"product_list grid row\"]>li:first-child .left-block")
    public SelenideElement firstProductBtn;

    @Name("Первый товар - Быстрый просмотр")
    @FindBy(css = "[class=\"product_list grid row\"]>li:first-child .quick-view")
    public SelenideElement quickViewBtn;

    @Name("Первый товар - Просмотр")
    @FindBy(css = "[class=\"product_list grid row\"]>li:first-child .button-container a:nth-child(2)")
    public SelenideElement viewProductBtn;

}