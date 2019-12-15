package ru.bcs.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import io.appium.java_client.AppiumDriver;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URI;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

@Log4j2
public class InitialDriver {
    /**
     * <p>Создание WebDriver</p>
     */

    private String testDevice;

    public void startUITest(Scenario scenario) throws MalformedURLException {
        testDevice = scenarioTestDevice(scenario);

        /**
         * Создает настойки прокси для запуска драйвера
         */
        Proxy proxy = creteProxy();

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart(proxy);
        } else {
            initRemoteStart(proxy, scenario);
        }
    }

    private void initLocalStart(Proxy proxy) {
        log.info("Тесты будут запущены на операционной системе: " + System.getProperty("os.name"));
        log.info("Тесты будут запущены локально в браузере: " + browser);
        boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");

        if (windows && browser.equals(CHROME)) {
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("useAutomationExtension", false);

            WebDriverRunner.setWebDriver(new ChromeDriver(options));
        }

        if (proxy != null) {
            WebDriverRunner.setProxy(proxy);
            log.info("Проставлена прокси: " + proxy);
        }

    }

    private void initRemoteStart(Proxy proxy, Scenario scenario) throws MalformedURLException {
        log.info("Тесты запущены на удаленной машине: " + Configuration.remote);
        log.info("Тесты будут запущены в браузере: " + browser);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        capabilities.setCapability("name", scenario.getName());

        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy);
            log.info("Проставлена прокси: " + proxy);
        }

        if (testDevice.equals("web")) {
            Configuration.browserSize = "1920x1080";
            capabilities.setBrowserName(Configuration.browser);
            capabilities.setCapability("sessionTimeout", "5m");

            Configuration.browserCapabilities = capabilities;
        }

        if (testDevice.equals("mobile")) {
            capabilities.setCapability(PLATFORM_NAME, AtCoreConfig.platformName);
            capabilities.setCapability("deviceName", AtCoreConfig.deviceName);
            capabilities.setCapability("platformVersion", AtCoreConfig.platformVersion);
            capabilities.setCapability("app", AtCoreConfig.app);

            setWebDriver(
                    new AppiumDriver(
                            URI.create(Configuration.remote).toURL(),
                            capabilities));
        }

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
        }

        return proxy;
    }

    private String scenarioTestDevice(Scenario scenario) {
        boolean web = scenario.getSourceTagNames().contains("@web");
        boolean mobile = scenario.getSourceTagNames().contains("@mobile");
        if (web && mobile) {
            Assert.fail("Сценарий подписан тегом @web и @mobile . Подпишите одним из них");
        }
        if (web) {
            return "web";
        }
        if (mobile) {
            return "mobile";
        }
        return null;
    }


}
