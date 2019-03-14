/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p style="color: green; font-size: 1.5em">
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p style="color: green; font-size: 1.5em">
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.specification.ProxySpecification.host;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadProperty;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveJsonVars;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveVars;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * API шаги
 * </h1>
 */
@Log4j2
public class ApiSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p style="color: green; font-size: 1.5em">
     * Отправка http запроса по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method       методов HTTP запроса
     * @param address      url запроса (ожно задать как напрямую в шаге, так и указав в application.properties)
     * @param variableName имя переменной в которую сохраняется ответ
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String method, String address, String variableName) {
        Response response = sendRequest(method, address, null);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method       методов HTTP запроса
     * @param address      url запроса (ожно задать как напрямую в шаге, так и указав в application.properties)
     * @param variableName имя переменной в которую сохраняется ответ
     * @param dataTable    И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                     и из хранилища переменных из CoreScenario.
     *                     Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String method, String address, String variableName, DataTable dataTable) {
        Response response = sendRequest(method, address, dataTable);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (ожно задать как напрямую в шаге, так и указав в application.properties)
     * @param expectedStatusCode ожидаемый код ответа
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void sendHttpRequestCheckResponseCode(String method, String address, int expectedStatusCode, DataTable dataTable) {
        Response response = sendRequest(method, address, dataTable);
        checkStatusCode(response, expectedStatusCode);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Отправка http запроса по заданному урлу с параметрами и/или BODY.
     * Результат сохраняется в заданную переменную</p>
     *
     * @param method             методов HTTP запроса
     * @param address            url запроса (ожно задать как напрямую в шаге, так и указав в application.properties)
     * @param expectedStatusCode ожидаемый код ответа
     * @param variableName       имя переменной в которую сохраняется ответ
     * @param dataTable          И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                           и из хранилища переменных из CoreScenario.
     *                           Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+) Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponseCheckResponseCode(String method, String address, int expectedStatusCode, String variableName, DataTable dataTable) {
        Response response = sendRequest(method, address, dataTable);
        checkStatusCode(response, expectedStatusCode);
        getBodyAndSaveToVariable(variableName, response);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка Response</p>
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @Тогда("^в (json|xml) ответа \"([^\"]*)\" значения равны(|, без учета регистра,) значениям из таблицы$")
    public void checkValuesBody(String typeContentBody, String valueToFind, String textRegister, DataTable dataTable) {
        //TODO посмотреть есть ли более изящное решение проверки boolean
        Response response = (Response) CoreScenario.getInstance().getVar(valueToFind);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String expectedValue =
                    loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));

            String actualValue = "";

            if (typeContentBody.equals("json")) {
                actualValue = response.jsonPath().getString(path);
            } else if (typeContentBody.equals("xml")) {
                actualValue = response.xmlPath().getString(path);
            }

            if (!textRegister.isEmpty()) {
                expectedValue = expectedValue.toLowerCase();
                actualValue = actualValue.toLowerCase();
            }

            Assert.assertEquals(
                    "Содержимое по " + typeContentBody + "path:" + path + " не равно" +
                            "\nожидаемое: " + expectedValue +
                            "\nреальное: " + actualValue +
                            "\n",
                    expectedValue, actualValue);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.</p>
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @Тогда("^значения из (json|xml) ответа \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
    public void getValuesFromBodyAsString(String typeContentBody, String valueToFind, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(valueToFind);

        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String varName = row.get(1);

            String value = null;

            if (typeContentBody.equals("json")) {
                value = response.jsonPath().getString(path);
            } else if (typeContentBody.equals("xml")) {
                value = response.xmlPath().getString(path);
            }

            if (value == null) {
                throw new RuntimeException("В " + typeContentBody.toUpperCase() + " не найдено значение по заданному jsonpath: " + path);
            }

            coreScenario.setVar(varName, value);
            coreScenario.write(typeContentBody.toUpperCase() + " path: " + path + ", значение: " + value + ", записано в переменную: " + varName);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Создание запроса
     * Content-Type при необходимости должен быть указан в качестве header.</p>
     *
     * @param dataTable массив с параметрами
     * @return сформированный запрос
     */
    private RequestSender createRequest(DataTable dataTable) {
        String body = null;
        RequestSpecification request = given();

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String type = requestParam.get(0);
                String name = requestParam.get(1);
                String value =
                        loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(2));
                switch (type.toUpperCase()) {
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
                    case "BODY": {
                        body = resolveJsonVars(value);
                        request.body(body);
                        break;
                    }

                    default: {
                        throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", type, name));
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
     * <p style="color: green; font-size: 1.5em">
     * Сравнение кода http ответа с ожидаемым</p>
     *
     * @param variableName       переменная в которой сохранен Response
     * @param expectedStatusCode ожидаемый http статус код
     */
    @И("^в ответе \"([^\"]*)\" statusCode: (\\d+)$")
    public void checkResponseStatusCode(String variableName, int expectedStatusCode) {
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);
        checkStatusCode(response, expectedStatusCode);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка что тело ответа соответсвует json схеме</p>
     *
     * @param variableName       переменная в которой сохранен Response
     * @param expectedJsonSchema путь до .json файла со схемой
     */
    @И("^ответ \"([^\"]*)\" соответсвует json схеме: \"([^\"]*)\"$")
    public void verifyingResponseMatchesJsonScheme(String variableName, String expectedJsonSchema) {
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);
        expectedJsonSchema =
                loadValueFromFileOrPropertyOrVariableOrDefault(expectedJsonSchema);

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(expectedJsonSchema));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Используется прокси</p>
     *
     * @param proxyHost адрес proxy, например: s-nsk-proxy-01.global.bcs
     * @param proxyPort порт proxy, например: 8080
     */
    @И("^используется proxy: \"([^\"]*)\" port: \"([^\"]*)\"$")
    public void turnOnProxy(String proxyHost, String proxyPort) {
        proxyHost = loadValueFromFileOrPropertyOrVariableOrDefault(proxyHost);
        proxyPort = loadValueFromFileOrPropertyOrVariableOrDefault(proxyPort);

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        RestAssured.proxy = host(proxyHost).withPort(Integer.valueOf(proxyPort));

    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выключить использование прокси</p>
     */
    @И("^выключено использование proxy$")
    public void turnOffProxy() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");

        RestAssured.proxy = null;
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Получает ответ и сохраняет в переменную</p>
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getBodyAndSaveToVariable(String variableName, Response response) {
        coreScenario.setVar(variableName, response);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сравнение кода http ответа с ожидаемым</p>
     *
     * @param response           ответ от сервиса
     * @param expectedStatusCode ожидаемый http статус код
     */
    private void checkStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Отправка http запроса</p>
     *
     * @param method    тип http запроса
     * @param address   url, на который будет направлен запроc
     * @param dataTable список параметров для http запроса
     * @return Response
     */
    private Response sendRequest(String method, String address,

                                 DataTable dataTable) {
        address = loadProperty(address, resolveVars(address));
        RestAssured.config =
                newConfig().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        RequestSender request = createRequest(dataTable);
        return request.request(Method.valueOf(method), address);
    }
}