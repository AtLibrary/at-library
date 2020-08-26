package ru.at.library.core.page.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.annotations.Optional;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Delay example")
public class DelayExample extends CorePage {

    @Name("Show squares")
    @FindBy(css = "body > button")
    public SelenideElement showSquaresBtn;

    @Optional
    @Name("Red square")
    @FindBy(css = "[id=\"div4\"]")
    public SelenideElement redSquareDiv;

    @Optional
    @Name("Purple square")
    @FindBy(css = "[id=\"div5\"]")
    public SelenideElement purpleSquareDiv;

}
