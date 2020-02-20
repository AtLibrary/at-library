package ru.bcs.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.net.MalformedURLException;
import java.net.URI;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

@Log4j2
public class InitialDriver {
    /**
     * Создание WebDriver
     */

    private String testDevice;

    public void startUITest(Scenario scenario) throws MalformedURLException {
        testDevice = scenarioTestDevice(scenario);

        /**
         * Создает настойки прокси для запуска драйвера
         */
        Proxy proxy = createProxy();

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

    private int getFreeDevice(String platformName) {
        if (platformName.equals("Android")) {
            synchronized (CoreScenario.deviceAvailability) {
                for (int i = 0; i < 5; i++) {
                    if (CoreScenario.deviceAvailability[i]) {
                        return ++i;
                    }
                }
                return 0;
            }
        } else {
            synchronized (CoreScenario.deviceAvailability) {
                for (int i = 5; i < 7; i++) {
                    if (CoreScenario.deviceAvailability[i]) {
                        return ++i;
                    }
                }
                return 0;
            }
        }
    }

    private void initRemoteStart(Proxy proxy, Scenario scenario) throws MalformedURLException {
        log.info("Тесты запущены на удаленной машине: " + Configuration.remote);

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", false);
        capabilities.setCapability("name", scenario.getName());

        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy);
            log.info("Проставлена прокси: " + proxy);
        }

        if (testDevice.equals("web") || (testDevice.equals("mobile_web") && AtCoreConfig.deviceName == null)) {
            Configuration.browserSize = "1920x1080";
            log.info("Тесты будут запущены в браузере: " + browser);

            capabilities.setBrowserName(Configuration.browser);
            capabilities.setCapability("sessionTimeout", "5m");

            Configuration.browserCapabilities = capabilities;
        }

