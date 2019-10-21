package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.text.MatchesPattern;
import ru.bcs.at.library.core.core.helpers.FormattedDataContainer;
import ru.bcs.at.library.core.core.helpers.TextFormat;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;
import java.util.function.Function;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable;
import static ru.bcs.at.library.core.core.helpers.Utils.defineOrCheckDataFormat;

public class VerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Проверка форматированного текста</p>
     */
    @И("^значения в ([^\\s]+) проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(String checkingValuePath, String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(null, checkingValuePath, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * <p>Проверка форматированного текста</p>
     */
    @И("^значения в ((?:XML|JSON|PARAMS)) ([^\\s]+) проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(TextFormat checkingValueType, String checkingValuePath, String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(checkingValueType, checkingValuePath, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * <p>Проверка форматированного текста</p>
     */
    @И("^значения в нём проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(null, null, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * <p>Проверка форматированного текста</p>
     */
    private void checkFormattedData(TextFormat checkingValueType, String checkingValuePath, boolean caseInsensitive, DataTable dataTable) {
        if (checkingValuePath == null) {
            checkingValuePath = String.valueOf(coreScenario.getVar(CoreScenario.CURRENT));
        }
        String checkingValueString = cycleSubstitutionFromFileOrPropertyOrVariable(checkingValuePath);
        checkingValueType = defineOrCheckDataFormat(checkingValueString, checkingValueType);
        StringBuilder errorMessage = new StringBuilder();

        FormattedDataContainer formattedData = new FormattedDataContainer(checkingValueType, checkingValueString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String operation = row.get(1);
            String value = row.get(2);

            Function<String, Matcher> matcher = defineOperation(operation);
            String expectedValue = cycleSubstitutionFromFileOrPropertyOrVariable(value);
            String actualValue = formattedData.readValue(path);

            if (caseInsensitive) {
                expectedValue = expectedValue.toLowerCase();
                actualValue = actualValue.toLowerCase();
            }

            try {
                assertThat("\nНеверное содержимое элемента: " + path,
                        actualValue, matcher.apply(expectedValue));
            } catch (AssertionError e) {
                errorMessage.append(e.getMessage());
            }
        }
        if (!errorMessage.toString().isEmpty()) {
            fail(errorMessage.toString());
        }
    }

    private static Function<String, Matcher> defineOperation(String operationString) {
        Function<String, Matcher> matcher = null;
        switch (operationString) {
            case "==":
                matcher = Matchers::equalTo;
                break;
            case "!=":
                matcher = s -> not(equalTo(s));
                break;
            case "~":
                matcher = MatchesPattern::matchesPattern;
                break;
            case "!~":
                matcher = s -> not(matchesPattern(s));
                break;
            default:
                fail("Нечитаемый формат операции: " + operationString);
                break;
        }
        return matcher;
    }

    /**
     * <p>Сохранение значений форматированного текста</p>
     */
    @И("^значения из ([^\\s]+) сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(String processingValuePath, DataTable dataTable) {
        saveValuesFromFormattedData(null, processingValuePath, dataTable);
    }

    /**
     * <p>Сохранение значений форматированного текста</p>
     */
    @И("^значения из него сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(DataTable dataTable) {
        saveValuesFromFormattedData(null, null, dataTable);
    }

    /**
     * <p>Сохранение значений форматированного текста</p>
     * <p>В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.</p>
     *
     * @param processingValueType формат переданной строки
     * @param processingValuePath имя переменной которая содержит строку для обработки
     * @param dataTable   И в URL, и в значениях в таблице можно использовать переменные и из application.properties,
     *                    и из хранилища переменных из CoreScenario.
     *                    Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из ((?:XML|JSON|PARAMS)) ([^\\s]+) сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(TextFormat processingValueType, String processingValuePath, DataTable dataTable) {
        if (processingValuePath == null) {
            processingValuePath = String.valueOf(coreScenario.getVar(CoreScenario.CURRENT));
        }
        String processingValueString = cycleSubstitutionFromFileOrPropertyOrVariable(processingValuePath);
        processingValueType = defineOrCheckDataFormat(processingValueString, processingValueType);
        StringBuilder errorMessage = new StringBuilder();

        FormattedDataContainer formattedData = new FormattedDataContainer(processingValueType, processingValueString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String variableToSave = row.get(1);

            try {
                String actualValue = formattedData.readValue(path);
                coreScenario.setVar(variableToSave, actualValue);
            } catch (Exception e) {
                errorMessage.append("Не найдено значение: ").append(path).append("\n");
            }
        }
        if (!errorMessage.toString().isEmpty()) {
            fail(errorMessage.toString());
        }
    }
}
