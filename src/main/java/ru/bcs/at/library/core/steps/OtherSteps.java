package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import org.hamcrest.Matchers;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.getPropertyOrValue;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadProperty;

/**
 * <h1 style="color: green; font-size: 2.2em">
 *     Шаги утилиты
 *     </>
 */
public class OtherSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p style="color: green; font-size: 1.5em">
     * Устанавливается значение переменной в хранилище переменных.
     * Один из кейсов: установка login пользователя
     *
     * @param variableName имя переменной
     * @param value        значение переменной
     *                     </p>
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        value = getPropertyOrValue(value);
        coreScenario.setVar(variableName, value);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка равенства двух переменных из хранилища
     *
     * @param firstVariableName  первая переменная
     * @param secondVariableName вторая переменная
     *                           </p>
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = coreScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = coreScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, equalTo(secondValueToCompare));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка неравенства двух переменных из хранилища
     *
     * @param firstVariableName  первая переменная
     * @param secondVariableName вторая переменная
     *                           </p>
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" не совпадают$")
    public void checkingTwoVariablesAreNotEquals(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = coreScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = coreScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, Matchers.not(equalTo(secondValueToCompare)));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка выражения на истинность
     *
     * @param expression выражение из property, из переменной сценария или значение аргумента
     *                   Например, string1.equals(string2)
     *                   OR string.equals("string")
     *                   Любое Java-выражение, возвращающие boolean
     *                   </p>
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        coreScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сохранено значение из property файла в переменную
     *
     * @param propertyVariableName ключ в файле application.properties
     * @param variableName         имя переменной
     *                             Значение заданной переменной из application.properties сохраняется в переменную в coreScenario
     *                             для дальнейшего использования</p>
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        propertyVariableName = loadProperty(propertyVariableName);
        coreScenario.setVar(variableName, propertyVariableName);
        coreScenario.write("Значение сохраненной переменной " + propertyVariableName);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ожидание в течение заданного количества секунд
     *
     * @param seconds секунд
     *                </p>
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Написание автотеста в работе</p>
     */
    @И("^написание автотеста в работе$")
    public void pendingException() {
        throw new cucumber.api.PendingException("написание автотеста в работе");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Автотест реализован на старом фреймворке</p>
     */
    @И("^автотест реализован на старом фреймворке$")
    public void oldFramework() {
        throw new cucumber.api.PendingException("автотест реализован на старом фреймворке");
    }
}
