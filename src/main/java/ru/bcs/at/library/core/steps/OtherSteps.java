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

public class OtherSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Устанавливается значение переменной в хранилище переменных. Один из кейсов: установка login пользователя
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        value = getPropertyOrValue(value);
        coreScenario.setVar(variableName, value);
    }

    /**
     * Проверка равенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = coreScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = coreScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, equalTo(secondValueToCompare));
    }

    /**
     * Проверка неравенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" не совпадают$")
    public void checkingTwoVariablesAreNotEquals(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = coreScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = coreScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, Matchers.not(equalTo(secondValueToCompare)));
    }

    /**
     * Проверка выражения на истинность
     * выражение из property, из переменной сценария или значение аргумента
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        coreScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Ожидание в течение заданного количества секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }
}
