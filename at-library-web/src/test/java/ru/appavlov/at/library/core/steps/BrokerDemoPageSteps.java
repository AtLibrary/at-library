package ru.appavlov.at.library.core.steps;

import cucumber.api.java.ru.И;
import ru.appavlov.at.library.core.WebActionSteps;

public class BrokerDemoPageSteps {

    @И("создание пользователя с ФИО: \"([^\"]*)\" телефон: \"([^\"]*)\" email: \"([^\"]*)\"")
    public void loginSystem(String fio, String password, String email) {
        WebActionSteps webActionSteps = new WebActionSteps();
        webActionSteps.loadPage("BCS demo аккаунт");

        webActionSteps.sendKeysCharacterByCharacter("ФИО", fio);
        webActionSteps.sendKeysCharacterByCharacter("Номер телефона", password);
        webActionSteps.sendKeysCharacterByCharacter("Email", email);
        webActionSteps.clickOnElement("Открыть счет");
    }

}
