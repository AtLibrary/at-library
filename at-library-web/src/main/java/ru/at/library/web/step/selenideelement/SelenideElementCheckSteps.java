package ru.at.library.web.step.selenideelement;

import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.А;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.time.Duration;
import java.time.LocalTime;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.sleep;
import static org.testng.Assert.assertEquals;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getTranslateNormalizeSpaceText;

/**
 * Проверки SelenideElement
 */
@Log4j2
public class SelenideElementCheckSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отображается на странице$")
    public void elementAppeared(String elementName) {
        elementAppeared(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отображается на странице$")
    public void elementAppeared(String blockName, String elementName) {
        elementAppeared(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в properties не задано,
     * таймаут равен 10 секундам
     */
    public void elementAppeared(SelenideElement element) {
        element.shouldHave(appear);
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отобразится на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementAppearedSecond(String elementName, int seconds) {
        elementAppearedSecond(
                coreScenario.getCurrentPage().getElement(elementName),
                seconds);
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отобразится на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementAppearedSecond(String blockName, String elementName, int seconds) {
        elementAppearedSecond(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seconds);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     */
    public void elementAppearedSecond(SelenideElement element, int seconds) {
        element.shouldHave(appear, Duration.ofSeconds(seconds));
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementHidden(String elementName) {
        elementHidden(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementHidden(String blockName, String elementName) {
        elementHidden(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в properties не задано,
     * таймаут равен 10 секундам
     *
     * @param element SelenideElement
     */
    public void elementHidden(SelenideElement element) {
        element.shouldHave(hidden);
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не (?:отобразится|отображается) на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementHiddenSecond(String elementName, int seconds) {
        elementHiddenSecond(
                coreScenario.getCurrentPage().getElement(elementName),
                seconds);
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не (?:отобразится|отображается) на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementHiddenSecond(String blockName, String elementName, int seconds) {
        elementHiddenSecond(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                seconds);
    }

    /**
     * @param element SelenideElement
     * @param seconds количество секунд
     */
    public void elementHiddenSecond(SelenideElement element, int seconds) {
        int time = LocalTime.now().toSecondOfDay() + seconds;

        int timeout = 200;
        while (time > LocalTime.now().toSecondOfDay()) {
            element.shouldHave(hidden, Duration.ofSeconds(seconds));
            sleep(timeout);
        }
    }

    /**
     * ######################################################################################################################
     */

    @И("^элемент \"([^\"]*)\" расположен в видимой части страницы$")
    public void elementInVisiblePartOfBrowser(String elementName) {
        elementInBounds(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" элемент \"([^\"]*)\" расположен в видимой части страницы$")
    public void elementInVisiblePartOfBrowser(String blockName, String elementName) {
        elementInBounds(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка появления элемента(не списка) в видимой части браузера
     *
     * @param element элемент для проверки
     */
    private void elementInBounds(SelenideElement element) {
        int elementLeftBound = element.getLocation().x;
        int elementUpperBound = element.getLocation().y;
        int elementRightBound = elementLeftBound + element.getSize().width;
        int elementLowerBound = elementUpperBound + element.getSize().height;

        long winLeftBound = executeJavaScript("return window.pageXOffset");
        long winUpperBound = executeJavaScript("return window.pageYOffset");
        long winWidth = executeJavaScript("return document.documentElement.clientWidth");
        long winHeight = executeJavaScript("return document.documentElement.clientHeight");
        long winRightBound = winLeftBound + winWidth;
        long winLowerBound = winUpperBound + winHeight;

        boolean inBounds = winLeftBound <= elementLeftBound
                && winUpperBound <= elementUpperBound
                && winRightBound >= elementRightBound
                && winLowerBound >= elementLowerBound;

        assertEquals(
                true, inBounds,
                String.format("Элемент вне видимой части браузера. Видимая область: %d %d %d %d Координаты элемента: %d %d %d %d",
                        winLeftBound, winUpperBound, winRightBound, winLowerBound, elementLeftBound, elementUpperBound, elementRightBound, elementLowerBound));
    }

    /**
     * ######################################################################################################################
     */

    @И("^элемент содержащий текст \"([^\"]*)\" расположен в видимой части страницы$")
    public void elementWihTextInVisiblePartOfBrowser(String expectedValue) {
        elementInBounds(
                coreScenario.getCurrentPage().getSelf().
                        $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))))
        );
    }

    @И("^в блоке \"([^\"]*)\" элемент содержащий текст \"([^\"]*)\" расположен в видимой части страницы$")
    public void elementWihTextInVisiblePartOfBrowser(String blockName, String expectedValue) {
        elementInBounds(
                coreScenario.getCurrentPage().getBlock(blockName).getSelf().
                        $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))))
        );
    }

    /**
     * ######################################################################################################################
     */

    /**
     * ----------------------------------------Проверки доступности для нажатия-----------------------------------------
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия$")
    public void clickableField(String elementName) {
        clickableField(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия$")
    public void clickableField(String blockName, String elementName) {
        clickableField(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    public void clickableField(SelenideElement element) {
        element.shouldHave(enabled);
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        clickableField(
                coreScenario.getCurrentPage().getElement(elementName),
                second);
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String blockName, String elementName, int second) {
        clickableField(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                second);
    }

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    public void clickableField(SelenideElement element, int second) {
        element.shouldHave(enabled, Duration.ofSeconds(second));
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
    public void buttonIsNotActive(String elementName) {
        buttonIsNotActive(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
    public void buttonIsNotActive(String blockName, String elementName) {
        buttonIsNotActive(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что элемент недоступен для нажатия
     */
    public void buttonIsNotActive(SelenideElement element) {
        element.shouldHave(disabled);
    }

    /**
     * ######################################################################################################################
     */

    /**
     * -----------------------------------------Проверки содержимого элементов------------------------------------------
     */

    /**
     * ######################################################################################################################
     */

    @И("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        fieldInputIsEmpty(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String blockName, String elementName) {
        fieldInputIsEmpty(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    public void fieldInputIsEmpty(SelenideElement element) {
        element.shouldHave(empty);
    }

    /**
     * ######################################################################################################################
     */

    @И("^значение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        storeElementValueInVariable(
                coreScenario.getCurrentPage().getElement(elementName),
                variableName);
    }

    @И("^в блоке \"([^\"]*)\" значение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String blockName, String elementName, String variableName) {
        storeElementValueInVariable(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                variableName);
    }

    /**
     * Сохранение значения элемента в переменную
     */
    public void storeElementValueInVariable(SelenideElement element, String variableName) {
        String text = element.getText();
        coreScenario.setVar(variableName, text);
        log.trace("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        checkElemContainsAtrWithValue(
                coreScenario.getCurrentPage().getElement(elementName),
                attribute,
                expectedAttributeValue
        );
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkElemContainsAtrWithValue(String blockName, String elementName, String attribute, String expectedAttributeValue) {
        checkElemContainsAtrWithValue(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                attribute,
                expectedAttributeValue
        );
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    public void checkElemContainsAtrWithValue(SelenideElement element, String attribute, String expectedAttributeValue) {
        attribute = getPropertyOrStringVariableOrValue(attribute);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * ######################################################################################################################
     */

    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkCssInElement(String elementName, String cssName, String cssValue) {
        checkCssInElement(
                coreScenario.getCurrentPage().getElement(elementName),
                cssName,
                cssValue
        );
    }

    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkCssInElement(String blockName, String elementName, String cssName, String cssValue) {
        checkCssInElement(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                cssName,
                cssValue
        );
    }

    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    public void checkCssInElement(SelenideElement element, String cssName, String cssValue) {
        cssName = getPropertyOrStringVariableOrValue(cssName);
        cssValue = getPropertyOrStringVariableOrValue(cssValue);
        element.shouldHave(cssValue(cssName, cssValue));
    }

    /**
     * ######################################################################################################################
     */

    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст")
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст \"([^\"]*)\"$")
    public void testFieldContainsInnerText(String elementName, String expectedValue) {
        testFieldContainsInnerText(
                coreScenario.getCurrentPage().getElement(elementName),
                expectedValue
        );
    }

    @А("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст")
    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст \"([^\"]*)\"$")
    public void testFieldContainsInnerText(String blockName, String elementName, String expectedValue) {
        testFieldContainsInnerText(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                expectedValue
        );
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    public void testFieldContainsInnerText(SelenideElement element, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента содержит",
                        text(expectedValue),
                        value(expectedValue)));
    }

    /**
     * ######################################################################################################################
     */

    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст")
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст \"([^\"]*)\"$")
    public void testFieldNotContainsInnerText(String elementName, String expectedValue) {
        testFieldNotContainsInnerText(
                coreScenario.getCurrentPage().getElement(elementName),
                expectedValue
        );
    }

    @А("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст")
    @И("^в блоке \"([^\"]*)\" (?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст \"([^\"]*)\"$")
    public void testFieldNotContainsInnerText(String blockName, String elementName, String expectedValue) {
        testFieldNotContainsInnerText(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                expectedValue
        );
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    public void testFieldNotContainsInnerText(SelenideElement element, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                and("Текст элемента не содержит",
                        not(text(expectedValue)),
                        not(value(expectedValue))));
    }

    /**
     * ######################################################################################################################
     */

    @И("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен \"([^\"]*)\"$")
    @А("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        compareValInFieldAndFromStep(
                coreScenario.getCurrentPage().getElement(elementName),
                expectedValue);
    }


    @И("^в блоке \"([^\"]*)\" текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен \"([^\"]*)\"$")
    @А("^в блоке \"([^\"]*)\" текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен$")
    public void compareValInFieldAndFromStep(String blockName, String elementName, String expectedValue) {
        compareValInFieldAndFromStep(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                expectedValue);
    }


    /**
     * Проверка, что текста в поле равен значению, указанному в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    public void compareValInFieldAndFromStep(SelenideElement element, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента равен",
                        exactText(expectedValue),
                        exactValue(expectedValue)));
    }

    /**
     * ######################################################################################################################
     */

    @И("^в (?:кнопке|ссылке|поле|чекбоксе|радиокнопке|тексте|элементе) \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        checkFieldSymbolsCount(
                coreScenario.getCurrentPage().getElement(elementName),
                num);
    }


    @И("^в блоке \"([^\"]*)\" в (?:кнопке|ссылке|поле|чекбоксе|радиокнопке|тексте|элементе) \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String blockName, String elementName, int num) {
        checkFieldSymbolsCount(
                coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName),
                num);
    }


    /**
     * Производится проверка количества символов в элементе со значением, указанным в шаге
     */
    public void checkFieldSymbolsCount(SelenideElement element, int num) {
        element.should(visible);
        int length;
        if (element.getTagName().equalsIgnoreCase("input")) {
            length = element.getAttribute("value").length();
        } else {
            length = element.getText().length();
        }
        assertEquals(num, length,
                String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length));
    }

    /**
     * ######################################################################################################################
     */

    /**
     * -----------------------------------------Проверки радиокнопок/чекбоксов------------------------------------------
     */

    /**
     * ######################################################################################################################
     */

    @И("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        radioButtonIsSelected(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String blockName, String elementName) {
        radioButtonIsSelected(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    public void radioButtonIsSelected(SelenideElement element) {
        element.shouldHave(selected);
    }

    /**
     * ######################################################################################################################
     */

    @И("^радиокнопка \"([^\"]*)\" не выбрана")
    public void radioButtonIsNotSelected(String elementName) {
        radioButtonIsNotSelected(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" радиокнопка \"([^\"]*)\" не выбрана")
    public void radioButtonIsNotSelected(String blockName, String elementName) {
        radioButtonIsNotSelected(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    public void radioButtonIsNotSelected(SelenideElement element) {
        element.shouldHave(not(selected));
    }

    /**
     * ######################################################################################################################
     */

    @И("^чекбокс \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String elementName) {
        checkBoxIsChecked(coreScenario.getCurrentPage().getElement(elementName));
    }


    @И("^в блоке \"([^\"]*)\" чекбокс \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String blockName, String elementName) {
        checkBoxIsChecked(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что чекбокс отмечен
     */
    public void checkBoxIsChecked(SelenideElement element) {
        element.shouldHave(checked);
    }

    /**
     * ######################################################################################################################
     */

    @И("^чекбокс \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String elementName) {
        checkBoxIsNotChecked(coreScenario.getCurrentPage().getElement(elementName));
    }

    @И("^в блоке \"([^\"]*)\" чекбокс \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String blockName, String elementName) {
        checkBoxIsNotChecked(coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName));
    }

    /**
     * Проверка, что чекбокс не отмечен
     */
    public void checkBoxIsNotChecked(SelenideElement element) {
        element.shouldHave(not(checked));
    }
}
