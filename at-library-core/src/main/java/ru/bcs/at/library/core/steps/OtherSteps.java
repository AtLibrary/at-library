package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import org.hamcrest.Matchers;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.*;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * Шаги утилиты
 * </>
 */
public class OtherSteps {

    private static CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @return Возвращает каталог "Downloads" в домашней директории
     * </p>
     */
    public static File getDownloadsDir() {
        String homeDir = System.getProperty("user.home");
        return new File(homeDir + "/Downloads");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @param filesToDelete массив файлов
     *                      Удаляет файлы, переданные в метод
     *                      </p>
     */
    public static void deleteFiles(File[] filesToDelete) {
        for (File file : filesToDelete) {
            file.delete();
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @param maxValueInRange максимальная граница диапазона генерации случайных чисел
     *                        Возвращает случайное число от нуля до maxValueInRange
     *                        </p>
     */
    public static int getRandom(int maxValueInRange) {
        return (int) (Math.random() * maxValueInRange);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Возвращает последовательность случайных символов переданных алфавита и длины
     * Принимает на вход варианты языков 'ru' и 'en'
     * Для других входных параметров возвращает латинские символы (en)
     *
     * @param length
     * @param lang   </p>
     */
    public static String getRandCharSequence(int length, String lang) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char symbol = charGenerator(lang);
            builder.append(symbol);
        }
        return builder.toString();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Возвращает случайный символ переданного алфавита
     *
     * @param lang </p>
     */
    public static char charGenerator(String lang) {
        Random random = new Random();
        if (lang.equals("ru")) {
            return (char) (1072 + random.nextInt(32));
        } else {
            return (char) (97 + random.nextInt(26));
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка на соответствие строки паттерну
     *
     * @param pattern
     * @param str     </p>
     */
    public static boolean isTextMatches(String str, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Возвращает локатор для поиска по нормализованному(без учета регистра) тексту
     *
     * @param expectedText </p>
     */
    public static String getTranslateNormalizeSpaceText(String expectedText) {
        StringBuilder text = new StringBuilder();
        text.append("//*[contains(translate(normalize-space(text()), ");
        text.append("'ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', ");
        text.append("'abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхчшщъыьэюя'), '");
        text.append(expectedText.toLowerCase());
        text.append("')]");
        return text.toString();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @return Возвращает значение из property файла, если отсутствует, то из пользовательских переменных,
     * если и оно отсутствует, то возвращает значение переданной на вход переменной
     * </p>
     */
    public static String getPropertyOrStringVariableOrValue(String propertyNameOrVariableNameOrValue) {
        String propertyValue = tryLoadProperty(propertyNameOrVariableNameOrValue);
        String variableValue = (String) CoreScenario.getInstance().tryGetVar(propertyNameOrVariableNameOrValue);

        boolean propertyCheck = checkResult(propertyValue, "Переменная " + propertyNameOrVariableNameOrValue + " из property файла");
        boolean variableCheck = checkResult(variableValue, "Переменная сценария " + propertyNameOrVariableNameOrValue);

        return propertyCheck ? propertyValue : (variableCheck ? variableValue : propertyNameOrVariableNameOrValue);
    }

    public static boolean checkResult(String result, String message) {
        if (isNull(result)) {
            coreScenario.write(message + " не найдена");
            return false;
        }
        coreScenario.write(message + " = " + result);
        CoreScenario.getInstance().write(message + " = " + result);
        return true;
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Устанавливается значение текущей даты в хранилище переменных.
     *
     * @param variableName имя переменной
     * @param dateFormat   формат даты
     *                     </p>
     */
    @И("^установлено значение переменной \"([^\"]*)\" с текущей датой в формате \"([^\"]*)\"$")
    public void setCurrentDate(String variableName, String dateFormat) {
        long date = System.currentTimeMillis();
        setDate(date, variableName, dateFormat);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Устанавливается значение текущей даты в хранилище переменных.
     *
     * @param variableName имя переменной
     * @param dateFormat   формат даты
     *                     </p>
     */
    @И("^установлено значение переменной \"([^\"]*)\" с текущей датой минус (\\d+) (?:час|часов) в формате \"([^\"]*)\"$")
    public void setMinusDate(String variableName, int hour, String dateFormat) {
        long time = new Date(System.currentTimeMillis() - hour * 1000 * 3600).getTime();
        setDate(time, variableName, dateFormat);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Устанавливается значение текущей даты + часы в хранилище переменных.
     *
     * @param variableName имя переменной
     * @param dateFormat   формат даты
     *                     </p>
     */
    @И("^установлено значение переменной \"([^\"]*)\" с текущей датой плюс (\\d+) (?:час|часов) в формате \"([^\"]*)\"$")
    public void setPlusDate(String variableName, int hour, String dateFormat) {
        long time = new Date(System.currentTimeMillis() + hour * 1000 * 3600).getTime();
        setDate(time, variableName, dateFormat);
    }

    private void setDate(long date, String variableName, String dateFormat) {
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            coreScenario.write("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }

        coreScenario.setVar(variableName, currentStringDate);
    }

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
     * Проверка совпадения значения из переменной и значения из property
     * </p>
     */
    @Тогда("^значения из переменной \"([^\"]*)\" и из property файла \"([^\"]*)\" совпадают$")
    public void checkIfValueFromVariableEqualPropertyVariable(String envVarible, String propertyVariable) {
        assertThat("Переменные " + envVarible + " и " + propertyVariable + " не совпадают",
                (String) coreScenario.getVar(envVarible), equalToIgnoringCase(loadProperty(propertyVariable)));
    }

    /**
     * Выполняется чтение файла с шаблоном и заполнение его значениями из таблицы
     * </p>
     */
    @И("^шаблон \"([^\"]*)\" заполнен данными из таблицы и сохранён в переменную \"([^\"]*)\"$")
    public void fillTemplate(String templateName, String varName, DataTable table) {
        String template = loadValueFromFileOrPropertyOrVariableOrDefault(templateName);
        boolean error = false;
        for (List<String> list : table.asLists()) {
            String regexp = list.get(0);
            String replacement = list.get(1);
            if (template.contains(regexp)) {
                template = template.replaceAll(regexp, replacement);
            } else {
                coreScenario.write("В шаблоне не найден элемент " + regexp);
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("В шаблоне не найдены требуемые регулярные выражения");
        coreScenario.setVar(varName, template);
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
    @И("^ручной тест$")
    public void manuelTest() {
        throw new cucumber.api.PendingException("написание автотеста в работе");
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
     * Написание автотеста в работе</p>
     */
    @И("^написание автотеста в работе. Планируемая дата: \"([^\"]*)\"$")
    public void pendingException(String date) {
        throw new cucumber.api.PendingException("написание автотеста в работе. Планируемая дата: " + date);
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
