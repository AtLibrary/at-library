package ru.at.library.core.setup;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import io.cucumber.java.Scenario;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.codeborne.selenide.Configuration.browser;
import static ru.at.library.core.setup.InitialSetupSteps.getScenarioId;

@Log4j2
public class InitialDriver {
    /**
     * Создание WebDriver
     */
    @Step("Запуск UI теста")
    public void startUITest(Scenario scenario, int testNumber) throws Exception {

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart(getScenarioId(scenario));
        } else {
            initRemoteStart(getScenarioId(scenario), scenario.getName(), testNumber);
        }
    }

    @Step("Запуск UI теста")
    public void startUITest(String scenarioId, String scenarioName, int testNumber) throws Exception {

        /**
         * Уведомление о месте запуска тестов
         */
        if (Strings.isNullOrEmpty(Configuration.remote)) {
            initLocalStart(scenarioId);
        } else {
            initRemoteStart(scenarioId, scenarioName, testNumber);
        }
    }

    @Step("Запуск теста локально")
    private void initLocalStart(String scenarioId){
        log.info(String.format("%s: ОС: %s", scenarioId, System.getProperty("os.name")));
        log.info(String.format("%s: локальный бразуер: %s", scenarioId, browser));
    }

    @Step("Запуск теста удаленно")
    private void initRemoteStart(String scenarioId, String scenarioName, int testNumber) throws Exception {
        log.info(String.format("%s: удаленная машина: %s", scenarioId, Configuration.remote));
        log.info(String.format("%s: браузер: %s", scenarioId, Configuration.browser));

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
        capabilities.setCapability("name", "[" + testNumber + "]" + scenarioName);
        capabilities.setCapability("screenResolution", "1900x1080x24");
        capabilities.setCapability("browserstack.timezone", "Moscow");
        capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
        capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        if (System.getProperty("disableChromeFileViewer", "true").equals("true")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("prefs", new HashMap<String, Object>() {
                {
                    put("profile.default_content_settings.popups", 0);
                    put("download.prompt_for_download", false);
                    put("download.directory_upgrade", true);
                    put("safebrowsing.enabled", false);
                    put("plugins.always_open_pdf_externally", true);
                    put("plugins.plugins_disabled", new ArrayList<String>() {
                        {
                            add("Chrome PDF Viewer");
                        }
                    });
                }
            });
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }
        WebDriverRunner.setWebDriver(new RemoteWebDriver(
                URI.create(Configuration.remote).toURL(),
                capabilities
        ));
    }
}
