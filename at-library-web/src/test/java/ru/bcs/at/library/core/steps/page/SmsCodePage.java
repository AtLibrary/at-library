package ru.bcs.at.library.core.steps.page;


import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("Окно ввода SMS-кода")
public class SmsCodePage extends CorePage {

    @Name("Поле ввода SMS-код")
    @FindBy(css = ".become-demo__sms .ui-placeholder__text")
    private SelenideElement inputSmsCode;


}

