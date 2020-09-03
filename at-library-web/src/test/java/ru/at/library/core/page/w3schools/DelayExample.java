package ru.at.library.core.page.w3schools;

import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.w3schools.DelayExampleFrame;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Delay example")
public class DelayExample extends CorePage {

    @Name("Delay example frame")
    @FindBy(xpath = "/html/body/p/..")
    public DelayExampleFrame delayExampleFrame;

}