        if (testDevice.equals("mobile")) {
            if (AtCoreConfig.platformVersion != null &&
                    AtCoreConfig.deviceName != null &&
                    AtCoreConfig.udid != null &&
                    AtCoreConfig.app != null) {
                capabilities.setCapability("platformVersion", AtCoreConfig.platformVersion);
                capabilities.setCapability("deviceName", AtCoreConfig.deviceName);
                capabilities.setCapability("udid", AtCoreConfig.udid);
                capabilities.setCapability("app", AtCoreConfig.app);
            } else {
//                System.out.println(scenario.getName());
                while (getFreeDevice(AtCoreConfig.platformName) == 0) {
                    try {
                        Thread.sleep(15000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (CoreScenario.deviceAvailability) {
                    switch (getFreeDevice(AtCoreConfig.platformName)) {
                        case 1:
                            capabilities.setCapability("platformVersion", "10");
                            capabilities.setCapability("deviceName", "samsung_galaxyS10_stand");
                            capabilities.setCapability("udid", "RF8M914RW9M");
                            capabilities.setCapability("app", "/Users/user/Desktop/apps/bcs-premier-premier-debug.apk");
                            CoreScenario.deviceAvailability[0] = false;
                            CoreScenario.deviceCases[0] = scenario.getId();
                            break;
                        case 2:
                            capabilities.setCapability("platformVersion", "9");
                            capabilities.setCapability("deviceName", "samsung_galaxyA40_stand");
                            capabilities.setCapability("udid", "R58M800LYDX");
                            capabilities.setCapability("app", "/home/bcsatplatform/apps/bcs-premier-premier-debug.apk");
                            CoreScenario.deviceAvailability[1] = false;
                            CoreScenario.deviceCases[1] = scenario.getId();
                            break;
                        case 3:
                            capabilities.setCapability("platformVersion", "9");
                            capabilities.setCapability("deviceName", "xiaomi_redmi8pro_stand");
                            capabilities.setCapability("udid", "lfcyfidmsg9pj7lz");
                            capabilities.setCapability("app", "/home/bcsatplatform/apps/bcs-premier-premier-debug.apk");
                            CoreScenario.deviceAvailability[2] = false;
                            CoreScenario.deviceCases[2] = scenario.getId();
                            break;
                        case 4:
                            capabilities.setCapability("platformVersion", "9");
                            capabilities.setCapability("deviceName", "honor_10lite_stand");
                            capabilities.setCapability("udid", "VFFNW19910001467");
                            capabilities.setCapability("app", "/home/bcsatplatform/apps/bcs-premier-premier-debug.apk");
                            CoreScenario.deviceAvailability[3] = false;
                            CoreScenario.deviceCases[3] = scenario.getId();
                            break;
                        case 5:
                            capabilities.setCapability("platformVersion", "9");
                            capabilities.setCapability("deviceName", "nokia_2.2_stand");
                            capabilities.setCapability("udid", "HZAL1670CAJ61717143");
                            capabilities.setCapability("app", "/home/bcsatplatform/apps/bcs-premier-premier-debug.apk");
                            CoreScenario.deviceAvailability[4] = false;
                            CoreScenario.deviceCases[4] = scenario.getId();
                            break;
                        case 6:
                            capabilities.setCapability("platformVersion", "13.3");
                            capabilities.setCapability("deviceName", "iPhone_11_stand");
                            capabilities.setCapability("udid", "00008030-000E51982298802E");
                            capabilities.setCapability("app", "/Users/user/Desktop/apps/BCS-Bank.ipa");
                            capabilities.setCapability("automationName", "XCUITest");
                            capabilities.setCapability("wdaLocalPort", "8101");
                            CoreScenario.deviceAvailability[5] = false;
                            CoreScenario.deviceCases[5] = scenario.getId();
                            break;
                        case 7:
                            capabilities.setCapability("platformVersion", "13.3");
                            capabilities.setCapability("deviceName", "iPhone_Xs_stand");
                            capabilities.setCapability("udid", "00008020-001405C03479002E");
                            capabilities.setCapability("app", "/Users/user/Desktop/apps/BCS-Bank.ipa");
                            capabilities.setCapability("automationName", "XCUITest");
                            capabilities.setCapability("wdaLocalPort", "8102");
                            CoreScenario.deviceAvailability[6] = false;
                            CoreScenario.deviceCases[6] = scenario.getId();
                            break;
//                        case 8:
//                            capabilities.setCapability("platformVersion", "13.3");
//                            capabilities.setCapability("deviceName", "iPhone_8plus_stand");
//                            capabilities.setCapability("udid", "32f97e07a99bace47d1edbf5b0ce294e2bc23d6e");
//                            capabilities.setCapability("app", "/Users/user/Desktop/apps/BCS-Bank.ipa");
//                            capabilities.setCapability("automationName", "XCUITest");
//                            CoreScenario.deviceAvailability[7] = false;
//                            CoreScenario.deviceCases[7] = scenario.getId();
//                            break;
//                        case 9:
//                            capabilities.setCapability("platformVersion", "13.3");
//                            capabilities.setCapability("deviceName", "iPad_2019_stand");
//                            capabilities.setCapability("udid", "424f06c9978b960e7477e0e4ffc37035964d0242");
//                            capabilities.setCapability("app", "/Users/user/Desktop/apps/BCS-Premier.ipa");
//                            capabilities.setCapability("automationName", "XCUITest");
//                            CoreScenario.deviceAvailability[8] = false;
//                            CoreScenario.deviceCases[8] = scenario.getId();
//                            break;
                        default:
                            log.warn("Нет доступных устройств!!!");
                            initRemoteStart(proxy, scenario);
                            return;
                    }
                }
            }
            log.info("Тесты будут запущены на мобильном устройстве: " + capabilities.getCapability("deviceName"));

            capabilities.setCapability(PLATFORM_NAME, AtCoreConfig.platformName);
//            capabilities.setCapability("platformVersion", AtCoreConfig.platformVersion);
//            capabilities.setCapability("version", AtCoreConfig.platformVersion);
//            capabilities.setCapability("app", AtCoreConfig.app);
//            capabilities.setCapability("wdaLocalPort", AtCoreConfig.wdaLocalPort);
//            capabilities.setCapability("showLogCode", AtCoreConfig.showLogCode);
//            capabilities.setCapability("clearSystemFiles", AtCoreConfig.clearSystemFiles);
//            capabilities.setCapability("automationName", AtCoreConfig.automationName);
//            capabilities.setCapability("webDriverAgentUrl", AtCoreConfig.webDriverAgentUrl);

            setWebDriver(
                    new AppiumDriver<>(
                            URI.create(Configuration.remote).toURL(),
                            capabilities));
        }

        if (testDevice.equals("mobile_web")) {
            log.info("Тесты будут запущены на мобильном устройстве: " + AtCoreConfig.deviceName);
            log.info("Тесты будут запущены в браузере: " + browser);

            capabilities.setCapability("deviceName", AtCoreConfig.deviceName);
            capabilities.setCapability("udid", AtCoreConfig.udid);
            capabilities.setCapability(PLATFORM_NAME, AtCoreConfig.platformName);
            capabilities.setCapability("platformVersion", AtCoreConfig.platformVersion);
            capabilities.setBrowserName(Configuration.browser);
            capabilities.setCapability("version", AtCoreConfig.version);

            Configuration.browserCapabilities = capabilities;

            setWebDriver(
                    new AndroidDriver<>(
                            URI.create(Configuration.remote).toURL(),
                            capabilities));
        }

    }

    private Proxy createProxy() {
        Proxy proxy = null;
        //TODO переработать использование прокси при запуске тестов на селенойд или гитлаб
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

    private String scenarioTestDevice(Scenario scenario) {
        boolean web = scenario.getSourceTagNames().contains("@web");
        boolean mobile = scenario.getSourceTagNames().contains("@mobile");
        boolean mobileWeb = scenario.getSourceTagNames().contains("@mobile_web");

        if (mobileWeb) {
            return "mobile_web";
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
