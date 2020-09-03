package ru.at.library.core.page.atpractice;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;

@Name("Товар")
public class Product extends ATPractice {

    @Name("Количество")
    @FindBy(css = "input#quantity_wanted")
    public SelenideElement productQtyInput;

    @Name("Размер")
    @FindBy(css = "select#group_1")
    public  SelenideElement sizeSelect;

    @Name("Добавить в корзину")
    @FindBy(css = "#add_to_cart button")
    public SelenideElement addToCartButton;

}