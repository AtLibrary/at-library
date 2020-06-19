package ru.at.library.core.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("ВТБ")
public class VTBPage extends CorePage {

    @Name("ВТБ-Онлайн")
    @FindBy(css="[href=\"https://www.vtb.ru/app\"]")
    public SelenideElement buttonOnlineVtb;
}
