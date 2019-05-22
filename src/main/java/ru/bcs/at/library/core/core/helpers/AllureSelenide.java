package ru.bcs.at.library.core.core.helpers;


import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class AllureSelenide implements LogEventListener {
    private boolean saveScreenshots;
    private boolean savePageHtml;
    private final AllureLifecycle lifecycle;

    public AllureSelenide() {
        this(Allure.getLifecycle());
    }

    public AllureSelenide(AllureLifecycle lifecycle) {
        this.saveScreenshots = true;
        this.savePageHtml = true;
        this.lifecycle = lifecycle;
    }

    public AllureSelenide screenshots(boolean saveScreenshots) {
        this.saveScreenshots = saveScreenshots;
        return this;
    }

    public AllureSelenide savePageSource(boolean savePageHtml) {
        this.savePageHtml = savePageHtml;
        return this;
    }

    public void onEvent(LogEvent event) {
        this.lifecycle.getCurrentTestCase().ifPresent((uuid) -> {
            String stepUUID = UUID.randomUUID().toString();
            this.lifecycle.startStep(stepUUID, (new StepResult()).setName(event.toString()).setStatus(Status.PASSED));
            this.lifecycle.updateStep((stepResult) -> {
                stepResult.setStart(stepResult.getStart() - event.getDuration());
            });
            if (
                    EventStatus.FAIL.equals(event.getStatus())||
                    EventStatus.IN_PROGRESS.equals(event.getStatus())||
                    EventStatus.PASS.equals(event.getStatus())

            ) {
                if (this.saveScreenshots) {
                    this.lifecycle.addAttachment("Screenshot", "image/png", "png", getScreenshotBytes());
                }

                if (this.savePageHtml) {
                    this.lifecycle.addAttachment("Page source", "text/html", "html", getPageSourceBytes());
                }

                this.lifecycle.updateStep((stepResult) -> {
                    StatusDetails details = (StatusDetails)ResultsUtils.getStatusDetails(event.getError()).orElse(new StatusDetails());
                    stepResult.setStatus((Status)ResultsUtils.getStatus(event.getError()).orElse(Status.BROKEN));
                    stepResult.setStatusDetails(details);
                });
            }

            this.lifecycle.stopStep(stepUUID);
        });
    }

    private static byte[] getScreenshotBytes() {
        return (byte[])((TakesScreenshot)WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    private static byte[] getPageSourceBytes() {
        return WebDriverRunner.getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }
}

