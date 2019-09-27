package ru.bcs.at.library.core.steps.json;

import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import ru.bcs.at.library.core.cucumber.ScopedVariables;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.isJSONValid;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.isXMLValid;

public class JsonVerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Создание json на основе json-шаблона</p>
     *
     * @param dataTable    таблица с ключ знанчение для замены текста в json
     *                     обчная замена текста
     * @param variableName переменная для сохраняения заполненного json
     */
    //TODO проверить работу c XML
    @И("заполняю ([^\"]*)-шаблон \"([^\"]*)\" данными из таблицы и сохраняю в переменную \"([^\"]*)\"")
    public void iFillInTheJsonTypeDataFromTheTableSafeguardTheVariable(String type, String pathExpectedJson, String variableName, DataTable dataTable) {
        String jsonPath = loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        String fileExample = loadValueFromFileOrPropertyOrVariableOrDefault(jsonPath);

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String key = requestParam.get(0);
                String value = loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(1));

                fileExample = fileExample.replaceAll(key, value);
            }
        }
        if (type.equals("JSON")) {
            if (!isJSONValid(fileExample)) {
                throw new JsonSyntaxException("Json " + variableName + "не прошел валидацию" +
                        "\nубедитесь в его корретности:\n" + fileExample);
            }
        }
        if (type.equals("XML")) {
            if (!isXMLValid(fileExample)) {
                throw new JsonSyntaxException("Xml " + variableName + "не прошел валидацию" +
                        "\nубедитесь в его корретности:\n" + fileExample);
            }
        }
        coreScenario.setVar(variableName, fileExample);
    }

    /**
     * <p>Проверка Response</p>
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^в (json|xml) ответа \"([^\"]*)\" значения равны значениям из таблицы$")
    public void checkValuesBodyValueCaseSensitive(String typeContentBody, String valueToFind, DataTable dataTable) {
        this.checkValuesBody(typeContentBody, valueToFind, false, dataTable);
    }

    /**
     * <p>Проверка Response</p>
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^в (json|xml) ответа \"([^\"]*)\" значения равны, без учета регистра, значениям из таблицы$")
    public void checkValuesBodyValueCaseInsensitive(String typeContentBody, String valueToFind, DataTable dataTable) {
        this.checkValuesBody(typeContentBody, valueToFind, true, dataTable);
    }

    private void checkValuesBody(String typeContentBody, String valueToFind, boolean caseInsensitive, DataTable dataTable) {
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

            if (caseInsensitive) {
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
     * <p>В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
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
     * <p>В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.</p>
     *
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из json \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
    public void getValuesFromJsonAsString(String valueToFind, DataTable dataTable) throws ParseException {
        String jsonString = loadValueFromFileOrPropertyOrVariableOrDefault(valueToFind);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String variableName =
                    loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String value = String.valueOf(read);

            coreScenario.setVar(variableName, value);
        }
    }

    /**
     * <p>Проверка json</p>
     */
    @И("^в json \"([^\"]*)\" значения равны(|, без учета регистра,) значениям из таблицы$")
    public void checkJson(String pathExpectedJson, String textRegister, DataTable dataTable) throws ParseException {
        String jsonString = loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String expectedValue = loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
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
     * <p>Проверка json</p>
     */
    @И("^в json \"([^\"]*)\" значения соответствуют шаблонам из таблицы$")
    public void checkJsonByRegex(String pathExpectedJson, DataTable dataTable) throws ParseException {
        String jsonString = loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String regex = loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String actualValue = String.valueOf(read);

            Assert.assertTrue(
                    "Содержимое по  jsonpath:" + path + " не соответствует" +
                            "\nожидаемое: " + regex +
                            "\nреальное: " + actualValue +
                            "\n",
                    actualValue.matches(regex));
        }
    }

    /**
     * <p>значения найденные по jsonPath из json ответа "ПЕРВЫЙ" равны значениям из json ответа "ВТОРОЙ"</p>
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
     * <p>Сравнение body json ответа с ожидаемым</p>
     *
     * @param variableName     переменная в которой сохранен Response
     * @param pathExpectedJson путь к json файлу
     */
    @И("^json в ответе \"([^\"]*)\" равен json: \"([^\"]*)\"")
    public void checkResponseJson(String variableName, String pathExpectedJson) {
        String json = loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        json = ScopedVariables.resolveJsonVars(json);
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);
        response
                .then().body(
                Matchers.equalTo(json)
        );
    }

    /**
     * <p>Проверка что тело ответа соответсвует json схеме</p>
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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(expectedJsonSchema));
    }
}
