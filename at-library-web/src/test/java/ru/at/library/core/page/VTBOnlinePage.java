package ru.at.library.core.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;
@Name("ВТБ-Онлайн")
public class VTBOnlinePage extends CorePage {

    @Name("Номер карты")
    @FindBy(css = "[name=\"login\"]")
    public SelenideElement inputNumberCart;

    @Name("Далее")
    @FindBy(css = "[class=\"button-1Mx\"]")
    public SelenideElement buttonNext;

    @Name("Вход в систему доступен только клиентам ВТБ")
    @FindBy(css = "[class=\"error-message-box-3vK operation-form__error-2HN\"]")
    public SelenideElement textError;
}
