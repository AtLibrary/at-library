package ru.bcs.at.library.core.steps.json;

import cucumber.api.java.ru.И;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;

public class JsonArrayVerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Проверка что массив найденный по jsonPath в ответе responseName содержит value</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param key          jsonPath поиска массива
     * @param value        ожидаемое значение
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" массив содержит \"([^\"]*)\"$")
    public void checkArrayHasItem(String responseName, String key, String value) {
        value = loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);
        response
                .then()
                .body(key, Matchers.hasItem(value));
    }

    /**
     * <p>Все объекты в коллекции имеют поле с определенным названием, содержащим конкретное значение</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param jsonPath     jsonPath поиска массива
     * @param value        ожидаемое значение
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" весь массив соотвествует \"([^\"]*)\"$")
    public void checkArrayEqualAllItem(String responseName, String jsonPath, String value) {
        value = loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);

        List<String> list = response
                .getBody().jsonPath().getList(jsonPath);

        for (String actualValue : list) {
            if (!actualValue.equals(value)) {
                throw new AssertionError(
                        "Найденный по jsonPath" + jsonPath +
                                "\n список: " + list +
                                "\n содержит значения которые не equals: " + value);
            }
        }
    }

    /**
     * <p>Проверка что массив найденный по  jsonPath размером = value</p>
     *
     * @param responseName переменная в которой сохранен Response
     * @param jsonPath     jsonPath поиска массива
     * @param value        size
     */
    @И("^в ответе \"([^\"]*)\" по ключу: \"([^\"]*)\" размер массива \"([^\"]*)\"$")
    public void checkArraySize(String responseName, String jsonPath, String value) {
        value = loadValueFromFileOrPropertyOrVariableOrDefault(value);
        Response response = (Response) CoreScenario.getInstance().getVar(responseName);
        MatcherAssert.assertThat(
                response.jsonPath().getList(jsonPath).size(),
                Matchers.equalTo(Integer.valueOf(value)));
    }
}
