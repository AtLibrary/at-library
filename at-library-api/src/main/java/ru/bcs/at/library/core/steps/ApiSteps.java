package ru.bcs.at.library.core.steps; /**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import ru.bcs.at.library.core.core.helpers.PropertyLoader;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * <h1>API шаги</h1>
 */
@Log4j2
public class ApiSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Сравнение кода http ответа с ожидаемым</p>
     *
     * @param responseNameVariable переменная в которой сохранен Response
     * @param expectedStatusCode   ожидаемый http статус код
     */
    @И("^в ответе \"([^\"]*)\" statusCode: (\\d+)$")
    public void checkResponseStatusCode(String responseNameVariable, int expectedStatusCode) {
        Response response = (Response) CoreScenario.getInstance().getVar(responseNameVariable);
        response.then().statusCode(expectedStatusCode);
    }

    /**
     * <p>Ответ, сохраннённый в переменной сохраняется в переменной.
     *
     * @param responseNameVariable имя переменной, которая содержит Response
     * @param variableName         имя переменной хранилища переменных из CoreScenario, в которую необходимо сохранить значение.
     */
    @И("^значение из body ответа \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void getValuesFromBodyAsString(String responseNameVariable, String variableName) {
        Response response = (Response) CoreScenario.getInstance().getVar(responseNameVariable);

        coreScenario.setVar(variableName, response.getBody().asString());
        coreScenario.write("Значение: " + response.getBody().asString() + ", записано в переменную: " + variableName);
    }

    /**
     * <p>Получение Cookies из ответа</p>
     *
     * @param responseNameVariable имя переменной которая содержит Response
     * @param dataTable            И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                             и из хранилища переменных из CoreScenario.
     *                             Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из cookies ответа \"([^\"]*)\", сохранены в переменные из таблицы$")
    public void getValuesFromCookiesAsString(String responseNameVariable, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(responseNameVariable);

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
     * <p>Сравнение в http ответе реальных header с ожидаемыми</p>
     *
     * @param responseNameVariable переменная в которой сохранен Response
     * @param dataTable            массив с параметрами
     */
    @И("^в ответе \"([^\"]*)\" содержатся header со значениями из таблицы$")
    public void checkResponseHeaderValues(String responseNameVariable, DataTable dataTable) {
        Response response = (Response) CoreScenario.getInstance().getVar(responseNameVariable);

        for (List<String> row : dataTable.asLists()) {
            String expectedHeaderName = row.get(0);
            String expectedHeaderValue = row.get(1);

            if (expectedHeaderName.isEmpty() || expectedHeaderValue.isEmpty()) {
                throw new RuntimeException("Header и значение не могут быть пустыми");
            }

            response.then()
                    .assertThat().header(expectedHeaderName, expectedHeaderValue);
        }
    }

    @И("^переменная \"([^\"]*)\" содержир base64 кодирование, декодирована в pdf и сохранена по пути \"([^\"]*)\" с именем \"([^\"]*)\" в формате \"([^\"]*)\"$")
    public void saveBase64ToPdf(String encodeBytes, String path, String fName, String fFormat) throws IOException {
        String base64Code = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(encodeBytes);
        String fileName = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(fName);
        String fileFormat = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(fFormat);
        String pathToSave = PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault(path) + "\\" + fileName + "." + fileFormat;

        byte[] decodedBytes = Base64.getDecoder().decode(base64Code);

        File file = new File(pathToSave);
        FileOutputStream fop = new FileOutputStream(file);

        fop.write(decodedBytes);
        fop.flush();
        fop.close();
    }
}
