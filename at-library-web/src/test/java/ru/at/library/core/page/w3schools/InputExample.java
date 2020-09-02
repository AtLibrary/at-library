package ru.at.library.core.page.w3schools;

import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.w3schools.InputExampleFrame;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Input example")
public class InputExample extends CorePage {

    @Name("Input example frame")
    @FindBy(xpath = "/html/body/h1/..")
    public InputExampleFrame inputExampleFrame;

}
