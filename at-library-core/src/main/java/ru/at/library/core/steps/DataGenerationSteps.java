package ru.at.library.core.steps;

import io.cucumber.java.ParameterType;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import net.andreinc.mockneat.MockNeat;
import org.apache.commons.lang3.RandomStringUtils;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;

/**
 * Шаги генерации тестовых данных
 */
@Log4j2
public class DataGenerationSteps {

    private static CoreScenario coreScenario = CoreScenario.getInstance();

    @ParameterType("кириллице|латинице")
    public static String lang(String lang) {
        return lang.equals("кириллице") ? "ru" : "en";
    }

    /**
     * Конкатенация строк
     */
    @И("конкатенация строк {string} и {string} и сохранено в переменную {string}")
    public void concatenationString(String text1, String text2, String varName) {
        String text = getPropertyOrStringVariableOrValue(text1) + getPropertyOrStringVariableOrValue(text2);
        coreScenario.setVar(varName, text);
        log.trace("Строка равна: " + text);
    }

    /**
     * Генерация последовательности латинских или кириллических букв задаваемой длины
     */
    @И("генерация {int} случайных символов на {lang} и сохранено в переменную {string}")
    public void setRandomCharSequence(int seqLength, String lang, String varName) {
        String charSeq = OtherSteps.getRandCharSequence(seqLength, lang);
        coreScenario.setVar(varName, charSeq);
        log.trace("Строка случайных символов равна: " + charSeq);
    }

    /**
     * Генерация последовательности цифр задаваемой длины и сохранение этого значения в переменную
     */
    @И("генерация случайного числа из {int} цифр/цифры и сохранение в переменную {string}")
    public void randomNumSequence(int seqLength, String varName) {
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        coreScenario.setVar(varName, numSeq);
        log.trace("Случайное число равно: " + numSeq);
    }

    /**
     * Создает случайную строку, которая находится между включающим минимумом и максимум </ p>
     */
    @И("генерация случайного числа в диапазоне от {int} до {int} и сохранение в переменную {string}")
    public void rRandomNumSequence(int min, int max, String varName) {
        max -= min;
        long number = (long) (Math.random() * ++max) + min;
        String numSeq = String.valueOf(number);
        coreScenario.setVar(varName, numSeq);
        log.trace("Случайное число равно: " + numSeq);
    }

    /**
     * Генерация случайного boolean и сохранение в переменную
     */
    @И("генерация случайного boolean и сохранение в переменную {string}")
    public void randomBoolean(String varName) {
        String randomString = String.valueOf(nextBoolean());
        coreScenario.setVar(varName, randomString);
        log.trace("Случайное boolean равно: " + randomString);
    }

    /**
     * Выбрано случайное знание из списка и сохранено в переменную
     */
    @И("сохранено в переменную {string} случайное значение из списка:")
    public void randomStingInList(String varName, List<String> list) {
        int random = new Random().nextInt(list.size());
        String randomString = list.get(random);
        coreScenario.setVar(varName, randomString);
        log.trace("Строка равна: " + randomString);
    }

    /**
     * Сгенерирован случайный email и сохранен в переменную
     */
    @И("генерация случайного email и сохранение в переменную {string}")
    public void randomEmail(String varName) {
        String randomEmail = MockNeat.secure().emails().val();
        coreScenario.setVar(varName, randomEmail);
        log.trace("Email равен: " + randomEmail);
    }
}
