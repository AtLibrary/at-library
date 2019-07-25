package ru.bcs.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;

import static com.codeborne.selenide.Browsers.*;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

@Log4j2
public class InitialDriver {
    /**
     * <p style="color: green; font-size: 1.5em">
     * Создание WebDriver</p>
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

        /**
         * Устанавливает разрешения экрана
         */
        if (testDevice.equals("web")) {
            WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    private void initLocalStart(Proxy proxy) {
        log.info("Тесты будут запущены на операционной системе: " + System.getProperty("os.name"));
        log.info("Тесты будут запущены локально в браузере: " + browser);
        boolean linuxOS = System.getProperty("os.name").equals("Linux");


        if (linuxOS) {
            switch (browser) {
                case CHROME: {
                    WebDriverManager.chromedriver().setup();
                    WebDriverRunner.setWebDriver(new ChromeDriver());
                    break;
                }
                case FIREFOX: {
                    WebDriverManager.firefoxdriver().setup();
                    WebDriverRunner.setWebDriver(new FirefoxDriver());
                    break;
                }
                case OPERA: {
                    WebDriverManager.operadriver().setup();
                    WebDriverRunner.setWebDriver(new OperaDriver());
                    break;
                }
            }
        } else {
            switch (browser) {
                case INTERNET_EXPLORER: {
                    WebDriverRunner.setWebDriver(new InternetExplorerDriver());
                    break;
                }
                case CHROME: {
                    ChromeOptions options = new ChromeOptions();
                    options.setExperimentalOption("useAutomationExtension", false);
                    WebDriverRunner.setWebDriver(new ChromeDriver(options));
                    break;
                }
                case FIREFOX: {
                    WebDriverRunner.setWebDriver(new FirefoxDriver());
                    break;
                }
                case OPERA: {
                    WebDriverRunner.setWebDriver(new OperaDriver());
                    break;
                }
            }

            if (proxy != null) {
                WebDriverRunner.setProxy(proxy);
                log.info("Проставлена прокси: " + proxy);
            }

        }
    }

    private void initRemoteStart(Proxy proxy, Scenario scenario) throws MalformedURLException {
        log.info("Тесты запущены на удаленной машине: " + Configuration.remote);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        capabilities.setCapability("name", scenario.getName());

        if (testDevice.equals("web")) {
            capabilities.setBrowserName(Configuration.browser);
            capabilities.setCapability("screenResolution", "1920x1080");
            capabilities.setCapability("width", "1920");
            capabilities.setCapability("height", "1080");
        }

        if (testDevice.equals("mobile")) {
            capabilities.setCapability(PLATFORM_NAME, AtCoreConfig.platformName);
            capabilities.setCapability("deviceName", AtCoreConfig.deviceName);
            capabilities.setCapability("platformVersion", AtCoreConfig.platformVersion);
            capabilities.setCapability("app", AtCoreConfig.app);
        }

        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy);
            log.info("Проставлена прокси: " + proxy);
        }

        setWebDriver(
                new RemoteWebDriver(
                        URI.create(Configuration.remote).toURL(),
                        capabilities));
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
            Assert.fail("Сценарий подписан тегом @web и @mobile . Подпишите одним из низ");
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
