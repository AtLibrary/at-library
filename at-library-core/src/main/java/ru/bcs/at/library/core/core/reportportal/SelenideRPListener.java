package ru.bcs.at.library.core.core.reportportal;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import cucumber.api.Scenario;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.nio.charset.StandardCharsets;

/**
 * Прикрепление скриншота экрана к отчету на ReportPortal при падении теста
 */
@Log4j2
//todo  вывод лога web-драйвера в РП - driver.manage().logs()
public class SelenideRPListener implements LogEventListener {

    private boolean saveScreenshots = true;
    private boolean savePageHtml = true;

    private static byte[] getScreenshotBytes() {
        return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    private static byte[] getPageSourceBytes() {
        return WebDriverRunner.getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    public SelenideRPListener screenshots(final boolean saveScreenshots) {
        this.saveScreenshots = saveScreenshots;
        return this;
    }

    public SelenideRPListener savePageSource(final boolean savePageHtml) {
        this.savePageHtml = savePageHtml;
        return this;
    }

    @Override
    public void afterEvent(LogEvent logEvent) {
        onEvent(logEvent);
    }

    @Override
    public void beforeEvent(LogEvent logEvent) {

    }

    public void onEvent(final LogEvent event) {
        Scenario scenario = CoreScenario.getInstance().getScenario();
        if (scenario == null) {
            return; //**QUIT**
        }
        if (LogEvent.EventStatus.FAIL.equals(event.getStatus())) { //todo сохранение скрина на каждом шаге
            if (saveScreenshots) {
                scenario.embed(getScreenshotBytes(), "image/png");
            }
            if (savePageHtml) {
                scenario.embed(getPageSourceBytes(), "text/html");
            }
        }
    }

}
