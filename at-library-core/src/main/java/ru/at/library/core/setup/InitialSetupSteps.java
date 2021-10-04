/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.utils.helpers.AssertionHelper;
import ru.at.library.core.cucumber.api.CoreEnvironment;
import ru.at.library.core.cucumber.api.CoreScenario;

import static ru.at.library.core.utils.helpers.PropertyLoader.tryLoadProperty;

/**
 * Начальная настройка
 */
@Log4j2
public class InitialSetupSteps {

    public volatile static int scenarioNumber = 1;

    @Delegate
    CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Действия выполняемые перед каждым сценарием
     * Включение слушателей Allure
     * Если сценарий содержит тег @web" то будет создан WebDriver
     * Создает окружение(среду) для запуска сценария
     */
    @Before(order = 500)
    @Step("Запуск браузера")
    public void startUITestInBrowser(Scenario scenario) throws Exception {
        int testNumber = scenarioNumber++;;
        log.info(String.format("++++++++++++++++++++++++++ %s: Старт сценария %d с именем [%s] ++++++++++++++++++++++++++", getScenarioId(scenario), testNumber, scenario.getName()));

        RestAssured.baseURI = System.getProperty("baseURI", tryLoadProperty("baseURI"));
        Configuration.baseUrl = System.getProperty("baseURI", tryLoadProperty("baseURI"));

        /**
         * Если сценарий содержит тег @web" то будет создан WebDriver
         */
        new InitialDriver().startUITest(scenario,testNumber);

        /**
         * Создает окружение(среду) для запуска сценария
         *
         * @param scenario сценарий
         * @throws Exception
         */
        coreScenario.setEnvironment(new CoreEnvironment(scenario));
        coreScenario.setAssertionHelper(new AssertionHelper());
    }

    @After
    @Step("Закрытие браузера")
    public void endOfTest(Scenario scenario) {

        log.info(String.format("%s: %s Завершение сценария с именем [%s]",  getScenarioId(scenario), scenario.getStatus().name().toUpperCase(), scenario.getName()));
        tryingToCloseTheBrowser(doNeedToCloseTheBrowser(tryLoadProperty("ENVIRONMENT")));
        log.info(String.format("-------------------------- %s: Драйвер успешно остановлен --------------------------", getScenarioId(scenario)));
    }

    @Step("Браузер будет закрыт: {quitDriver}")
    private void tryingToCloseTheBrowser(boolean quitDriver) {
        if (quitDriver) {
            Selenide.closeWebDriver();
        }
    }

    @Step("В зависимости от стенда принимаете решение о закрытии браузера")
    private boolean doNeedToCloseTheBrowser(String environment) {
        if (environment == null) {
            return true;
        }

        boolean quitDriver = true;
        switch (environment) {
            case "dev": {
                break;
            }
            case "integrative": {
                break;
            }
            case "prod": {
                quitDriver = false;
                break;
            }
        }
        return quitDriver;
    }

    /**
     * Возврщает сокращенный ID сценария
     *
     * @return      ID сценария в формате feature_file.feature:ID
     */
    public static String getScenarioId(Scenario scenario) {
        String fullID = scenario.getId();
        return fullID.substring(fullID.lastIndexOf('/') + 1).replace(':', '_');
    }
}
