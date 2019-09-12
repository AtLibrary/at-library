package ru.bcs.at.library.core.steps;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Method;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import ru.bcs.at.library.core.core.helpers.PropertyLoader;
import ru.bcs.at.library.core.cucumber.ScopedVariables;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;

public class RequestSteps {

    private static final String REQUEST_URL = "^выполнен (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) запрос на URL \"([^\"]*)\"";
    public static int requestRetries = Integer.parseInt(System.getProperty("request.retries", "1"));
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Отправка http запроса по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в application.properties)
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     */
    @И(REQUEST_URL + ". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String method,
                                             String address,
                                             String responseNameVariable) {
        Response response = sendRequest(method, address, null);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * <p>Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в application.properties)
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И(REQUEST_URL + " с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String method,
                                            String address,
                                            String responseNameVariable,
                                            DataTable dataTable) {
        Response response = sendRequest(method, address, dataTable);
        getBodyAndSaveToVariable(responseNameVariable, response);
    }

    /**
     * <p>Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (можно задать как напрямую в шаге, так и указав в application.properties)
     * @param expectedStatusCode ожидаемый код ответа
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И(REQUEST_URL + " с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void sendHttpRequestCheckResponseCode(String method,
                                                 String address,
                                                 int expectedStatusCode,
                                                 DataTable dataTable) {
        Response response = tryingSendRequestRetries(method, address, dataTable, expectedStatusCode);
        response.then().statusCode(expectedStatusCode);
    }

    /**
     * <p>Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method               методов HTTP запроса
     * @param address              url запроса (можно задать как напрямую в шаге, так и указав в application.properties)
     * @param expectedStatusCode   ожидаемый код ответа
     * @param responseNameVariable имя переменной в которую сохраняется ответ
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И(REQUEST_URL + " с headers и parameters из таблицы. Ожидается код ответа: (\\d+) Полученный ответ сохранен в переменную \"([^\"]*)\"$")
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
     * <p>Отправка http запроса</p>
     *
     * @param method    тип http запроса
     * @param address   url, на который будет направлен запроc
     * @param dataTable список параметров для http запроса
     * @return Response
     */
    private Response sendRequest(String method, String address, DataTable dataTable) {
        address = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(address);
        RestAssured.config =
                RestAssuredConfig.newConfig().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        RequestSender request = createRequest(dataTable);
        return request.request(Method.valueOf(method), address);
    }

    /**
     * <p>Создание запроса
     * Content-Type при необходимости должен быть указан в качестве header.</p>
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
                        body = ScopedVariables.resolveJsonVars(value);
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
                coreScenario.write("Тело запроса:\n" + body);
            }
        }

        return request;
    }

    /**
     * <p>Получает тела запроса</p>
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
     * <p>Получает ответ и сохраняет в переменную</p>
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getBodyAndSaveToVariable(String variableName, Response response) {
        coreScenario.setVar(variableName, response);
    }

    /**
     * <p>Запрос отправляется заданное количество попыток requestRetries пока не ответ не вернет ожидаемый statusCode или закончистя количество попыток</p>
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (можно задать как напрямую в шаге, так и указав в application.properties)
     * @param expectedStatusCode ожидаемый statusCode
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
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

}
