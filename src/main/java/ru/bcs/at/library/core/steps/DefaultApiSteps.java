/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.steps;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadProperty;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveJsonVars;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveVars;

/**
 * Шаги для тестирования API, доступные по умолчанию в каждом новом проекте
 */

@Log4j2
public class DefaultApiSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Посылается http запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String method, String address, String variableName) throws Exception {
        Response response = sendRequest(method, address, null);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http запрос по заданному урлу с заданными параметрами.
     * И в URL, и в значениях в таблице можно использовать переменные и из application.properties, и из хранилища переменных
     * из CoreScenario. Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     * Content-Type при необходимости должен быть указан в качестве header.
     * Результат сохраняется в заданную переменную
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String method, String address, String variableName, DataTable paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http запрос по заданному урлу с заданными параметрами.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     * Content-Type при необходимости должен быть указан в качестве header.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void checkResponseCode(String method, String address, int expectedStatusCode, DataTable paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        assertTrue(checkStatusCode(response, expectedStatusCode));
    }

    @Тогда("^в json ответа \"([^\"]*)\" значения равны значениям из таблицы$")
    public void checkValuesInJson(String valueToFind, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(valueToFind);
        for (List<String> row : dataTable.asLists()) {
            String name = row.get(0);
            String value = row.get(1);
            response
                    .then()
                    .assertThat()
                    .body(name, equalTo(value));
        }
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сравниваются с ожидаемым значением во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     */
    @Тогда("^в json (?:строке|файле) \"([^\"]*)\" значения, найденные по jsonpath, равны значениям из таблицы$")
    public void checkValuesInJsonAsString(String jsonVar, DataTable dataTable) {
        String strJson = loadValueFromFileOrPropertyOrVariableOrDefault(jsonVar);
        Gson gsonObject = new Gson();
        JsonParser parser = new JsonParser();
        ReadContext ctx = JsonPath.parse(strJson, createJsonPathConfiguration());
        boolean error = false;
        for (List<String> row : dataTable.asLists()) {
            String jsonPath = row.get(0);
            JsonElement actualJsonElement;
            try {
                actualJsonElement = gsonObject.toJsonTree(ctx.read(jsonPath));
            } catch (PathNotFoundException e) {
                error = true;
                continue;
            }
            JsonElement expectedJsonElement = parser.parse(row.get(1));
            if (!actualJsonElement.equals(expectedJsonElement)) {
                error = true;
            }
            coreScenario.write("JsonPath: " + jsonPath + ", ожидаемое значение: " + expectedJsonElement + ", фактическое значение: " + actualJsonElement);
        }
        if (error)
            throw new RuntimeException("Ожидаемые и фактические значения в json не совпадают");
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     */
    @Тогда("^значения из json (?:строки|файла) \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
    public void getValuesFromJsonAsString(String jsonVar, DataTable dataTable) {
        String strJson = loadValueFromFileOrPropertyOrVariableOrDefault(jsonVar);
        Gson gsonObject = new Gson();
        ReadContext ctx = JsonPath.parse(strJson, createJsonPathConfiguration());
        boolean error = false;
        for (List<String> row : dataTable.asLists()) {
            String jsonPath = row.get(0);
            String varName = row.get(1);
            JsonElement jsonElement;
            try {
                jsonElement = gsonObject.toJsonTree(ctx.read(jsonPath));
            } catch (PathNotFoundException e) {
                error = true;
                continue;
            }
            coreScenario.setVar(varName, jsonElement.toString());
            coreScenario.write("JsonPath: " + jsonPath + ", значение: " + jsonElement + ", записано в переменную: " + varName);
        }
        if (error)
            throw new RuntimeException("В json не найдено значение по заданному jsonpath");
    }

    private Configuration createJsonPathConfiguration() {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new GsonJsonProvider())
                .mappingProvider(new GsonMappingProvider())
                .build();
    }

    /**
     * Создание запроса
     *
     * @param paramsTable массив с параметрами
     * @return сформированный запрос
     */
    private RequestSender createRequest(DataTable paramsTable) {
        String body = null;
        RequestSpecification request = given();

        if (paramsTable != null) {
            for (List<String> requestParam : paramsTable.asLists()) {
                String type = requestParam.get(0);
                String name = requestParam.get(1);
                String value =
                        loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(2));
                switch (type.toUpperCase()) {
                    case "ACCESS_TOKEN": {
                        request.header(name, "Bearer " + value.replace("\"", ""));
                        break;
                    }
                    case "PARAMETER": {
                        request.param(name, value);
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
     * Получает ответ и сохраняет в переменную
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getBodyAndSaveToVariable(String variableName, Response response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            coreScenario.setVar(variableName, response);
        } else {
            fail("Вернулся не корректный status code: " + response.statusCode() + "\n body: " + response.getBody().print());
        }
    }

    /**
     * Сравнение кода http ответа с ожидаемым
     *
     * @param response           ответ от сервиса
     * @param expectedStatusCode ожидаемый http статус код
     * @return возвращает true или false в зависимости от ожидаемого и полученного http кодов
     */
    public boolean checkStatusCode(Response response, int expectedStatusCode) {
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            coreScenario.write("Получен неверный статус код ответа " + statusCode + ". Ожидаемый статус код " + expectedStatusCode);
        }
        return statusCode == expectedStatusCode;
    }

    /**
     * Отправка http запроса
     *
     * @param method      тип http запроса
     * @param address     url, на который будет направлен запроc
     * @param paramsTable список параметров для http запроса
     */
    public Response sendRequest(String method, String address,
                                DataTable paramsTable) {
        address = loadProperty(address, resolveVars(address));
        RequestSender request = createRequest(paramsTable);
        return request.request(Method.valueOf(method), address);
    }
}