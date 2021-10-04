package ru.at.library.web.step.selenideelement;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.ru.А;
import io.cucumber.java.ru.И;
import io.qameta.allure.Story;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;

import static com.codeborne.selenide.Selenide.*;
import static ru.at.library.core.steps.OtherSteps.*;

/**
 * Действия с SelenideElement
 */
@Log4j2
public class SelenideElementActionSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * ######################################################################################################################
     */

    @И("^выполнено нажатие на (?:кнопку|элемент) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        clickOnElement(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" выполнено нажатие на (?:кнопку|элемент) \"([^\"]*)\"$")
    public void clickOnElement(String blockName, String elementName) {
        clickOnElement(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * На странице происходит click по заданному элементу
     */
    public void clickOnElement(SelenideElement element) {
        element.click();
    }

    /**
     * ######################################################################################################################
     */

    @И("^выполнено нажатие c ховером на (?:кнопку|элемент) \"([^\"]*)\"$")
    public void clickOnElementWithHover(String elementName) {
        clickOnElementWithHover(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" выполнено нажатие c ховером на (?:кнопку|элемент) \"([^\"]*)\"$")
    public void clickOnElementWithHover(String blockName, String elementName) {
        clickOnElementWithHover(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * На странице происходит hover и click по заданному элементу
     */
    public void clickOnElementWithHover(SelenideElement element) {
        element.hover().click();
    }

    /**
     * ######################################################################################################################
     */

    @И("^выполнен ховер на элемент \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        elementHover(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" выполнен ховер на элемент \"([^\"]*)\"$")
    public void elementHover(String blockName, String elementName) {
        elementHover(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Выполняется наведение курсора на элемент
     */
    public void elementHover(SelenideElement element) {
        element.hover();
    }

    /**
     * ######################################################################################################################
     */

    @И("^выполнено нажатие на (?:кнопку|элемент) \"([^\"]*)\" и переход на новую вкладку$")
    public void clickOnElementAndSwitchToNewTab(String elementName) {
        clickOnElementAndSwitchToNewTab(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" выполнено нажатие на (?:кнопку|элемент) \"([^\"]*)\" и переход на новую вкладку$")
    public void clickOnElementAndSwitchToNewTab(String blockName, String elementName) {
        clickOnElementAndSwitchToNewTab(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    public void clickOnElementAndSwitchToNewTab(SelenideElement element) {
        element.clear();
        Selenide.switchTo().window(WebDriverRunner.getWebDriver().getWindowHandles().size() - 1);
    }

    /**
     * ######################################################################################################################
     */

    @SuppressWarnings("deprecation")
    @И("^выполнено нажатие на элемент с текстом \"([^\"]*)\"$")
    public void clickingElementWithText(String text) {
        coreScenario.getCurrentPage().getSelf()
                .$(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)))).click();
    }

    @SuppressWarnings("deprecation")
    @И("^в блоке \"([^\"]*)\" выполнено нажатие на элемент с текстом \"([^\"]*)\"$")
    public void clickingElementWithText(String blockName, String text) {
        coreScenario.getCurrentPage().getBlock(blockName).getSelf()
                .$(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)))).click();
    }

    /**
     * ######################################################################################################################
     */

    @А("^в поле \"([^\"]*)\" введено значение$")
    @И("^в поле \"([^\"]*)\" введено значение \"([^\"]*)\"$")
    public String setFieldValue(String elementName, String value) {
        return setFieldValue(
                coreScenario.getCurrentPage().getElement(elementName),
                value);
    }

    @А("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено значение$")
    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено значение \"([^\"]*)\"$")
    public String setFieldValue(String blockName, String elementName, String value) {
        return setFieldValue(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                value);
    }

    /**
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     */
    public String setFieldValue(SelenideElement element, String value) {
       // cleanInput(element);
        value = getPropertyOrStringVariableOrValue(value);
        element.setValue(value);
        return value;
    }

    /**
     * ######################################################################################################################
     */

    @А("^в поле \"([^\"]*)\" посимвольно набирается значение$")
    @И("^в поле \"([^\"]*)\" посимвольно набирается значение \"([^\"]*)\"$")
    public void sendKeysCharacterByCharacter(String elementName, String value) {
        sendKeysCharacterByCharacter(
                coreScenario.getCurrentPage().getElement(elementName),
                value);
    }

    @А("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" посимвольно набирается значение$")
    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" посимвольно набирается значение \"([^\"]*)\"$")
    public void sendKeysCharacterByCharacter(String blockName, String elementName, String value) {
        sendKeysCharacterByCharacter(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                value);
    }

    /**
     * Набирается значение посимвольно (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле очищается
     */
    public void sendKeysCharacterByCharacter(SelenideElement element, String value) {
        value = getPropertyOrStringVariableOrValue(value);
//        cleanInput(element);
        for (char character : value.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            sleep(100);
        }
    }

    /**
     * ######################################################################################################################
     */

    @А("^в поле \"([^\"]*)\" дописывается значение$")
    @И("^в поле \"([^\"]*)\" дописывается значение \"([^\"]*)\"$")
    public void valueIsAppended(String elementName, String value) {
        valueIsAppended(
                coreScenario.getCurrentPage().getElement(elementName),
                value
        );
    }

    @А("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" дописывается значение$")
    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" дописывается значение \"([^\"]*)\"$")
    public void valueIsAppended(String blockName, String elementName, String value) {
        valueIsAppended(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                value
        );
    }

    /**
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     */
    public void valueIsAppended(SelenideElement element, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        String oldValue = element.getValue();
        if (oldValue == null || oldValue.isEmpty()) {
            oldValue = element.getText();
        }
        element.setValue("");
        element.setValue(oldValue + value);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" набирается текущая дата в формате \"([^\"]*)\"$")
    public void currentDateIsTypedInTheFormat(String elementName, String dateFormat) {
        currentDateIsTypedInTheFormat(
                coreScenario.getCurrentPage().getElement(elementName),
                dateFormat);
    }

    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" набирается текущая дата в формате \"([^\"]*)\"$")
    public void currentDateIsTypedInTheFormat(String blockName, String elementName, String dateFormat) {
        currentDateIsTypedInTheFormat(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                dateFormat);
    }

    /**
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    public void currentDateIsTypedInTheFormat(SelenideElement element, String dateFormat) {
        dateFormat = getPropertyOrStringVariableOrValue(dateFormat);
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.trace("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        element.setValue("");
        element.setValue(currentStringDate);
        log.trace("Текущая дата " + currentStringDate);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" с помощью горячих клавиш вставлено значение \"([^\"]*)\"$")
    public void pasteValueToTextField(String elementName, String value) {
        pasteValueToTextField(
                coreScenario.getCurrentPage().getElement(elementName),
                value
        );
    }

    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" с помощью горячих клавиш вставлено значение \"([^\"]*)\"$")
    public void pasteValueToTextField(String blockName, String elementName, String value) {
        pasteValueToTextField(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                value
        );
    }

    /**
     * Ввод в поле указанного текста (в приоритете: из property, из переменной сценария, значение аргумента),
     * используя буфер обмена и клавиши SHIFT + INSERT
     */
    public void pasteValueToTextField(SelenideElement element, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        element.sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * ######################################################################################################################
     */

    @И("^очищено поле \"([^\"]*)\"$")
    public void clearInput(String elementName) {
        clearInput(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" очищено поле \"([^\"]*)\"$")
    public void clearInput(String blockName, String elementName) {
        clearInput(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Очищается заданное поле
     */
    public void clearInput(SelenideElement element) {
        element.clear();

        if (element.getAttribute("value").length() > 0 ||
                        element.getValue().length() > 0 ||
                        element.is(Condition.not(Condition.empty))
        ) {
            for (int i = 0; i < 225; i++) {
                element.sendKeys(Keys.BACK_SPACE);
            }
        }
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, String seqLengthString, String lang) {
        setRandomCharSequence(
                coreScenario.getCurrentPage().getElement(elementName),
                seqLengthString,
                lang);
    }

    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String blockName, String elementName, String seqLengthString, String lang) {
        setRandomCharSequence(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seqLengthString,
                lang);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     */
    public String setRandomCharSequence(SelenideElement element, String seqLengthString, String lang) {
        int seqLength = Integer.parseInt(seqLengthString);

        String charSeq = getRandCharSequence(seqLength, lang);
        element.setValue(charSeq);
        log.trace("Строка случайных символов равна :" + charSeq);

        return charSeq;
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, String seqLengthString, String lang, String varName) {
        String charSeq = setRandomCharSequence(
                coreScenario.getCurrentPage().getElement(elementName),
                seqLengthString,
                lang);

        coreScenario.setVar(varName, charSeq);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     */
    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String blockName, String elementName, String seqLengthString, String lang, String varName) {
        String charSeq = setRandomCharSequence(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seqLengthString,
                lang);

        coreScenario.setVar(varName, charSeq);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" введено случайное число из \"([^\"]*)\" (?:цифр|цифры)$")
    public String inputRandomNumSequence(String elementName, String seqLengthString) {
        return inputRandomNumSequence(
                coreScenario.getCurrentPage().getElement(elementName),
                seqLengthString);
    }

    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено случайное число из \"([^\"]*)\" (?:цифр|цифры)$")
    public String inputRandomNumSequence(String blockName, String elementName, String seqLengthString) {
        return inputRandomNumSequence(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seqLengthString);
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины
     *
     * @return сгенерированное число
     */
    public String inputRandomNumSequence(SelenideElement element, String seqLengthString) {
        int seqLength = Integer.parseInt(seqLengthString);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        return setFieldValue(element, numSeq);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String elementName, int seqLengthString, String varName) {
        inputAndSetRandomNumSequence(
                coreScenario.getCurrentPage().getElement(elementName),
                seqLengthString,
                varName);
    }

    @И("^в блоке \"([^\"]*)\" в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String blockName, String elementName, int seqLengthString, String varName) {
        inputAndSetRandomNumSequence(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seqLengthString,
                varName);
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     */
    public void inputAndSetRandomNumSequence(SelenideElement element, int seqLengthString, String varName) {
        String value = inputRandomNumSequence(element, String.valueOf(seqLengthString));
        coreScenario.setVar(varName, value);
    }

    /**
     * ######################################################################################################################
     */

    @И("^страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String elementName) {
        scrollPageToElement(
                coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String blockName, String elementName) {
        scrollPageToElement(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     */
    public void scrollPageToElement(SelenideElement element) {
        element.scrollTo();
    }


    /**
     * ######################################################################################################################
     */

    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String elementName) {
        scrollWhileElemNotFoundOnPage(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String blockName, String elementName) {
        scrollWhileElemNotFoundOnPage(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    public void scrollWhileElemNotFoundOnPage(SelenideElement element) {
        do {
            element.scrollTo();
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
    }

    /**
     * ######################################################################################################################
     */

    @SuppressWarnings("deprecation")
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
        scrollWhileElemWithTextNotFoundOnPage(
                coreScenario.getCurrentPage().getSelf()
                        .$(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))))
        );
    }

    @SuppressWarnings("deprecation")
    @И("^в блоке \"([^\"]*)\" страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String blockName, String expectedValue) {
        scrollWhileElemWithTextNotFoundOnPage(
                coreScenario.getCurrentPage().getBlock(blockName).getSelf()
                        .$(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))))
        );
    }

    /**
     * Скроллит страницу вниз до появления элемента с текстом из property файла, из переменной сценария или указанному в шаге каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    public void scrollWhileElemWithTextNotFoundOnPage(SelenideElement element) {
        ((JavascriptExecutor) WebDriverRunner.getWebDriver()).executeScript("arguments[0].scrollIntoView();", element);
    }

}