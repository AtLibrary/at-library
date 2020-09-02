package ru.at.library.core.page.w3schools;

import org.openqa.selenium.support.FindBy;
import ru.at.library.core.block.w3schools.FadeOutExampleFrame;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 FadeOut example")
public class FadeOutExample extends CorePage {

    @Name("FadeOut example frame")
    @FindBy(xpath = "/html/body/p/..")
    public FadeOutExampleFrame fadeOutExampleFrame;

}
