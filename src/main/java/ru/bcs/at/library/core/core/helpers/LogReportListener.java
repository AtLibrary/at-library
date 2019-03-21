package ru.bcs.at.library.core.core.helpers;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import lombok.extern.log4j.Log4j2;
import ru.bcs.at.library.core.log.Log4jRestAssuredFilter;
import ru.bcs.at.library.core.reportportal.ReportPortalRestAssuredFilter;
import ru.bcs.at.library.core.reportportal.SelenideRPListener;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class LogReportListener {

    private volatile static boolean enabled = false;

    /**
     * Добавляет фильтры логирования.
     * <ul>
     * <li> лог (Log4j2)</li>
     * <li> reportPortal</li>
     * <li> отчет allure</li>
     * </ul>
     */
    public static synchronized void turnOn() {
        if (!enabled) {
            SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
            SelenideLogger.addListener("RPSelenide", new SelenideRPListener().screenshots(true).savePageSource(false));

            List<Filter> filters = new ArrayList<>();
            filters.add(new Log4jRestAssuredFilter());
            log.debug("Включен вывод rest-assured в log4j");

            filters.add(new AllureRestAssured());
            log.debug("Включен вывод rest-assured в Allure");

            filters.add(new ReportPortalRestAssuredFilter());
            log.debug("Включен вывод rest-assured в ReportPortal");

            RestAssured.filters(filters);
            enabled = true;
        }
    }

    private LogReportListener() {
    }
}
