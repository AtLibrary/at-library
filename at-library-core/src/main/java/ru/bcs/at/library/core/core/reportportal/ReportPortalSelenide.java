package ru.bcs.at.library.core.core.reportportal;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import cucumber.api.Scenario;
import io.qameta.allure.selenide.LogType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReportPortalSelenide implements LogEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportPortalSelenide.class);
    private final Map<LogType, Level> logTypesToSave = new HashMap<>();
    private boolean saveScreenshots = true;
    private boolean savePageHtml = true;

    private static Optional<byte[]> getScreenshotBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted()
                    ? Optional.of(((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES))
                    : Optional.empty();
        } catch (WebDriverException e) {
            LOGGER.warn("Could not get screen shot", e);
            return Optional.empty();
        }
    }

    private static Optional<byte[]> getPageSourceBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted()
                    ? Optional.of(WebDriverRunner.getWebDriver().getPageSource().getBytes(UTF_8))
                    : Optional.empty();
        } catch (WebDriverException e) {
            LOGGER.warn("Could not get page source", e);
            return Optional.empty();
        }
    }

    private static String getBrowserLogs(final LogType logType, final Level level) {
        return String.join("\n\n", Selenide.getWebDriverLogs(logType.toString(), level));
    }

    public ReportPortalSelenide screenshots(final boolean saveScreenshots) {
        this.saveScreenshots = saveScreenshots;
        return this;
    }

    public ReportPortalSelenide savePageSource(final boolean savePageHtml) {
        this.savePageHtml = savePageHtml;
        return this;
    }

    public ReportPortalSelenide enableLogs(final LogType logType, final Level logLevel) {
        logTypesToSave.put(logType, logLevel);

        return this;
    }

    public ReportPortalSelenide disableLogs(final LogType logType) {
        logTypesToSave.remove(logType);

        return this;
    }

    @Override
    public void beforeEvent(final LogEvent event) {
    }

    @Override
    public void afterEvent(final LogEvent event) {
        Scenario scenario = CoreScenario.getInstance().getScenario();

        switch (event.getStatus()) {
            case PASS: {
                break;
            }
            case FAIL: {
                if (saveScreenshots) {
                    getScreenshotBytes()
                            .ifPresent(bytes -> scenario.embed(bytes, "image/png"));
                }
                if (savePageHtml) {
                    getPageSourceBytes()
                            .ifPresent(bytes -> scenario.embed(bytes, "text/html"));
                }
                if (!logTypesToSave.isEmpty()) {
                    logTypesToSave
                            .forEach((logType, level) -> {
                                final byte[] content = getBrowserLogs(logType, level).getBytes(UTF_8);
                                scenario.embed(content, "application/json");
                            });
                }
                break;
            }
            default: {
                LOGGER.warn("Step finished with unsupported status {}", event.getStatus());
                break;
            }
        }
    }
}
