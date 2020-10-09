package ru.at.library.web.block.itgeared;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Countdown Timer block")
public class CountdownTimerBlock extends CorePage {

    @Name("Disabled button")
    @FindBy(css = "button#myBtn")
    public SelenideElement disabledBtn;

}
