package ru.at.library.api.steps;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.cucumber.java.ru.И;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Method;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.params.CoreConnectionPNames;
import ru.at.library.api.helpers.Utils;
import ru.at.library.core.utils.helpers.PropertyLoader;
import ru.at.library.core.utils.helpers.ScopedVariables;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static org.hamcrest.Matchers.is;

/**
 * Шаги по формированию и отправки запроса
 */
@Log4j2
public class RequestSteps {

    private static final int DEFAULT_TIMEOUT = PropertyLoader.loadPropertyInt("http.timeout", 10);
    private static final String REQUEST_URL = "выполнен ((?:GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH)) запрос на URL \"([^\"]+)\"";
    public static int requestRetries = Integer.parseInt(getProperty("request.retries", "1"));
    @Getter
    private static RequestSteps instance = new RequestSteps();
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Отправка http запроса по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     */
    @И("^" + REQUEST_URL + ". Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestWithoutParams(String method,
                                             String address,
                                             String responseNameVariable) {
        Response response = sendRequest(method, address, null);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^" + REQUEST_URL + " с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestSaveResponse(String method,
                                            String address,
                                            String responseNameVariable,
                                            DataTable dataTable) {
        Response response = sendRequest(method, address, dataTable);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode ожидаемый код ответа
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^" + REQUEST_URL + " с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void sendHttpRequestCheckResponseCode(String method,
                                                 String address,
                                                 int expectedStatusCode,
                                                 DataTable dataTable) {
        Response response = tryingSendRequestRetries(method, address, dataTable, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^" + REQUEST_URL + " с headers и parameters из таблицы. Ожидается код ответа: (\\d+) Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestSaveResponseCheckResponseCode(String method,
                                                             String address,
                                                             int expectedStatusCode,
                                                             String responseNameVariable,
                                                             DataTable dataTable) {
        Response response = tryingSendRequestRetries(method, address, dataTable, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     */
    @И("^" + REQUEST_URL + ". Ожидается код ответа: (\\d+) Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestSaveResponseCheckResponseCode(String method,
                                                             String address,
                                                             int expectedStatusCode,
                                                             String responseNameVariable) {
        Response response = tryingSendRequestRetries(method, address, null, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY периодично в заданный интервал времени
     * пока ответ не вернет ожидаемый statusCode или закончится время выполнения попыток.
     * Результат сохраняется в заданную переменную
     *
     * @param timeoutSec           время выполнения попыток запроса - таймаут попыток
     * @param periodSec            период попыток запроса - ожидание между попытками
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     */
    @И("^в течение (\\d+) секунд каждую (\\d+) " + REQUEST_URL + ". Ожидается код ответа: (\\d+). Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestPeriodicallySaveResponseCheckResponseCode(int timeoutSec,
                                                                         int periodSec,
                                                                         String method,
                                                                         String address,
                                                                         int expectedStatusCode,
                                                                         String responseNameVariable) {
        Response response = tryingPeriodicallySendRequestRetries(timeoutSec, periodSec, method, address, null,
                null, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY периодично в заданный интервал времени
     * пока ответ не вернет ожидаемый statusCode или закончится время выполнения попыток.
     * Результат сохраняется в заданную переменную
     *
     * @param timeoutSec           время выполнения попыток запроса - таймаут попыток
     * @param periodSec            период попыток запроса - ожидание между попытками
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^в течение (\\d+) секунд каждую (\\d+) " + REQUEST_URL + " с параметрами из таблицы. Ожидается код ответа: (\\d+). Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestPeriodicallySaveResponseCheckResponseCode(int timeoutSec,
                                                                         int periodSec,
                                                                         String method,
                                                                         String address,
                                                                         int expectedStatusCode,
                                                                         String responseNameVariable,
                                                                         DataTable dataTable) {
        Response response = tryingPeriodicallySendRequestRetries(timeoutSec, periodSec, method, address, dataTable, null, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса по заданному урлу с параметрами и/или BODY периодично в заданный интервал времени
     * пока ответ не вернет ожидаемый statusCode или закончится время выполнения попыток.
     * <p>
     * Параметры запроса и требуемые параметры ответа в таблице отделяются строкой:
     * \ RESPONSE \ \ \
     * Например:
     * \ BODY     \                \ test_text      \   - параметр запроса
     * \ HEADER   \ test_header    \ test_header    \   - параметр запроса
     * \ RESPONSE \                \                \   - строка разделитель
     * \ BODY     \                \ checked_body   \   - параметр ответа
     * \ HEADER   \ checked_header \ checked_header \   - параметр ответа
     * \ COOKIE   \ checked_cookie \ checked_cookie \   - параметр ответа
     * <p>
     * Результат сохраняется в заданную переменную
     *
     * @param timeoutSec           время выполнения попыток запроса - таймаут попыток
     * @param periodSec            период попыток запроса - ожидание между попытками
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^в течение (\\d+) секунд каждую (\\d+) " + REQUEST_URL + " с параметрами из таблицы. Ожидается код ответа: (\\d+) с параметрами из таблицы. Полученный ответ сохранен в переменную \"([^\"]+)\"$")
    public void sendHttpRequestPeriodicallySaveResponseCheckResponseParams(int timeoutSec,
                                                                           int periodSec,
                                                                           String method,
                                                                           String address,
                                                                           int expectedStatusCode,
                                                                           String responseNameVariable,
                                                                           DataTable dataTable) {
        DataTable respDataTable = null;
        if (dataTable.column(0).indexOf("RESPONSE") != -1) {
            respDataTable = dataTable.subTable(dataTable.column(0).indexOf("RESPONSE") + 1, 0, dataTable.height(), dataTable.width());
            dataTable = dataTable.subTable(0, 0, dataTable.column(0).indexOf("RESPONSE"), dataTable.width());
        }
        Response response = tryingPeriodicallySendRequestRetries(timeoutSec, periodSec, method, address, dataTable, respDataTable, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
        checkResponseByParams(response, respDataTable);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * Отправка http запроса
     *
     * @param method    тип http запроса
     * @param address   url, на который будет направлен запроc
     * @param dataTable список параметров для http запроса
     * @return Response
     */
    private Response sendRequest(String method, String address, DataTable dataTable) {
        address = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(address);
        if (parseBoolean(getProperty("relaxedHTTPSValidation", "false"))) {
            RestAssured.config =
                    RestAssuredConfig.newConfig()
                            .sslConfig(new SSLConfig().allowAllHostnames())
                            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
                            .httpClient(HttpClientConfig.httpClientConfig()
                                    .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_TIMEOUT * 1000)
                                    .setParam(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_TIMEOUT * 1000)
                            );
        }

        RequestSender request = createRequest(dataTable);
        return request.request(Method.valueOf(method), address);
    }

    /**
     * Создание запроса
     * Content-Type при необходимости должен быть указан в качестве header.
     *
     * @param dataTable массив с параметрами
     * @return сформированный запрос
     */
    private RequestSender createRequest(DataTable dataTable) {
        String body = null;
        RequestSpecification request = RestAssured.given();

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String type = requestParam.get(0);

                String name = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(1));
                String value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(2));
                value = PropertyLoader.loadValueFromFileOrVariableOrDefault(value);

                switch (type.toUpperCase()) {
                    case "BASIC_AUTHENTICATION": {
                        request.auth().basic(name, value);
                        break;
                    }
                    case "RELAXED_HTTPS": {
                        request.relaxedHTTPSValidation();
                        break;
                    }
                    case "ACCESS_TOKEN": {
                        request.header(name, "Bearer " + value.replace("\"", ""));
                        break;
                    }
                    case "PARAMETER": {
                        request.queryParam(name, value);
                        break;
                    }
                    case "MULTIPART": {
                        request.multiPart(name, value);
                        break;
                    }
                    case "FORM_PARAMETER": {
                        request.formParam(name, value);
                        break;
                    }
                    case "PATH_PARAMETER": {
                        request.pathParam(name, value);
                        break;
                    }
                    case "HEADER": {
                        request.header(name, value);
                        break;
                    }
                    case "COOKIES": {
                        Cookie myCookie = new Cookie.Builder(name, value).build();
                        request.cookie(myCookie);
                        break;
                    }
                    case "BODY": {
                        value = checkBody(value);
                        body = Utils.resolveJsonVars(value);
                        request.body(body);
                        break;
                    }
                    case "FILE": {
                        String filePath = PropertyLoader.loadProperty(value, ScopedVariables.resolveVars(value));
                        request.multiPart("file", new File(filePath), name);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException(format("Некорректно задан тип %s для параметра запроса %s ", type, name));
                    }
                }
            }
            if (body != null) {
                log.trace("Тело запроса:\n" + body);
            }
        }

        return request;
    }

    /**
     * Проверка параметров ответа.
     *
     * @param response  http-ответ
     * @param dataTable массив с параметрами
     */
    private void checkResponseByParams(Response response, DataTable dataTable) {
        if (dataTable != null) {
            StringBuilder errorMessage = new StringBuilder();
            for (List<String> responseParam : dataTable.asLists()) {
                String type = responseParam.get(0);

                String name = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(responseParam.get(1));
                String value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(responseParam.get(2));
                value = PropertyLoader.loadValueFromFileOrVariableOrDefault(value);

                try {
                    switch (type.toUpperCase()) {
                        case "HEADER": {
                            response.then().header(name, value);
                            break;
                        }
                        case "COOKIES": {
                            response.then().cookie(name, value);
                            break;
                        }
                        case "BODY": {
                            response.then().body(is(value));
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException(format("Некорректно задан тип %s для параметра ответа %s ", type, name));
                        }
                    }
                } catch (AssertionError e) {
                    errorMessage.append(e.getMessage());
                }
            }
            if (!errorMessage.toString().isEmpty()) {
                throw new AssertionError(errorMessage);
            }
        }
    }

    /**
     * Получает тело запроса
     * TODO разобраться с реализацией метода и его необходимость
     *
     * @param value
     */
    private String checkBody(String value) {
        URL url = PropertyLoader.class.getClassLoader().getResource(value);
        if (url != null) {
            try {
                value = Resources.toString(url, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(format("Ошибка чтения файла ресурса: %s", url.getPath()));
            }
        }
        return value;
    }

    /**
     * Получает ответ и сохраняет в переменную
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getBodyAndSaveToVariable(String variableName, Response response) {
        coreScenario.setVar(variableName, response);
    }

    /**
     * Запрос отправляется заданное количество попыток requestRetries пока не ответ не вернет ожидаемый statusCode или закончистя количество попыток
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode ожидаемый statusCode
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    private Response tryingSendRequestRetries(String method,
                                              String address,
                                              DataTable dataTable,
                                              int expectedStatusCode) {
        Response response = null;
        for (int i = 0; i < requestRetries; i++) {
            response = sendRequest(method, address, dataTable);
            if (response.statusCode() == expectedStatusCode) {
                break;
            }
        }
        return response;
    }

    /**
     * Запрос отправляется периодично в заданный интервал времени пока ответ не вернет ожидаемый statusCode или закончится время выполнения попыток
     *
     * @param timeoutSec         время выполнения попыток запроса - таймаут попыток
     * @param periodSec          период попыток запроса - ожидание между попытками
     * @param method             методов HTTP запроса
     * @param address            url запроса (можно задать как напрямую в шаге, так и указав в properties)
     * @param expectedStatusCode ожидаемый statusCode
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    private Response tryingPeriodicallySendRequestRetries(int timeoutSec,
                                                          int periodSec,
                                                          String method,
                                                          String address,
                                                          DataTable dataTable,
                                                          DataTable respDataTable,
                                                          int expectedStatusCode) {
        Response response = null;

        long startTime = System.currentTimeMillis();
        long endTimeTime = startTime + timeoutSec * 1000;
        while (System.currentTimeMillis() < endTimeTime) {
            response = sendRequest(method, address, dataTable);
            if (response.statusCode() == expectedStatusCode) {
                try {
                    checkResponseByParams(response, respDataTable);
                    break;
                } catch (AssertionError e) {
                    // ignore because it's intermediate result
                }
            }
            if (System.currentTimeMillis() + periodSec * 1000 > endTimeTime) {
                break;
            }
            try {
                Thread.sleep(periodSec * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

}
