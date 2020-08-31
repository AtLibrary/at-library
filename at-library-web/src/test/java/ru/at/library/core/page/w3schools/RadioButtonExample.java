package ru.at.library.core.page.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Radio example")
public class RadioButtonExample extends CorePage {

    @Name("Male radio button")
    @FindBy(css = "input[value=\"male\"]")
    public SelenideElement maleRadioBtn;

}
