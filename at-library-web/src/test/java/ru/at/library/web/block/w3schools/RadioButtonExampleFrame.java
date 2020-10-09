package ru.at.library.web.block.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("RadioButton example frame")
public class RadioButtonExampleFrame extends CorePage {

    @Name("Male radio button")
    @FindBy(css = "input[value=\"male\"]")
    public SelenideElement maleRadioBtn;

}
