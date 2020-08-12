package ru.at.library.core.core.helpers;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.core.log.Log4jRestAssuredFilter;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class LogReportListener {

    private static boolean turn = false;

    private LogReportListener() {
    }

    /**
     * Добавляет фильтры логирования.
     * <ul>
     * <li> лог (Log4j2)</li>
     * <li> reportPortal</li>
     * <li> отчет allure</li>
     * </ul>
     */
    public synchronized static void turnOn() {
        turnListenerSelenide();
        if (!turn) {
            turnListenerRestAssured();
            turn = true;
        }
    }

    private synchronized static void turnListenerSelenide() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
//                        .enableLogs(LogType.BROWSER, Level.ALL)
        );
        log.trace("Включен слушатель Selenide в Allure");
    }

    private synchronized static void turnListenerRestAssured() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Log4jRestAssuredFilter());
        log.trace("Включен слушатель rest-assured в log4j");

        filters.add(new AllureRestAssured());
        log.trace("Включен слушатель rest-assured в Allure");

        RestAssured.filters(filters);
    }
}
