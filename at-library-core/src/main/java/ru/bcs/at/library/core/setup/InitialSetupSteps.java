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
package ru.bcs.at.library.core.setup;

import com.codeborne.selenide.Selenide;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.appium.java_client.AppiumDriver;
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

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.remote.BrowserType.SAFARI;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * Начальная настройка
 */
@Log4j2
public class InitialSetupSteps {

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
     * Действия выполняемые перед каждым сценарием
     * Включение слушателей Allure
     * Если сценарий содержит тег @web" то будет создан WebDriver
     * Создает окружение(среду) для запуска сценария
     */
    @Before
    public void beforeEachTest(Scenario scenario) throws MalformedURLException {

//        try {
//            Thread.sleep(15000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("beforeEachTest start: " + scenario.getName());
        RestAssured.baseURI = System.getProperty("baseURI", tryLoadProperty("baseURI"));
        baseUrl = System.getProperty("baseURI", tryLoadProperty("baseURI"));

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
        coreScenario.setEnvironment(new CoreEnvironment(scenario));

        LogReportListener.turnOn();
//        System.out.println("beforeEachTest end: " + scenario.getName());
    }

    /**
     * Если сценарий содержит тег @web" то по завершению теста удаляет все куки и закрывает веб-браузер
     */
    @After
    public void afterEachTest(Scenario scenario) {

//        System.out.println("afterEachTest start: " + scenario.getName());
        if (scenario.getSourceTagNames().contains("@web")) {
            if (browser.equals(SAFARI)) {
                getWebDriver().quit();
            }
            getWebDriver().close();
        }
        if (scenario.getSourceTagNames().contains("@mobile")
                && !scenario.getSourceTagNames().contains("@web")) {
            AppiumDriver appiumDriver = (AppiumDriver) getWebDriver();
            appiumDriver.closeApp();
            appiumDriver.removeApp(AtCoreConfig.appPackageName);
            appiumDriver.quit();
        }

        for (int i = 0; i < 5; i++) {
            if (scenario.getId().equals(CoreScenario.deviceCases[i])) {
                CoreScenario.deviceAvailability[i] = true;
                CoreScenario.deviceCases[i] = null;
            }
        }

//        System.out.println("afterEachTest end: " + scenario.getName());
    }
}
