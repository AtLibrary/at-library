package ru.at.library.core.block.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("FadeOut example frame")
public class FadeOutExampleFrame extends CorePage {

    @Name("Click to fade out")
    @FindBy(tagName = "button")
    public SelenideElement fadeOutBtn;

    @Name("Blue box")
    @FindBy(id = "div3")
    public SelenideElement blueBoxDiv;

}
