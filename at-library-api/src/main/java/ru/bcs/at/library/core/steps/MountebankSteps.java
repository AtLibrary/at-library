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
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.ImposterParser;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.imposters.Imposter;
import org.mbtest.javabank.http.responses.Is;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * <h1>Mountebank шаги</h1>
 */
@Log4j2
public class MountebankSteps {

    private static final int DEFAULT_MB_DEPLOY_PORT = 4545;

    private static final String MB_HOST = System.getProperty("mbHost", tryLoadProperty("mbHost"));
    private static final String MB_PORT = System.getProperty("mbPort", tryLoadProperty("mbPort"));
    private static Client client = new Client(MB_HOST, Integer.parseInt(MB_PORT));

    /**
     * <p>Создание mountebank-заглушки по-умолчанию</p>
     */
    @И("^разворачивается mb заглушка по-умолчанию$")
    public void deployImposter() {
        deployImposter(DEFAULT_MB_DEPLOY_PORT);
    }

    /**
     * <p>Создание mountebank-заглушки по-умолчанию на указанном порте</p>
     *
     * @param deployPort   порт разворачивания заглушки по-умолчанию
     */
    @И("^разворачивается mb заглушка по-умолчанию на порте (\\d+)$")
    public void deployImposter(int deployPort) {
        checkMB(client);
        client.deleteImposter(deployPort);
        client.createImposter(new Imposter().onPort(deployPort));
    }

    /**
     * <p>Создание mountebank-заглушки по mountebank-json</p>
     *
     * @param imposterJson   json с описанием необходимых заглушек
     */
    @И("^разворачивается mb заглушка с параметрами из файла '([^\']+)'$")
    public void deployImposter(String imposterJson) throws ParseException {
        Imposter imposter = ImposterParser.parse(loadValueFromFileOrPropertyOrVariableOrDefault(imposterJson));

        checkMB(client);
        client.deleteImposter(imposter.getPort());
        client.createImposter(imposter);
    }

    /**
     * <p>Создание mountebank-заглушки по mountebank-json на указанном порте</p>
     *
     * @param imposterJson  json с описанием необходимых заглушек
     * @param deployPort    порт разворачивания заглушки по-умолчанию
     */
    @И("^разворачивается mb заглушка с параметрами из файла '([^\']+)' на порте (\\d+)$")
    public void deployImposter(String imposterJson, int deployPort) throws ParseException {
        Imposter imposter = ImposterParser.parse(loadValueFromFileOrPropertyOrVariableOrDefault(imposterJson));

        checkMB(client);
        client.deleteImposter(deployPort);
        client.createImposter(imposter.onPort(deployPort));
    }

    /**
     * <p>Создание mountebank-заглушки с указанным ответом</p>
     *
     * @param response   ответ на любой запрос новой заглушки заглушки
     */
    @И("^разворачивается mb заглушка с ответом из файла '([^\']+)'$")
    public void deployImposterWithResponse(String response) {
        deployImposterWithResponse(response, DEFAULT_MB_DEPLOY_PORT);
    }

    /**
     * <p>Создание mountebank-заглушки с указанным ответом на указанном порте</p>
     *
     * @param response    ответ на любой запрос новой заглушки заглушки
     * @param deployPort  порт разворачивания заглушки с указанным ответом
     */
    @И("^разворачивается mb заглушка с ответом из файла '([^\']+)' на порте (\\d+)$")
    public void deployImposterWithResponse(String response, int deployPort) {
        Stub stub = new Stub().withResponse(
                new Is().withBody(
                        loadValueFromFileOrPropertyOrVariableOrDefault(response)
                )
        );

        checkMB(client);
        client.deleteImposter(deployPort);
        client.createImposter(new Imposter().onPort(deployPort).addStub(stub));
    }

    /**
     * <p>Удаление mountebank-заглушки на порте по-умолчанию</p>
     */
    @И("^удаляется mb заглушка$")
    public void deleteImposter() {
        deleteImposter(DEFAULT_MB_DEPLOY_PORT);
    }

    /**
     * <p>Удаление mountebank-заглушки на указанном порте</p>
     */
    @И("^удаляется mb заглушка на порте (\\d+)$")
    public void deleteImposter(int destroyPort) {
        checkMB(client);
        client.deleteImposter(destroyPort);
    }

    /**
     * <p>Удаление всех mountebank-заглушек</p>
     */
    @И("^удаляются все mb заглушки$")
    public void deleteAllImposters() {
        checkMB(client);
        client.deleteAllImposters();
    }

    private void checkMB(Client client) {
        Assert.assertTrue("Mountebank is not running!", client.isMountebankRunning());
    }
}
