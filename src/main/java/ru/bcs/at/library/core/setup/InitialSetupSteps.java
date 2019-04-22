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
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.restassured.RestAssured;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.bcs.at.library.core.core.helpers.LogReportListener;
import ru.bcs.at.library.core.cucumber.api.CoreEnvironment;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.net.MalformedURLException;
import java.net.URI;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.*;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadProperty;

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

    /**
     * <p style="color: green; font-size: 1.5em">
     * Действия выполняемые перед каждым сценарием</p>
     * <p>Включение слушателей Allure</p>
     * <p>Если сценарий содержит тег @web" то будет создан WebDriver</p>
     * <p>Создает окружение(среду) для запуска сценария</p>
     */
    @Before
    public void beforeEachTest(Scenario scenario) throws MalformedURLException {
        RestAssured.baseURI = System.getProperty("baseURI", loadProperty("baseURI"));
        Configuration.baseUrl = System.getProperty("baseURI", loadProperty("baseURI"));

        /**
         * Если сценарий содержит тег @web" то будет создан WebDriver
         */
        boolean webTest = scenario.getSourceTagNames().contains("@web");
        if (webTest) {
            startWebTest(scenario);
        }

        /**
         * Создает окружение(среду) для запуска сценария
         *
         * @param scenario сценарий
         * @throws Exception
         */
        coreScenario.setEnvironment(new CoreEnvironment(scenario, webTest));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Если сценарий содержит тег @web" то по завершению теста удаляет все куки и закрывает веб-браузер</p>
     */
    @After
    public void afterEachTest(Scenario scenario) {
        boolean webTest = scenario.getSourceTagNames().contains("@web");
        if (webTest) {
            Selenide.clearBrowserLocalStorage();
            Selenide.clearBrowserCookies();
            Selenide.close();
        }

        /**
         * Очищает окружение(среду) по окончанию сценария
         */
        coreScenario.removeEnvironment();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Создание WebDriver</p>
     */
    private void startWebTest(Scenario scenario) throws MalformedURLException {
        /**
         * Создает настойки прокси для запуска драйвера
         */
        Proxy proxy = creteProxy();

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart();
        } else {
            initRemoteStart(proxy, scenario);
        }
    }

    private void initRemoteStart(Proxy proxy, Scenario scenario) throws MalformedURLException {
        log.info("Тесты запущены на удаленной машине: " + Configuration.remote);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(browser);
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        capabilities.setCapability("screenResolution", "1920x1080");
        capabilities.setCapability("width", "1920");
        capabilities.setCapability("height", "1080");
        capabilities.setCapability("name", scenario.getName());

        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }

        setWebDriver(
                new RemoteWebDriver(
                        URI.create(Configuration.remote).toURL(),
                        capabilities));
    }

    private void initLocalStart() {
        log.info("Тесты будут запущены на операционной системе: " + System.getProperty("os.name"));
        log.info("Тесты будут запущены локально в браузере: " + browser);

        if (browser.equals(CHROME) && !System.getProperty("os.name").equals("Linux")) {
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("useAutomationExtension", false);
            WebDriverRunner.setWebDriver(
                    new ChromeDriver(options)
            );
        }
        /**
         * Устанавливает разрешения экрана
         */
        getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
    }

    private Proxy creteProxy() {
        Proxy proxy = null;
        String stringProxy = System.getProperty("proxy");
        if (!Strings.isNullOrEmpty(stringProxy)) {
            proxy = new Proxy()
                    .setProxyType(Proxy.ProxyType.MANUAL)
                    .setHttpProxy(stringProxy)
                    .setFtpProxy(stringProxy)
                    .setSslProxy(stringProxy)
            ;
            setProxy(proxy);
            log.info("Проставлена прокси: " + proxy);
        }

        return proxy;
    }


}
