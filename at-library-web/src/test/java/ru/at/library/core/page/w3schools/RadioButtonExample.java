package ru.at.library.core.page.w3schools;

import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.w3schools.RadioButtonExampleFrame;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Radio example")
public class RadioButtonExample extends CorePage {

    @Name("RadioButton example frame")
    @FindBy(xpath = "/html/body/form/..")
    public RadioButtonExampleFrame radioButtonExampleFrame;

}
