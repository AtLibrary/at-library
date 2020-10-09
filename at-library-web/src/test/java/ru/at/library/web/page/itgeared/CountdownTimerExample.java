package ru.at.library.web.page.itgeared;

import org.openqa.selenium.support.FindBy;
import ru.at.library.web.block.itgeared.CountdownTimerBlock;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Countdown Timer")
public class CountdownTimerExample extends CorePage {

    @Name("Countdown Timer block")
    @FindBy(id = "wrapper")
    public CountdownTimerBlock countdownTimerBlock;

}
