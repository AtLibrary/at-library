package ru.at.library.core;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import org.openqa.selenium.Keys;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.Assert.*;
import static ru.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.at.library.core.steps.OtherSteps.deleteFiles;
import static ru.at.library.core.steps.OtherSteps.getDownloadsDir;

public class OtherSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        switchTo().activeElement()
                .sendKeys(getKeyOrCharacter(buttonName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @param keyNames название клавиши
     *                 Эмулирует нажатие сочетания клавиш на клавиатуре.
     *                 Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     *                 | CONTROL |
     *                 | a       |
     */
    @И("^выполнено нажатие на сочетание клавиш из таблицы$")
    public void pressKeyCombination(List<String> keyNames) {
        Iterable<CharSequence> listKeys = keyNames.stream()
                .map(this::getKeyOrCharacter)
                .collect(Collectors.toList());
        String combination = Keys.chord(listKeys);
        switchTo().activeElement().sendKeys(combination);
    }


    /**
     * Выполняется запуск js-скрипта с указанием в js.executeScript его логики
     * Скрипт можно передать как аргумент метода или значение из application.properties
     */
    @И("^выполнен js-скрипт \"([^\"]*)\"")
    public void executeJsScript(String scriptName) {
        String content = loadValueFromFileOrPropertyOrVariableOrDefault(scriptName);
        Selenide.executeJavaScript(content);
    }


    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @И("^файл по пути \"([^\"]*)\" выгрузился в поле \"([^\"]*)\"$")
    public void uploadFile(String path, String fieldName) {
        coreScenario.getCurrentPage()
                .getElement(fieldName)
                .uploadFile(new File(path));
    }

    /**
     * Выполняется нажатие на кнопку и подгружается указанный файл
     * Селектор кнопки должны быть строго на input элемента
     * Можно указать путь до файла. Например, src/test/resources/example.pdf
     */
    @И("^выполнено нажатие на кнопку \"([^\"]*)\" и загружен файл \"([^\"]*)\"$")
    public void clickOnButtonAndUploadFile(String buttonName, String fileName) {
        String file = loadValueFromFileOrPropertyOrVariableOrDefault(fileName);
        File attachmentFile = new File(file);
        coreScenario.getCurrentPage().getElement(buttonName).uploadFile(attachmentFile);
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @И("^файл \"([^\"]*)\" загрузился в папку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым названием, содержащим текст [%s]", fileName),
                expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }

    /**
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     */
    @И("^открыта read-only форма$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue("Форма не read-only. Количество input-полей: " + inputsCount, inputsCount == 0);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue("Форма не read-only. Количество элементов textarea: " + textareasCount, textareasCount == 0);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
                .filter(SelenideElement::isDisplayed)
                .collect(Collectors.toList());
    }

    private CharSequence getKeyOrCharacter(String key) {
        try {
            return Keys.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return key;
        }
    }

}
