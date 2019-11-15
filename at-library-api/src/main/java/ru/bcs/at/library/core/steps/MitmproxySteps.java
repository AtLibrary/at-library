package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import io.restassured.response.Response;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static java.lang.String.format;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * <p>Проверка http/https-запросов/ответов через прозрачный прокси сервер Mitmproxy</p>
 */
public class MitmproxySteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();
    private RequestSteps requestSteps = new RequestSteps();
    private static final String MITM_CLIENT_HOST = System.getProperty("mitmClientHost", tryLoadProperty("mitmClientHost"));
    private static final String MITM_CLIENT_PORT = System.getProperty("mitmClientPort", tryLoadProperty("mitmClientPort"));
    private static final String TEMP_RESPONSE = "TEMP_RESPONSE";

    @И("^получено последнее http-сообщение на url, содержащий \"([^\"]+)\"$")
    public void getMessage(String key) {
        getMessage(key, null);
    }

    @И("^получено последнее http-сообщение на url, содержащий \"([^\"]+)\" и сохранено в переменную \"([^\"]+)\"$")
    public void getMessage(String key, String requestNameVariable) {
        if (requestNameVariable == null) {
            requestNameVariable = CoreScenario.CURRENT;
        }
        requestSteps.sendHttpRequestWithoutParams("GET", format("%s:%s/last", MITM_CLIENT_HOST, MITM_CLIENT_PORT), TEMP_RESPONSE);

        Response response = (Response) coreScenario.getVar(TEMP_RESPONSE);
        coreScenario.setVar(requestNameVariable, response.body().print());
    }

}
