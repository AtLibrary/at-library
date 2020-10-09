package ru.at.library.web.page.itgeared;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Adding a Time Enabled Button")
public class ItGearedTimeout extends CorePage {

    @Name("View Example")
    @FindBy(css = "#main p > a")
    public SelenideElement viewExampleLink;

}
