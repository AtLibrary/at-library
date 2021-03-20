package ru.at.library.api.json;

import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.И;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.hamcrest.Matchers;
import ru.at.library.core.core.helpers.PropertyLoader;
import ru.at.library.api.helpers.Utils;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;


@Log4j2
public class JsonVerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Создание json на основе json-шаблона
     *
     * @param dataTable    таблица с ключ знанчение для замены текста в json
     *                     обчная замена текста
     * @param variableName переменная для сохраняения заполненного json
     */
    //TODO проверить работу c XML
    @И("заполняю ([^\"]*)-шаблон \"([^\"]*)\" данными из таблицы и сохраняю в переменную \"([^\"]*)\"")
    public void iFillInTheJsonTypeDataFromTheTableSafeguardTheVariable(String type, String pathExpectedJson, String variableName, DataTable dataTable) {
        String jsonPath = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        String fileExample = PropertyLoader.loadValueFromFileOrVariableOrDefault(jsonPath);

        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String key = requestParam.get(0);
                String value = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(requestParam.get(1));

                fileExample = fileExample.replaceAll(key, value);
            }
        }
        if (type.equals("JSON")) {
            if (!Utils.isJSONValid(fileExample)) {
                throw new JsonSyntaxException("Json " + variableName + "не прошел валидацию" +
                        "\nубедитесь в его корретности:\n" + fileExample);
            }
        }
        if (type.equals("XML")) {
            if (!Utils.isXMLValid(fileExample)) {
                throw new JsonSyntaxException("Xml " + variableName + "не прошел валидацию" +
                        "\nубедитесь в его корретности:\n" + fileExample);
            }
        }
        coreScenario.setVar(variableName, fileExample);
    }

    /**
     * Проверка Response
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @Deprecated
    @И("^в ((?:json|xml)) ответа \"([^\"]*)\" значения равны значениям из таблицы$")
    public void checkValuesBodyValueCaseSensitive(String typeContentBody, String valueToFind, DataTable dataTable) {
        this.checkValuesBody(typeContentBody, valueToFind, false, dataTable);
    }

    /**
     * Проверка Response
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @Deprecated
    @И("^в ((?:json|xml)) ответа \"([^\"]*)\" значения равны, без учета регистра, значениям из таблицы$")
    public void checkValuesBodyValueCaseInsensitive(String typeContentBody, String valueToFind, DataTable dataTable) {
        this.checkValuesBody(typeContentBody, valueToFind, true, dataTable);
    }

    private void checkValuesBody(String typeContentBody, String valueToFind, boolean caseInsensitive, DataTable dataTable) {
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

            if (caseInsensitive) {
                expectedValue = expectedValue.toLowerCase();
                actualValue = actualValue.toLowerCase();
            }

           assertEquals(
                    expectedValue, actualValue,
                   "Содержимое по " + typeContentBody + "path: " + path + " не равно" +
                           "\nожидаемое: " + expectedValue +
                           "\nреальное: " + actualValue +
                           "\n");
        }
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     *
     * @param typeContentBody тип контента
     * @param valueToFind     имя переменной которая содержит Response
     * @param dataTable       И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                        и из хранилища переменных из CoreScenario.
     *                        Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из ((?:json|xml)) ответа \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
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
            log.trace(typeContentBody.toUpperCase() + " path: " + path + ", значение: " + value + ", записано в переменную: " + varName);
        }
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     *
     * @param valueToFind имя переменной которая содержит Response
     * @param dataTable   И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                    и из хранилища переменных из CoreScenario.
     *                    Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @Deprecated
    @И("^значения из json \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
    public void getValuesFromJsonAsString(String valueToFind, DataTable dataTable) throws ParseException {
        String jsonString = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(valueToFind);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String variableName =
                    PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String value = String.valueOf(read);

            coreScenario.setVar(variableName, value);
        }
    }

    /**
     * Проверка json
     */
    @Deprecated
    @И("^в json \"([^\"]*)\" значения равны(|, без учета регистра,) значениям из таблицы$")
    public void checkJson(String pathExpectedJson, String textRegister, DataTable dataTable) throws ParseException {
        String jsonString = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);

            String expectedValue = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String actualValue = String.valueOf(read);

            if (!textRegister.isEmpty()) {
                expectedValue = expectedValue.toLowerCase();
                actualValue = actualValue.toLowerCase();
            }

            assertEquals(
                    "Содержимое по jsonpath: " + path + " не равно" +
                            "\nожидаемое: " + expectedValue +
                            "\nреальное: " + actualValue +
                            "\n",
                    expectedValue, actualValue);
        }
    }

    /**
     * Проверка json
     */
    @Deprecated
    @И("^в json \"([^\"]*)\" значения соответствуют шаблонам из таблицы$")
    public void checkJsonByRegex(String pathExpectedJson, DataTable dataTable) throws ParseException {
        String jsonString = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String regex = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(row.get(1));
            Object read = JsonPath.read(jsonObject, path);
            String actualValue = String.valueOf(read);

            assertTrue(
                    actualValue.matches(regex),
                    "Содержимое по jsonpath: " + path + " не соответствует" +
                            "\nожидаемое: " + regex +
                            "\nреальное: " + actualValue +
                            "\n");
        }
    }

    /**
     * значения найденные по jsonPath из json ответа "ПЕРВЫЙ" равны значениям из json ответа "ВТОРОЙ"
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

            assertNotNull(
                    "Содержимое по jsonpath: " + path +
                            "\nВ ответе: " + nameResponseOne +
                            "\nНе должно быть: null ",
                    actualValue);
            assertNotNull(
                    "Содержимое по jsonpath: " + path +
                            "\nВ ответе: " + nameResponseTwo +
                            "\nНе должно быть: null ",
                    expectedValue);

            assertEquals(
                    "Содержимое по jsonpath: " + path + " не равно" +
                            "\nВ ответе: " + expectedValue +
                            "\nВ переменной: " + actualValue +
                            "\n",
                    expectedValue, actualValue);
        }

    }

    /**
     * Сравнение body json ответа с ожидаемым
     *
     * @param variableName     переменная в которой сохранен Response
     * @param pathExpectedJson путь к json файлу
     */
    @И("^json в ответе \"([^\"]*)\" равен json: \"([^\"]*)\"")
    public void checkResponseJson(String variableName, String pathExpectedJson) {
        String json = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(pathExpectedJson);
        json = Utils.resolveJsonVars(json);
        Response response = (Response) CoreScenario.getInstance().getVar(variableName);
        response
                .then().body(
                Matchers.equalTo(json)
        );
    }

    /**
     * Проверка что тело ответа соответсвует json схеме
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
}
