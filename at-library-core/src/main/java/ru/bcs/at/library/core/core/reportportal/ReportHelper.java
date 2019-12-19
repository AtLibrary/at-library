/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bcs.at.library.core.core.reportportal;

import com.epam.reportportal.cucumber.Utils;
import cucumber.api.Scenario;
import io.qameta.allure.Allure;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

/**
 * служебный класс для вывода дополнительной информации в отчет allure и на ReportPortal.
 */
public final class ReportHelper {
    private ReportHelper() {
    }

    /**
     * приложить файл к отчету (allure и report-portal)
     *
     * @param name      название файла
     * @param fileBytes содержимое файла
     * @param mimeType  тип (mime) файла
     * @param fileExt   расширение файла
     */
    public static void attachFile2Report(String name, final byte[] fileBytes, String mimeType, String fileExt) {
        Allure.addByteAttachmentAsync(name, mimeType, fileExt, () -> fileBytes);

        // вывод в reportPortal
        Scenario scenario = CoreScenario.getInstance().getScenario();
        scenario.embed(fileBytes, Utils.makeFileDef(mimeType, name, "INFO"));
    }
}
