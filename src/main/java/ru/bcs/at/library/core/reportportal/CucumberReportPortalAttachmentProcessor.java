package ru.bcs.at.library.core.reportportal;

import com.epam.reportportal.cucumber.Utils;
import io.qameta.allure.attachment.AttachmentContent;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.AttachmentRenderer;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.nio.charset.StandardCharsets;

import static ru.bcs.at.library.core.reportportal.ReportPortalRestAssuredFilter.ATTACHMENT_LOG_LEVEL;


public class CucumberReportPortalAttachmentProcessor implements AttachmentProcessor<AttachmentData> {
    @Override
    public void addAttachment(AttachmentData attachmentData, AttachmentRenderer<AttachmentData> renderer) {
        final AttachmentContent content = renderer.render(attachmentData);

        CoreScenario.getInstance().getScenario().embed(content.getContent().getBytes(StandardCharsets.UTF_8),
                Utils.makeFileDef(content.getContentType(), attachmentData.getName(), ATTACHMENT_LOG_LEVEL));
    }
}
