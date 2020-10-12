package ru.at.library.api.steps;

import cucumber.api.java.ru.И;
import io.cucumber.datatable.DataTable;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.text.MatchesPattern;
import ru.at.library.core.core.helpers.FormattedDataContainer;
import ru.at.library.core.core.helpers.PropertyLoader;
import ru.at.library.core.core.helpers.TextFormat;
import ru.at.library.core.core.helpers.Utils;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;
import java.util.function.Function;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VerificationSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Определение типа операции по проверке:
     * '==' - равенство, '!=' - неравенство, '~' - соответствие регулярному выражению, '!~' - несоответствие регулярному выражению
     *
     * @param operationString строка с операцией проверки
     * @return матчер для проверки
     */
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
     * Проверка значений форматированного текста
     * В строке json/xml/params происходит проверка значений по jsonPath/xmlPath/paramName из первого столбца таблицы
     * по операциям из второго столбца таблицы ('==' - равенство, '!=' - неравенство, '~' - соответствие регулярному выражению,
     * '!~' - несоответствие регулярному выражению) и по значениям в третьем столбце таблицы.
     *
     * @param checkingValuePath        строка для обработки
     * @param caseInsensitiveIndicator независимость то регистра
     * @param dataTable                И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                                 и из хранилища переменных из CoreScenario.
     *                                 Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения в \"([^\"]+)\" проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(String checkingValuePath, String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(null, checkingValuePath, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * Проверка значений форматированного текста
     * В строке json/xml/params происходит проверка значений по jsonPath/xmlPath/paramName из первого столбца таблицы
     * по операциям из второго столбца таблицы ('==' - равенство, '!=' - неравенство, '~' - соответствие регулярному выражению,
     * '!~' - несоответствие регулярному выражению) и по значениям в третьем столбце таблицы.
     *
     * @param checkingValueType        формат переданной строки
     * @param checkingValuePath        строка для обработки
     * @param caseInsensitiveIndicator независимость то регистра
     * @param dataTable                И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                                 и из хранилища переменных из CoreScenario.
     *                                 Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения в ((?:XML|JSON|PARAMS)) \"([^\"]+)\" проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(TextFormat checkingValueType, String checkingValuePath, String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(checkingValueType, checkingValuePath, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * Проверка значений форматированного текста
     * В строке json/xml/params происходит проверка значений по jsonPath/xmlPath/paramName из первого столбца таблицы
     * по операциям из второго столбца таблицы ('==' - равенство, '!=' - неравенство, '~' - соответствие регулярному выражению,
     * '!~' - несоответствие регулярному выражению) и по значениям в третьем столбце таблицы.
     *
     * @param caseInsensitiveIndicator независимость то регистра
     * @param dataTable                И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                                 и из хранилища переменных из CoreScenario.
     *                                 Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения в нём проверены ((?:|без учета регистра ))по таблице:$")
    public void checkFormattedData(String caseInsensitiveIndicator, DataTable dataTable) {
        checkFormattedData(null, null, !caseInsensitiveIndicator.isEmpty(), dataTable);
    }

    /**
     * Проверка значений форматированного текста
     * В строке json/xml/params происходит проверка значений по jsonPath/xmlPath/paramName из первого столбца таблицы
     * по операциям из второго столбца таблицы ('==' - равенство, '!=' - неравенство, '~' - соответствие регулярному выражению,
     * '!~' - несоответствие регулярному выражению) и по значениям в третьем столбце таблицы.
     *
     * @param checkingValueType формат переданной строки
     * @param checkingValuePath строка для обработки
     * @param caseInsensitive   независимость то регистра
     * @param dataTable         И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                          и из хранилища переменных из CoreScenario.
     *                          Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    private void checkFormattedData(TextFormat checkingValueType, String checkingValuePath, boolean caseInsensitive, DataTable dataTable) {
        String checkingValueString = PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable(checkingValuePath);
        checkingValueType = Utils.defineOrCheckDataFormat(checkingValueString, checkingValueType);
        StringBuilder errorMessage = new StringBuilder();

        FormattedDataContainer formattedData = new FormattedDataContainer(checkingValueType, checkingValueString);
        for (List<String> row : dataTable.asLists()) {
            String path = row.get(0);
            String operation = row.get(1);
            String value = row.get(2);

            Function<String, Matcher> matcher = defineOperation(operation);
            String expectedValue = PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable(value);
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

    /**
     * Сохранение значений форматированного текста
     * В строке json/xml/params происходит поиск значений по jsonPath/xmlPath/paramName из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     *
     * @param processingValuePath строка для обработки
     * @param dataTable           И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                            и из хранилища переменных из CoreScenario.
     *                            Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из \"([^\"]+)\" сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(String processingValuePath, DataTable dataTable) {
        saveValuesFromFormattedData(null, processingValuePath, dataTable);
    }

    /**
     * Сохранение значений форматированного текста
     * В строке json/xml/params, сохраннённой в переменной по-умолчанию, происходит поиск значений по jsonPath/xmlPath/paramName из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     *
     * @param dataTable И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                  и из хранилища переменных из CoreScenario.
     *                  Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из него сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(DataTable dataTable) {
        saveValuesFromFormattedData(null, null, dataTable);
    }

    /**
     * Сохранение значений форматированного текста
     * В строке json/xml/params происходит поиск значений по jsonPath/xmlPath/paramName из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     *
     * @param processingValueType формат переданной строки
     * @param processingValuePath строка для обработки
     * @param dataTable           И в URL, и в значениях в таблице можно использовать переменные и из properties,
     *                            и из хранилища переменных из CoreScenario.
     *                            Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     */
    @И("^значения из ((?:XML|JSON|PARAMS)) \"([^\"]+)\" сохранены в переменные по таблице:$")
    public void saveValuesFromFormattedData(TextFormat processingValueType, String processingValuePath, DataTable dataTable) {
//        if (processingValuePath == null) {
//            processingValuePath = String.valueOf(coreScenario.getVar(CoreScenario.CURRENT));
//        }
        String processingValueString = PropertyLoader.cycleSubstitutionFromFileOrPropertyOrVariable(processingValuePath);
        processingValueType = Utils.defineOrCheckDataFormat(processingValueString, processingValueType);
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
