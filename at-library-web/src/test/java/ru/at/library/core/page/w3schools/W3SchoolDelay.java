package ru.at.library.core.page.w3schools;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("W3 Delay")
public class W3SchoolDelay extends CorePage {

    @Name("Try it Yourself")
    @FindBy(css = "[class=\"w3-example\"] a")
    public SelenideElement toDelayExampleBtn;

}
