package ru.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;

import static com.codeborne.selenide.Configuration.browser;

@Log4j2
public class InitialDriver {
    /**
     * Создание WebDriver
     */
    public void startUITest(Scenario scenario) {

        /**
         * Создает настойки прокси для запуска драйвера
         */
        Proxy proxy = createProxy();

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart(proxy, scenario);
        } else {
            initRemoteStart(proxy, scenario);
        }
    }

    private void initLocalStart(Proxy proxy, Scenario scenario) {
        log.info(String.format("%s: ОС: %s", scenario.getId(), System.getProperty("os.name")));
        log.info(String.format("%s: локальный бразуер: %s", scenario.getId(), browser));
        if (proxy != null) {
            WebDriverRunner.setProxy(proxy);
            log.trace(String.format("%s: Проставлена прокси: %s", scenario.getId(), proxy));
        }

    }

    private void initRemoteStart(Proxy proxy, Scenario scenario) {
        log.info(String.format("%s: удаленная машина: %s", scenario.getId(), Configuration.remote));
        log.info(String.format("%s: браузер: %s", scenario.getId(), browser));

        Configuration.browserCapabilities.setCapability("enableVNC", true);
        Configuration.browserCapabilities.setCapability("enableVideo", false);
        Configuration.browserCapabilities.setCapability("name", scenario.getName());

        if (proxy != null) {
            Configuration.browserCapabilities.setCapability(CapabilityType.PROXY, proxy);
            log.trace(String.format("%s: Проставлена прокси: %s", scenario.getId(), proxy));
        }
    }

    private Proxy createProxy() {
        Proxy proxy = null;
        String stringProxy = System.getProperty("selenoid.proxy");

        if (Strings.isNullOrEmpty(stringProxy)) {
            stringProxy = System.getProperty("proxy");
        }

        if (!Strings.isNullOrEmpty(stringProxy)) {
            proxy = new Proxy()
                    .setProxyType(Proxy.ProxyType.MANUAL)
                    .setHttpProxy(stringProxy)
                    .setFtpProxy(stringProxy)
                    .setSslProxy(stringProxy)
            ;
        }
        return proxy;
    }
}
