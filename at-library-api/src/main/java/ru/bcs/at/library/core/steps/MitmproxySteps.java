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
    private RequestSteps requestSteps = RequestSteps.getInstance();

    private static final String MITM_CLIENT_HOST = System.getProperty("mitmClientHost", tryLoadProperty("mitmClientHost"));
    private static final String MITM_CLIENT_PORT = System.getProperty("mitmClientPort", tryLoadProperty("mitmClientPort"));
    private static final String MITM_CLIENT_PATH = System.getProperty("mitmClientPath", tryLoadProperty("mitmClientPath"));

    @И("^из кэша mitmproxy получено последнее отправленное http-сообщение(?: с ожиданием до (\\d+) секунд|)$")
    public void getMessage(Integer sec) {
        getMessage(null, null, sec);
    }

    @И("^из кэша mitmproxy получено последнее отправленное http-сообщение на url, содержащий \"([^\"]+)\"(?:, с ожиданием до (\\d+) секунд|)$")
    public void getMessage(String key, Integer sec) {
        getMessage(key, null, sec);
    }

    @И("^из кэша mitmproxy получено последнее отправленное http-сообщение на url, содержащий \"([^\"]+)\", и сохранено в переменную \"([^\"]+)\"(?:, с ожиданием до (\\d+) секунд|)$")
    public void getMessage(String key, String requestNameVariable, Integer sec) {
        if (requestNameVariable == null) {
            requestNameVariable = CoreScenario.CURRENT;
        }
        String requestAddress;
        if (key != null && !key.isEmpty()) {
            requestAddress = format("%s:%s%s/last?key=%s", MITM_CLIENT_HOST, MITM_CLIENT_PORT, MITM_CLIENT_PATH, key);
        } else {
            requestAddress = format("%s:%s%s/last", MITM_CLIENT_HOST, MITM_CLIENT_PORT, MITM_CLIENT_PATH);
        }

        if (sec != null) {
            requestSteps.sendHttpRequestPeriodicallySaveResponseCheckResponseCode(
                    sec,
                    1,
                    "GET",
                    requestAddress,
                    200,
                    TEMP_RESPONSE
            );
        } else {
            requestSteps.sendHttpRequestWithoutParams(
                    "GET",
                    requestAddress,
                    TEMP_RESPONSE
            );
        }
        requestSteps.sendHttpRequestWithoutParams("GET", format("%s:%s/last", MITM_CLIENT_HOST, MITM_CLIENT_PORT), CoreScenario.TEMP_RESPONSE);

        Response response = (Response) coreScenario.getVar(CoreScenario.TEMP_RESPONSE);
        coreScenario.setVar(requestNameVariable, response.body().print());
    }

    @И("^очищен кэш mitmproxy http-сообщений$")
    public void cleanMessageCache() {
        requestSteps.sendHttpRequestWithoutParams(
                "DELETE",
                format("%s:%s%s/clean", MITM_CLIENT_HOST, MITM_CLIENT_PORT, MITM_CLIENT_PATH),
                null
        );
    }

}
