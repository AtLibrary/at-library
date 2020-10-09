package ru.at.library.web.block.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Input example frame")
public class InputExampleFrame extends CorePage {

    @Name("Header")
    @FindBy(tagName = "h1")
    public SelenideElement headerLabel;

    @Name("First name input")
    @FindBy(id = "fname")
    public SelenideElement firstNameInput;

}
