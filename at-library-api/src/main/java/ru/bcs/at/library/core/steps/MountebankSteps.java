/*
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
package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.ImposterParser;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.imposters.Imposter;
import org.mbtest.javabank.http.responses.Is;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static java.lang.String.format;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * <h1>Mountebank шаги</h1>
 */
@Log4j2
public class MountebankSteps {

    private static final int DEFAULT_MB_PORT = 4545;
    private static final String MB_HOST = System.getProperty("mbHost", tryLoadProperty("mbHost"));
    private static final String MB_PORT = System.getProperty("mbPort", tryLoadProperty("mbPort"));
    private static Client client = new Client(MB_HOST, Integer.parseInt(MB_PORT));
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Создание mountebank-заглушки по-умолчанию на стандартном порте</p>
     */
    @И("^разворачивается mb заглушка по-умолчанию$")
    public void deployImposter() {
        deployImposter(null);
    }

    /**
     * <p>Создание mountebank-заглушки по-умолчанию на указанном порте</p>
     *
     * @param deployPort порт разворачивания заглушки по-умолчанию
     */
    @И("^разворачивается mb заглушка по-умолчанию на порте \"(\\d+)\"$")
    public void deployImposter(Integer deployPort) {
        checkMB();
        if (deployPort == null) {
            deployPort = DEFAULT_MB_PORT;
        }
        client.deleteImposter(deployPort);
        client.createImposter(new Imposter().onPort(deployPort));
    }

    /**
     * <p>Создание mountebank-заглушки по mountebank-json</p>
     *
     * @param imposterJson json с описанием необходимых заглушек
     */
    @И("^разворачивается mb заглушка с параметрами из файла \"([^\"]+)\"$")
    public void deployImposter(String imposterJson) throws ParseException {
        Imposter imposter = ImposterParser.parse(loadValueFromFileOrPropertyOrVariableOrDefault(imposterJson));

        checkMB();
        client.deleteImposter(imposter.getPort());
        client.createImposter(imposter);
    }

    /**
     * <p>Создание mountebank-заглушки по mountebank-json на указанном порте</p>
     *
     * @param imposterJson json с описанием необходимых заглушек
     * @param deployPort   порт разворачивания заглушки по-умолчанию
     */
    @И("^разворачивается mb заглушка с параметрами из файла \"([^\"]+)\" на порте \"(\\d+)\"$")
    public void deployImposter(String imposterJson, int deployPort) throws ParseException {
        Imposter imposter = ImposterParser.parse(loadValueFromFileOrPropertyOrVariableOrDefault(imposterJson));

        checkMB();
        client.deleteImposter(deployPort);
        client.createImposter(imposter.onPort(deployPort));
    }

    /**
     * <p>Создание mountebank-заглушки с указанным ответом на стандартном порте</p>
     *
     * @param response   ответ на любой запрос новой заглушки заглушки
     */
    @И("^разворачивается mb заглушка с ответом из файла \"([^\"]+)\"$")
    public void deployImposterWithResponse(String response) {
        deployImposterWithResponse(response, null);
    }

    /**
     * <p>Создание mountebank-заглушки с указанным ответом на указанном порте</p>
     *
     * @param response   ответ на любой запрос новой заглушки заглушки
     * @param deployPort порт разворачивания заглушки с указанным ответом
     */
    @И("^разворачивается mb заглушка с ответом из файла \"([^\"]+)\" на порте \"(\\d+)\"$")
    public void deployImposterWithResponse(String response, Integer deployPort) {
        Stub stub = new Stub().withResponse(
                new Is().withBody(
                        loadValueFromFileOrPropertyOrVariableOrDefault(response)
                )
        );

        checkMB();
        if (deployPort == null) {
            deployPort = DEFAULT_MB_PORT;
        }
        client.deleteImposter(deployPort);
        client.createImposter(new Imposter().onPort(deployPort).addStub(stub));
    }

    /**
     * <p>Удаление mountebank-заглушки на стандартном порте</p>
     */
    @И("^удаляется mb заглушка$")
    public void deleteImposter() {
        deleteImposter(DEFAULT_MB_PORT);
    }

    /**
     * <p>Удаление mountebank-заглушки на указанном порте</p>
     */
    @И("^удаляется mb заглушка на порте \"(\\d+)\"$")
    public void deleteImposter(Integer destroyPort) {
        checkMB();
        if (destroyPort == null) {
            destroyPort = DEFAULT_MB_PORT;
        }
        client.deleteImposter(destroyPort);
    }

    /**
     * <p>Удаление всех mountebank-заглушек</p>
     */
    @И("^удаляются все mb заглушки$")
    public void deleteAllImposters() {
        checkMB();
        client.deleteAllImposters();
    }

    /**
     * <p>Получение запросов mountebank-заглушки на порте</p>
     */
    @И("^получены запросы mb заглушки$")
    public void getRequestsOnPort() throws ParseException {
        getRequestsOnPort(null, null);
    }

