package ru.bcs.at.library.core.core.helpers;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.selenide.LogType;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import ru.bcs.at.library.core.core.log.Log4jRestAssuredFilter;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LogReportListener {

    private static boolean turn = false;

    private LogReportListener() {
    }

    /**
     * Добавляет фильтры логирования.
     * <ul>
     * <li> лог (Log4j2)</li>
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
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .enableLogs(LogType.BROWSER, Level.ALL)
        );
        CoreScenario.getInstance().write("Включен слушатель Selenide в Allure");
    }

    private synchronized static void turnListenerRestAssured() {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Log4jRestAssuredFilter());
        CoreScenario.getInstance().write("Включен слушатель rest-assured в log4j");

        filters.add(new AllureRestAssured());
        CoreScenario.getInstance().write("Включен слушатель rest-assured в Allure");

        RestAssured.filters(filters);
    }
}
