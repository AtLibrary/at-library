package ru.at.library.core.page.w3schools;

import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.w3schools.CheckBoxExampleFrame;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Checkbox example")
public class CheckBoxExample extends CorePage {

    @Name("Checkbox example frame")
    @FindBy(xpath = "/html/body/h1/..")
    public CheckBoxExampleFrame checkBoxExampleFrame;

}