    /**
     * <p>Получение запросов mountebank-заглушки на порте</p>
     */
    @И("^получены запросы mb заглушки на порте \"(\\d+)\"$")
    public void getRequestsOnPort(Integer gettingPort) throws ParseException {
        getRequestsOnPort(gettingPort, null);
    }

    /**
     * <p>Получение запросов mountebank-заглушки на порте</p>
     */
    @И("^получены запросы mb заглушки и сохранены в переменную \"([^\"]+)\"$")
    public void getRequestsOnPort(String requestsNameVariable) throws ParseException {
        getRequestsOnPort(null, requestsNameVariable);
    }

    /**
     * <p>Получение запросов mountebank-заглушки на порте</p>
     */
    @И("^получены запросы mb заглушки на порте \"(\\d+)\" и сохранены в переменную \"([^\"]+)\"$")
    public void getRequestsOnPort(Integer gettingPort, String requestsNameVariable) throws ParseException {
        checkMB();
        if (gettingPort == null) {
            gettingPort = DEFAULT_MB_PORT;
        }
        if (requestsNameVariable == null) {
            requestsNameVariable = CoreScenario.CURRENT;
        }
        coreScenario.setVar(requestsNameVariable, client.getImposter(gettingPort).getRequests().toString());
    }

    /**
     * <p>Получение запроса mountebank-заглушки на порте</p>
     */
    @И("^получен \"(\\d+)\" запрос mb заглушки$")
    public void getRequestOnPort(int reqNum) throws ParseException {
        getRequestOnPort(reqNum, null, null);
    }

    /**
     * <p>Получение запроса mountebank-заглушки на порте</p>
     */
    @И("^получен \"(\\d+)\" запрос mb заглушки на порте \"(\\d+)\"$")
    public void getRequestOnPort(int reqNum, Integer gettingPort) throws ParseException {
        getRequestOnPort(reqNum, gettingPort, null);
    }

    /**
     * <p>Получение запроса mountebank-заглушки на порте</p>
     */
    @И("^получен \"(\\d+)\" запрос mb заглушки и сохранен в переменную \"([^\"]+)\"$")
    public void getRequestOnPort(int reqNum, String requestNameVariable) throws ParseException {
        getRequestOnPort(reqNum, null, requestNameVariable);
    }

    /**
     * <p>Получение запроса mountebank-заглушки на порте</p>
     */
    @И("^получен \"(\\d+)\" запрос mb заглушки на порте \"(\\d+)\" и сохранен в переменную \"([^\"]+)\"$")
    public void getRequestOnPort(int reqNum, Integer gettingPort, String requestNameVariable) throws ParseException {
        checkMB();
        if (gettingPort == null) {
            gettingPort = DEFAULT_MB_PORT;
        }
        if (requestNameVariable == null) {
            requestNameVariable = CoreScenario.CURRENT;
        }
        coreScenario.setVar(requestNameVariable, client.getImposter(gettingPort).getRequest(reqNum));
    }

    /**
     * <p>Получение последнего запроса mountebank-заглушки на порте</p>(?: и сохранен в переменную "([^"]+)")
     */
    @И("^получен последний запрос mb заглушки$")
    public void getLastRequestOnPort() throws ParseException {
        getLastRequestOnPort(null, null);
    }

    /**
     * <p>Получение последнего запроса mountebank-заглушки на порте</p>(?: и сохранен в переменную "([^"]+)")
     */
    @И("^получен последний запрос mb заглушки на порте \"(\\d+)\"$")
    public void getLastRequestOnPort(Integer gettingPort) throws ParseException {
        getLastRequestOnPort(gettingPort, null);
    }

    /**
     * <p>Получение последнего запроса mountebank-заглушки на порте</p>(?: и сохранен в переменную "([^"]+)")
     */
    @И("^получен последний запрос mb заглушки и сохранен в переменную \"([^\"]+)\"$")
    public void getLastRequestOnPort(String requestNameVariable) throws ParseException {
        getLastRequestOnPort(null, requestNameVariable);
    }

    /**
     * <p>Получение последнего запроса mountebank-заглушки на порте</p>(?: и сохранен в переменную "([^"]+)")
     */
    @И("^получен последний запрос mb заглушки на порте \"(\\d+)\" и сохранен в переменную \"([^\"]+)\"$")
    public void getLastRequestOnPort(Integer gettingPort, String requestNameVariable) throws ParseException {
        checkMB();
        if (gettingPort == null) {
            gettingPort = DEFAULT_MB_PORT;
        }
        if (requestNameVariable == null) {
            requestNameVariable = CoreScenario.CURRENT;
        }
        coreScenario.setVar(requestNameVariable, client.getImposter(gettingPort).getLastRequest());
    }

    private void checkMB() {
        if (client == null) {
            client = new Client(MB_HOST, Integer.parseInt(MB_PORT));
        }
        Assert.assertTrue(format("Mountebank on '%s' is not running!", client.getBaseUrl()), client.isMountebankRunning());
    }
}
