package ru.bcs.at.library.api; /**
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
import org.hamcrest.Matchers;
import org.junit.Assert;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;
import ru.bcs.at.library.core.helpers.PropertyLoader;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.specification.ProxySpecification.host;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    @И("^выполнен (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
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
    @И("^выполнен (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
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
    @И("^выполнен (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
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
    @И("^выполнен (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+) Полученный ответ сохранен в переменную \"([^\"]*)\"$")
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
                    PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));

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
     * значения найденные по jsonPath из json ответа "ПЕРВЫЙ" равны значениям из json ответа "ВТОРОЙ"</p>
     *
     * @param nameResponseOne имя первого ответа
     * @param nameResponseTwo имя второго ответа ответа
     * @param dataTable       список jsonpath ключей
     */
    @Тогда("^значения найденные по jsonPath из json ответа \"([^\"]*)\" равны значениям из json ответа \"([^\"]*)\"$")
    public void valuesFoundByPathEqual(String nameResponseOne, String nameResponseTwo, DataTable dataTable) {
        Response response1 = (Response) CoreScenario.getInstance().getVar(nameResponseOne);
        Response response2 = (Response) CoreScenario.getInstance().getVar(nameResponseTwo);

        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String actualValue = response1.jsonPath().getString(path);
            String expectedValue = response2.jsonPath().getString(path);

            Assert.assertNotNull(
                    "Содержимое по jsonpath: " + path +
                            "\nВ ответе: " + nameResponseOne +
                            "\nНе должно быть: null ",
                    actualValue);
            Assert.assertNotNull(
                    "Содержимое по jsonpath: " + path +
                            "\nВ ответе: " + nameResponseTwo +
                            "\nНе должно быть: null ",
                    expectedValue);

            Assert.assertEquals(
                    "Содержимое по jsonpath: " + path + " не равно" +
                            "\nВ ответе: " + expectedValue +
                            "\nВ переменной: " + actualValue +
                            "\n",
                    expectedValue, actualValue);
        }

    }

//    /**
//     * <p style="color: green; font-size: 1.5em">
//     * элемент найденный по jsonPath из json ответа содержит указанное количество элементов</p>
//     *
//     * @param nameResponse имя ответа
//     * @param dataTable    список jsonpath ключей
//     */
//    @Тогда("^элемент найденный по jsonPath из json ответа \"([^\"]*)\" содержит указанное количество элементов$")
//    public void valuesFoundByPathContainElementCounts(String nameResponse, DataTable dataTable) {
//        Response response = (Response) CoreScenario.getInstance().getVar(nameResponse);
//
//        for (List<String> row : dataTable.asLists()) {
//            String path = row.get(0);
//            int count = Integer.parseInt(row.get(1));
//
//            String actualValue = response.jsonPath().getString(path);
//            //TODO Объект по jsonPath привести к массиву и получить его длину, сравнить с ожидаемой, вывести в ошибку
//
//            Assert.assertNotNull(
//                    "Содержимое по jsonpath: " + path +
//                            "\nВ ответе: " + nameResponse +
//                            "\nНе должно быть: null ",
//                    actualValue);
//
//            //if(  != count){
//            //    throw new RuntimeException( "jsonpath: " + path + " содержит не верное количество элементов.\nВ ответе: " +  +"\nОжидалось:"+count);
//            //}
//        }
//
//    }

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
     * Сравнение body json ответа с ожидаемым</p>
     *
     * @param variableName     переменная в которой сохранен Response
     * @param pathExpectedJson путь к json файлу
     */
    @И("^json в ответе \"([^\"]*)\" равен json: \"([^\"]*)\"")
    public void checkResponseJson(String variableName, String pathExpectedJson) {
        String json = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        json = ScopedVariables.resolveJsonVars(json);
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);
        response
                .then().body(
                Matchers.equalTo(json)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сравнение кода http ответа с ожидаемым</p>
     *
     * @param variableName переменная в которой сохранен Response
     * @param dataTable    массив с параметрами
     */
    @И("^в ответе \"([^\"]*)\" содержатся header со значениями из таблицы$")
    public void checkResponseHeaderValues(String variableName, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);

        for (List<String> row : dataTable.asLists()) {
            String header = row.get(0);
            String headerValue = row.get(1);

            if (header.isEmpty() || headerValue.isEmpty()) {
                throw new RuntimeException("Header и значение не могут быть пустыми");
            }
            checkHeaderValue(response, header, headerValue);
        }

    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка что массив найденный по ключу responseName содержит value</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param key          ключ поиска массива
     * @param value        ожидаемое значенме
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" массив содержит \"([^\"]*)\"$")
    public void checkArrayHasItem(String responseName, String key, String value) {
        value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);
        response
                .then()
                .body(key, Matchers.hasItem(value));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Все объекты в коллекции имеют поле с определенным названием, содержащим конкретное значение</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param key          ключ поиска массива
     * @param value        ожидаемое значенме
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" весь массив соотвествует \"([^\"]*)\"$")
    public void checkArrayEqualAllItem(String responseName, String key, String value) {
        value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);

        List<String> list = response
                .getBody().jsonPath().getList(key);

        for (String actualValue : list) {
            if (!actualValue.equals(value)) {
                throw new AssertionError(
                        "Найденный по ключу" + key +
                                "\n список: " + list +
                                "\n содержит значения которые не equals: " + value);
            }
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка что массив найденный по ключу key размером = value</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param key          ключ поиска массива
     * @param value        size
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" размер массива \"([^\"]*)\"$")
    public void checkArraySize(String responseName, String key, String value) {
        value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);
        MatcherAssert.assertThat(response.jsonPath().getList(key).size(), Matchers.equalTo(Integer.valueOf(value)));
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
                PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(expectedJsonSchema);

        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(expectedJsonSchema));
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
        proxyHost = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(proxyHost);
        proxyPort = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(proxyPort);

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        RestAssured.proxy = ProxySpecification.host(proxyHost).withPort(Integer.valueOf(proxyPort));

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

    @И("^переменная \"([^\"]*)\" содержир base64 кодирование, декодирована в pdf и сохранена по пути \"([^\"]*)\" с именем \"([^\"]*)\" в формате \"([^\"]*)\"$")
    public void saveBase64ToPdf(String encodeBytes, String path, String fName, String fFormat) throws IOException {
        String base64Code = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(encodeBytes);
        String fileName = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(fName);
        String fileFormat = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(fFormat);
        String pathToSave = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(path) + "\\" + fileName + "." + fileFormat;

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(base64Code);

        File file = new File(pathToSave);
        FileOutputStream fop = new FileOutputStream(file);

        fop.write(decodedBytes);
        fop.flush();
        fop.close();
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
     * Сравнение кода http ответа с ожидаемым</p>
     *
     * @param response            ответ от сервиса
     * @param expectedHeader      ожидаемый Header
     * @param expectedHeaderValue ожидаемое содержание Header
     */
    private void checkHeaderValue(Response response, String expectedHeader, String expectedHeaderValue) {
        response.then().assertThat().header(expectedHeader, expectedHeaderValue);
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
        address = PropertyLoader.loadProperty(address, ScopedVariables.resolveVars(address));
        RestAssured.config =
                RestAssuredConfig.newConfig().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        RequestSender request = createRequest(dataTable);
        return request.request(Method.valueOf(method), address);
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
        RequestSpecification request = RestAssured.given();

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String type = requestParam.get(0);
                String name = requestParam.get(1);
                String value =
                        PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(2));
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
                        body = ScopedVariables.resolveJsonVars(value);
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
}
