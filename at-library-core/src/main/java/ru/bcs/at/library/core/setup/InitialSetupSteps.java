/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p style="color: green; font-size: 1.5em">
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p style="color: green; font-size: 1.5em">
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.qameta.allure.Attachment;
import io.restassured.RestAssured;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.logging.LogType;
import ru.bcs.at.library.core.core.helpers.LogReportListener;
import ru.bcs.at.library.core.cucumber.api.CoreEnvironment;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * Начальная настройка
 * </h1>
 */
@Log4j2
public class InitialSetupSteps {

    /**
     * <p>Включение слушателей Allure</p>
     */
    static {
        LogReportListener.turnOn();
    }

    @Delegate
    CoreScenario coreScenario = CoreScenario.getInstance();

    @Attachment(value = "Web Driver Logs", type = "text/plain", fileExtension = ".log")
    private static String attachmentWebDriverLogs() {
        /**
         * Чтоб все логи консоли успели загрузится
         */
        Selenide.sleep(1000);
        List<String> webDriverLogs = Selenide.getWebDriverLogs(LogType.BROWSER, Level.ALL);
        StringBuilder stringBuilder = new StringBuilder();
        for (String log : webDriverLogs) {
            stringBuilder.append(log);
            stringBuilder.append("\n\n");
            CoreScenario.getInstance().write(log);
        }

        return stringBuilder.toString();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Действия выполняемые перед каждым сценарием</p>
     * <p>Включение слушателей Allure</p>
     * <p>Если сценарий содержит тег @web" то будет создан WebDriver</p>
     * <p>Создает окружение(среду) для запуска сценария</p>
     */
    @Before
    public void beforeEachTest(Scenario scenario) throws MalformedURLException {
        RestAssured.baseURI = System.getProperty("baseURI", tryLoadProperty("baseURI"));
        Configuration.baseUrl = System.getProperty("baseURI", tryLoadProperty("baseURI"));

        /**
         * Если сценарий содержит тег @web" то будет создан WebDriver
         */
        boolean uiTest =
                scenario.getSourceTagNames().contains("@web") ||
                        scenario.getSourceTagNames().contains("@mobile");
        if (uiTest) {
            new InitialDriver().startUITest(scenario);
        }

        /**
         * Создает окружение(среду) для запуска сценария
         *
         * @param scenario сценарий
         * @throws Exception
         */
        coreScenario.setEnvironment(new CoreEnvironment(scenario, uiTest));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Если сценарий содержит тег @web" то по завершению теста удаляет все куки и закрывает веб-браузер</p>
     */
    @After
    public void afterEachTest(Scenario scenario) {
        /**
         * Очищает окружение(среду) по окончанию сценария
         */
        coreScenario.removeEnvironment();

        if (scenario.getSourceTagNames().contains("@web")) {
            attachmentWebDriverLogs();
            Selenide.clearBrowserLocalStorage();
            Selenide.clearBrowserCookies();
            WebDriverRunner.getWebDriver().close();
        }
        if (scenario.getSourceTagNames().contains("@mobile")) {
            WebDriverRunner.getWebDriver().quit();
        }
    }
}
