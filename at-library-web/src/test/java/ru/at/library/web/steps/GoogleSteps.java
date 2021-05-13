package ru.at.library.web.steps;

import io.cucumber.java.ru.И;
import ru.at.library.web.step.corepage.CorePageStep;
import ru.at.library.web.step.selenideelement.SelenideElementActionSteps;

public class GoogleSteps {

    @И("создание пользователя с ФИО: \"([^\"]*)\" телефон: \"([^\"]*)\" email: \"([^\"]*)\"")
    public void loginSystem(String fio, String password, String email) {
        CorePageStep corePageStep = new CorePageStep();
        corePageStep.loadPage("BCS demo аккаунт");

        SelenideElementActionSteps selenideElementActionSteps = new SelenideElementActionSteps();
        selenideElementActionSteps.setFieldValue("ФИО", fio);
        selenideElementActionSteps.setFieldValue("Номер телефона", password);
        selenideElementActionSteps.setFieldValue("Email", email);
        selenideElementActionSteps.clickOnElement("Открыть счет");
    }

}
