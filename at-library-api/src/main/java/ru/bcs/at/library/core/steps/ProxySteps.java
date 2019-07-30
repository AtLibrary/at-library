package ru.bcs.at.library.core.steps; /**
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
import io.restassured.RestAssured;
import io.restassured.specification.ProxySpecification;
import lombok.extern.log4j.Log4j2;
import ru.bcs.at.library.core.core.helpers.PropertyLoader;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * API шаги
 * </h1>
 */
@Log4j2
public class ProxySteps {

    /**
     * <p style="color: green; font-size: 1.5em">
     * Используется прокси</p>
     *
     * @param proxyHost адрес proxy, например: s-nsk-proxy-01.global.bcs
     * @param proxyPort порт proxy, например: 8080
     */
    @Deprecated
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
    @Deprecated
    @И("^выключено использование proxy$")
    public void turnOffProxy() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");

        RestAssured.proxy = null;
    }

}