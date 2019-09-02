package ru.bcs.at.library.core.steps;

import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import ru.bcs.at.library.core.core.helpers.PropertyLoader;
import ru.bcs.at.library.core.cucumber.ScopedVariables;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static ru.bcs.at.library.core.cucumber.ScopedVariables.isJSONValid;

public class JsonVerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

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
    @И("^в (json|xml) ответа \"([^\"]*)\" значения равны(|, без учета регистра,) значениям из таблицы$")
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
    @И("^значения из (json|xml) ответа \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
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
     * Получение Cookies из ответа</p>
     *
     * @param valueToFind имя переменной которая содержит Response
     * @param dataTable   И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                    и из хранилища переменных из CoreScenario.
     *                    Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из cookies ответа \"([^\"]*)\", сохранены в переменные из таблицы$")
    public void getValuesFromCookiesAsString(String valueToFind, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(valueToFind);

        for (List<String> row : dataTable.asLists()) {
            String nameCookies = row.get(0);
            String varName = row.get(1);

            String value = response.cookie(nameCookies);

            if (value == null) {
                throw new RuntimeException("В " + response.getCookies() + " не найдено значение по заданному nameCookies: " + nameCookies);
            }

            coreScenario.setVar(varName, value);
            coreScenario.write("Значение Cookies с именем: " + nameCookies + " с value: " + value + ", записано в переменную: " + varName);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка json</p>
     */
    @И("^в json \"([^\"]*)\" значения равны(|, без учета регистра,) значениям из таблицы$")
    public void checkJson(String pathExpectedJson, String textRegister, DataTable dataTable) throws ParseException {
        String jsonString = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String expectedValue =
                    PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String actualValue = String.valueOf(read);


            if (!textRegister.isEmpty()) {
                expectedValue = expectedValue.toLowerCase();
                actualValue = actualValue.toLowerCase();
            }

            Assert.assertEquals(
                    "Содержимое по  jsonpath:" + path + " не равно" +
                            "\nожидаемое: " + expectedValue +
                            "\nреальное: " + actualValue +
                            "\n",
                    expectedValue, actualValue);
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
    @И("^значения найденные по jsonPath из json ответа \"([^\"]*)\" равны значениям из json ответа \"([^\"]*)\"$")
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
     * Создание json на основе json-шаблона</p>
     *
     * @param dataTable таблица с ключ знанчение для замены текста в json
     *                  обчная замена текста
     * @return сформированный json
     */
    //TODO проверить работу c XML
    @И("заполняю json-шаблон \"([^\"]*)\" данными из таблицы и сохраняю в переменную \"([^\"]*)\"")
    public void iFillInTheJsonTypeDataFromTheTableSafeguardTheVariable(String pathExpectedJson, String variableName, DataTable dataTable) {
        String jsonExample = OtherSteps.getPropertyOrStringVariableOrValue(pathExpectedJson);

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String key = requestParam.get(0);
                String value = OtherSteps.getPropertyOrStringVariableOrValue(requestParam.get(1));

                jsonExample = jsonExample.replaceAll(key, value);
            }
        }
        if (!isJSONValid(jsonExample)) {
            throw new JsonSyntaxException("Json " + variableName + "не прошел валидацию" +
                    "\nубедитесь в его корретности:\n" + jsonExample);
        }
        coreScenario.setVar(variableName, jsonExample);
    }

}
