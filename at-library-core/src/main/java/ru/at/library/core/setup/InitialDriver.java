package ru.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import io.cucumber.java.Scenario;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.Calendar;

import static com.codeborne.selenide.Configuration.browser;

@Log4j2
public class InitialDriver {
    /**
     * Создание WebDriver
     */
    @Step("Запуск UI теста")
    public void startUITest(Scenario scenario, int testNumber) throws Exception {

        /**
         * Создает настойки прокси для запуска драйвера
         */
//        Proxy proxy = createProxy();

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart(scenario, testNumber);
        } else {
            initRemoteStart(scenario, testNumber);
        }
    }

    @Step("Запуск теста локально")
    private void initLocalStart(Scenario scenario, int testNumber) throws Exception {
        log.info(String.format("%s: ОС: %s", scenario.getId(), System.getProperty("os.name")));
        log.info(String.format("%s: локальный бразуер: %s", scenario.getId(), browser));
//        if (proxy != null) {
//            WebDriverRunner.setProxy(proxy);
//            log.trace(String.format("%s: Проставлена прокси: %s", scenario.getId(), proxy));
//        }

    }

    @Step("Запуск теста удаленно")
    private void initRemoteStart(Scenario scenario, int testNumber) throws Exception {
        log.info(String.format("%s: удаленная машина: %s", scenario.getId(), Configuration.remote));
        log.info(String.format("%s: браузер: %s", scenario.getId(), Configuration.browser));

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(Configuration.browser);
        if (System.getProperty("version") != null && (!System.getProperty("version").isEmpty())) {
            capabilities.setVersion(System.getProperty("version"));
        }
        capabilities.setCapability("enableVNC",
                Boolean.parseBoolean(System.getProperty("enableVNC", "false"))
        );
        capabilities.setCapability("enableVideo",
                Boolean.parseBoolean(System.getProperty("enableVideo", "false"))
        );
        capabilities.setCapability("name", "[" + testNumber + "]" + scenario.getName());
        capabilities.setCapability("screenResolution", "1900x1080x24");
        capabilities.setCapability("browserstack.timezone", "Moscow");
        WebDriverRunner.setWebDriver(new RemoteWebDriver(
                URI.create(Configuration.remote).toURL(),
                capabilities
        ));
    }

//    private Proxy createProxy() {
//        Proxy proxy = null;
//        String stringProxy = System.getProperty("selenoid.proxy");
//
//        if (Strings.isNullOrEmpty(stringProxy)) {
//            stringProxy = System.getProperty("proxy");
//        }
//
//        if (!Strings.isNullOrEmpty(stringProxy)) {
//            proxy = new Proxy()
//                    .setProxyType(Proxy.ProxyType.MANUAL)
//                    .setHttpProxy(stringProxy)
//                    .setFtpProxy(stringProxy)
//                    .setSslProxy(stringProxy)
//            ;
//        }
//        return proxy;
//    }
}
