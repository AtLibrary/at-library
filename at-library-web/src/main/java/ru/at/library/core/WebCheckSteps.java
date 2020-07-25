package ru.at.library.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.А;
import cucumber.api.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.assertEquals;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;

/**
 * WEB шаги
 * <p>
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Log4j2
public class WebCheckSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ---------------------------------------TODO Проверки страниц------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Проверка того, что блок отображается
     */
    @И("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" отображается на странице$")
    public void blockAppeared(String nameOfPage) {
        coreScenario.getPage(nameOfPage).isAppeared();
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @И("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" не отображается на странице$")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else coreScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ---------------------------------------TODO Проверки элементов----------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отображается на странице$")
    public void elementAppeared(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(appear);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отобразится на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementAppearedSecond(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(appear, seconds * 1000);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementHidden(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(hidden);
    }

    /**
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не отобразится на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementHiddenSecond(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(hidden, seconds * 1000);
    }

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия$")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:доступна|доступен) для нажатия в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что элемент не недоступен для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disabled);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @И("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(empty);
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @И("^значение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        String text = coreScenario.getCurrentPage().getElement(elementName).getText();
        coreScenario.setVar(variableName, text);
        log.info("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkCssInElement(String elementName, String cssName, String cssValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        cssName = getPropertyOrStringVariableOrValue(cssName);
        cssValue = getPropertyOrStringVariableOrValue(cssValue);
        element.shouldHave(cssValue(cssName, cssValue));
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст \"([^\"]*)\"$")
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит текст")
    public void testFieldContainsInnerText(String elementName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента содержит",
                        text(expectedValue),
                        value(expectedValue)));
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст \"([^\"]*)\"$")
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит текст")
    public void testFieldNotContainsInnerText(String elementName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                and("Текст элемента не содержит",
                        not(text(expectedValue)),
                        not(value(expectedValue))));
    }

    /**
     * Проверка, что текста в поле равен значению, указанному в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен \"([^\"]*)\"$")
    @А("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" равен$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента равен",
                        exactText(expectedValue),
                        exactValue(expectedValue)));
    }

    /**
     * Производится проверка количества символов в элементе со значением, указанным в шаге
     */
    @И("^в (?:кнопке|ссылке|поле|чекбоксе|радиокнопке|тексте|элементе) \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        int length = coreScenario.getCurrentPage().getElement(elementName).getText().length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(selected);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^радиокнопка \"([^\"]*)\" не выбрана")
    public void radioButtonIsNotSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(not(selected));
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^чекбокс \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(checked);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^чекбокс \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(not(checked));
    }


    /**
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ---------------------------------------TODO Проверки в Блоке------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     * ------------------------------------------------------------------------------------------------------
     */


    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" отображается на странице")
    public void elementAppeared(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(appear);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" отображается на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementAppearedSecond(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.waitUntil(appear, seconds * 1000);
    }

    /**
     * Проверка не отображения элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не отображается на странице")
    public void elementHidden(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(hidden);
    }

    /**
     * Проверка  не отображения элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не отображается на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementHiddenSecond(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.waitUntil(hidden, seconds * 1000);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\"")
    public void elemDisappears(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(disappears);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение seconds
     *
     * @param elementName название
     * @param seconds     время в секундах
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" в течение (\\d+) (?:секунд|секунды)")
    public void elemDisappears(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.waitUntil(disappears, seconds * 1000);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:доступна|доступно|доступен) для нажатия")
    public void clickableField(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:доступна|доступно|доступен) для нажатия в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:недоступна|недоступно|недоступен) для (?:нажатия|редактирования)$")
    public void buttonIsNotActive(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(disabled);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:недоступна|недоступно|недоступен) для (?:нажатия|редактирования) в течение (\\d+) (?:секунд|секунды)$")
    public void buttonIsNotActive(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.waitUntil(disabled, second * 1000);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @И("^поле \"([^\"]*)\" в блоке \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(empty);
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^(?:кнопка|ссылка|поле|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:выбрана|выбрано|выбран)$")
    public void radioButtonIsSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(selected);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^(?:кнопка|ссылка|поле|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не (?:выбрана|выбрано|выбран)$")
    public void radioButtonIsNotSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(not(selected));
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^чекбокс \"([^\"]*)\" в блоке \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(checked);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^чекбокс \"([^\"]*)\" в блоке \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        element.shouldHave(not(checked));
    }


    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит текст \"([^\"]*)\"$")
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит текст")
    public void testFieldContainsInnerText(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента содержит",
                        text(expectedValue),
                        value(expectedValue)));
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не содержит текст \"([^\"]*)\"$")
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не содержит текст")
    public void testFieldNotContainsInnerText(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                and("Текст элемента не содержит",
                        not(text(expectedValue)),
                        not(value(expectedValue))));
    }


    /**
     * Проверка, что текста в поле равен значению, указанному в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" равен \"([^\"]*)\"$")
    @А("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" равен$")
    public void compareValInFieldAndFromStep(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента равен",
                        exactText(expectedValue),
                        exactValue(expectedValue)));
    }

    /**
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @И("^текст (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, String blockName, int num) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);

        int lengthText = element.getText().length();
        int lengthValue = element.getValue().length();

        int sleepTime = 100;
        for (int time = 0; time < Configuration.timeout; time += sleepTime) {
            if (lengthText == num || lengthValue == num) {
                return;
            }
            sleep(sleepTime);
        }

        BrowserSteps.takeScreenshot();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, lengthText), num, lengthText);
    }


    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String blockName, String attribute, String expectedAttributeValue) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }


    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkCssInElement(String elementName, String blockName, String cssName, String cssValue) {
        SelenideElement element = coreScenario.getPage(blockName).getElement(elementName);
        cssName = getPropertyOrStringVariableOrValue(cssName);
        cssValue = getPropertyOrStringVariableOrValue(cssValue);
        element.shouldHave(cssValue(cssName, cssValue));
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @И("^значение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String blockName, String variableName) {
        String text = coreScenario.getPage(blockName).getElement(elementName).getText();
        coreScenario.setVar(variableName, text);
        log.info("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

}
