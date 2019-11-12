package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import net.andreinc.mockneat.MockNeat;
import org.apache.commons.lang3.RandomStringUtils;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static ru.bcs.at.library.core.steps.OtherSteps.getRandCharSequence;
/**
 * <h1>Шаги генерации тестовых данных</h1>
 */
public class DataGenerationSteps {

    private static CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Конкатенация строк</p>
     */
    @И("конкатенация строк \"([^\"]*)\" и \"([^\"]*)\" и сохранено в переменную \"([^\"]*)\"$")
    public void concatenationString(String text1, String text2, String varName) {
        String text = text1 + text2;
        coreScenario.setVar(varName, text);
        coreScenario.write("Строка равна :" + text);
    }

    /**
     * <p>Генерация последовательности латинских или кириллических букв задаваемой длины</p>
     */
    @И("^генерация (\\d+) случайных символов на ((?:кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequence(int seqLength, String lang, String varName) {
        if (lang.equals("кириллице")) {
            lang = "ru";
        } else {
            lang = "en";
        }
        String charSeq = getRandCharSequence(seqLength, lang);
        coreScenario.setVar(varName, charSeq);
        coreScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * <p>Генерация последовательности цифр задаваемой длины и сохранение этого значения в переменную</p>
     */
    @И("^генерация случайного числа из (\\d+) (?:цифр|цифры) и сохранение в переменную \"([^\"]*)\"$")
    public void randomNumSequence(int seqLength, String varName) {
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        coreScenario.setVar(varName, numSeq);
        coreScenario.write("Случайное число равно :" + numSeq);
    }

    /**
     * <p>Создает случайную строку, длина которой находится между включающим минимумом и максимум </ p>
     */
    @И("^генерация случайного числа в диапазоне от (\\d+) до (\\d+) и сохранение в переменную \"([^\"]*)\"$")
    public void rRandomNumSequence(int min, int max, String varName) {
        String numSeq = RandomStringUtils.randomNumeric(min, max);
        coreScenario.setVar(varName, numSeq);
        coreScenario.write("Случайное число равно :" + numSeq);
    }

    /**
     * <p>Генерация случайного boolean и сохранение в переменную</p>
     */
    @И("^генерация случайного boolean и сохранение в переменную \"([^\"]*)\"$")
    public void randomBoolean(String varName) {
        String randomString = String.valueOf(nextBoolean());
        coreScenario.setVar(varName, randomString);
        coreScenario.write("Случайное boolean равно :" + randomString);
    }

    /**
     * <p>Выбрано случайное знание из списка и сохранено в переменную</p>
     */
    @И("^сохранено в переменную \"([^\"]*)\" случайное значение из списка:$")
    public void randomStingInList(String varName, List<String> list) {
        int random = new Random().nextInt(list.size());
        String randomString = list.get(random);
        coreScenario.setVar(varName, randomString);
        coreScenario.write("Строка равна :" + randomString);
    }

    /**
     * <p>Сгенерирован случайный email и сохранен в переменную</p>
     */
    @И("^генерация случайного email и сохранение в переменную \"([^\"]*)\"$")
    public void randomEmail(String varName) {
        String randomEmail = MockNeat.secure().emails().val();
        coreScenario.setVar(varName, randomEmail);
        coreScenario.write("Email равен :" + randomEmail);
    }
}
