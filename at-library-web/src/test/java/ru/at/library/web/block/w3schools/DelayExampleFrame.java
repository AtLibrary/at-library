package ru.at.library.web.block.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.annotations.Optional;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Delay example frame")
public class DelayExampleFrame extends CorePage {

    @Name("Show squares")
    @FindBy(tagName = "button")
    public SelenideElement showSquaresBtn;

    @Optional
    @Name("Red square")
    @FindBy(id = "div4")
    public SelenideElement redSquareDiv;

    @Optional
    @Name("Purple square")
    @FindBy(id = "div5")
    public SelenideElement purpleSquareDiv;

}
