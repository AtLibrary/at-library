package ru.at.library.core.page.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Checkbox example")
public class CheckBoxExample extends CorePage {

    @Name("Bike checkbox")
    @FindBy(css = "input#vehicle1")
    public SelenideElement bikeCheckbox;

}
