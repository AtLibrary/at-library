package ru.at.library.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.А;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import ru.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.assertEquals;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getTranslateNormalizeSpaceText;

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
     * -----------------------------------------------------------------------------------------------------------------
     * ------------------------------------------------Проверки страниц-------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Проверка того, что блок отображается
     */
    @И("страница/блок/форма/вкладка {string} отображается на странице")
    public void blockAppeared(String nameOfPage) {
        coreScenario.getPage(nameOfPage).isAppeared();
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @И("страница/блок/форма/вкладка {string} не отображается на странице")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else {
            coreScenario.getPage(nameOfPage).disappeared();
        }
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Проверки элементов------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * -----------------------------------------Проверки отображения элементов------------------------------------------
     */

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} отображается на странице")
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
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} отобразится на странице в течение {int} секунд/секунды")
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
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} не отображается на странице")
    public void elementHidden(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(hidden);
    }

    /**
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} не отобразится на странице в течение {int} секунд/секунды")
    public void elementHiddenSecond(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        for (int i = 0; i < seconds * 2; ++i) {
            element.waitUntil(hidden,0);
            sleep(500);
        }
    }

    /**
     * Проверка появления элемента(не списка) в видимой части браузера
     *
     * @param elementName название
     */
    @И("элемент {string} расположен в видимой части страницы")
    public void elementInVisiblePartOfBrowser(String elementName) {
        elementInBounds(coreScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * Проверка появления элемента(не списка) с текстом из property файла, из переменной сценария в видимой части браузера
     *
     * @param expectedValue текст/переменная в property файле/переменная сценария
     */
    @И("элемент содержащий текст {string} расположен в видимой части страницы")
    public void elementWihTextInVisiblePartOfBrowser(String expectedValue) {
        elementInBounds(
                $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))))
        );
    }

    /**
     * ----------------------------------------Проверки доступности для нажатия-----------------------------------------
     */

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} доступна/доступен для нажатия")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице доступен для нажатия
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} доступна/доступен для нажатия в течение {int} секунд/секунды")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что элемент недоступен для нажатия
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} недоступна/недоступен для нажатия")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disabled);
    }

    /**
     * -----------------------------------------Проверки содержимого элементов------------------------------------------
     */

    /**
     * Проверка, что поле для ввода пусто
     */
    @И("поле {string} пусто")
    public void fieldInputIsEmpty(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(empty);
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @И("значение кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} сохранено в переменную {string}")
    public void storeElementValueInVariable(String elementName, String variableName) {
        String text = coreScenario.getCurrentPage().getElement(elementName).getText();
        coreScenario.setVar(variableName, text);
        log.trace("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} содержит атрибут {string} со значением {string}")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} содержит css {string} со значением {string}")
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
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} содержит текст {string}")
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} содержит текст")
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
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} не содержит текст {string}")
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} не содержит текст")
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
    @И("текст кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} равен {string}")
    @А("текст кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} равен")
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
    @И("в кнопке/ссылке/поле/чекбоксе/радиокнопке/тексте/элементе {string} содержится {int} символов")
    public void checkFieldSymbolsCount(String elementName, int num) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.should(visible);
        int length;
        if (element.getTagName().equalsIgnoreCase("input")) {
            length = element.getAttribute("value").length();
        } else {
            length = element.getText().length();
        }
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }

    /**
     * -----------------------------------------Проверки радиокнопок/чекбоксов------------------------------------------
     */

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("радиокнопка {string} выбрана")
    public void radioButtonIsSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(selected);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("радиокнопка {string} не выбрана")
    public void radioButtonIsNotSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(not(selected));
    }

    /**
     * Проверка, что чекбокс отмечен
     */
    @И("чекбокс {string} выбран")
    public void checkBoxIsChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(checked);
    }

    /**
     * Проверка, что чекбокс не отмечен
     */
    @И("чекбокс {string} не выбран")
    public void checkBoxIsNotChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(not(checked));
    }


    /**
     * -----------------------------------------------------------------------------------------------------------------
     * ------------------------------------------------Проверки в блоке-------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * -------------------------------------Проверки отображения элементов в блоке--------------------------------------
     */

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} отображается на странице")
    public void elementAppeared(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(appear);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} отображается на странице в течение {int} секунд/секунды")
    public void elementAppearedSecond(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.waitUntil(appear, seconds * 1000);
    }

    /**
     * Проверка не отображения элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} не отображается на странице")
    public void elementHidden(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(hidden);
    }

    /**
     * Проверка  не отображения элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} не отображается на странице в течение {int} секунд/секунды")
    public void elementHiddenSecond(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        for (int i = 0; i < seconds * 2; ++i) {
            element.waitUntil(hidden,0);
            sleep(500);
        }
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("ожидается исчезновение кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string}")
    public void elemDisappears(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(disappears);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение seconds
     *
     * @param elementName название
     * @param seconds     время в секундах
     */
    @И("ожидается исчезновение кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string} в течение {int} секунд/секунды")
    public void elemDisappears(String elementName, String blockName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.waitUntil(disappears, seconds * 1000);
    }

    /**
     * ------------------------------------Проверки доступности для нажатия в блоке-------------------------------------
     */

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} доступна/доступно/доступен для нажатия")
    public void clickableField(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} доступна/доступно/доступен для нажатия в течение {int} секунд/секунды")
    public void clickableField(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} недоступна/недоступно/недоступен для нажатия/редактирования")
    public void buttonIsNotActive(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(disabled);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} недоступна/недоступно/недоступен для нажатия/редактирования в течение {int} секунд/секунды")
    public void buttonIsNotActive(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        for (int i = 0; i < second * 2; ++i) {
            element.waitUntil(disabled,0);
            sleep(500);
        }
    }

    /**
     * -------------------------------------Проверки содержимого элементов в блоке--------------------------------------
     */

    /**
     * Проверка, что поле для ввода пусто
     */
    @И("поле {string} в блоке {string} пусто")
    public void fieldInputIsEmpty(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(empty);
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @И("значение кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string} сохранено в переменную {string}")
    public void storeElementValueInVariable(String elementName, String blockName, String variableName) {
        String text = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName).getText();
        coreScenario.setVar(variableName, text);
        log.trace("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} содержит атрибут {string} со значением {string}")
    public void checkElemContainsAtrWithValue(String elementName, String blockName, String attribute, String expectedAttributeValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} содержит css {string} со значением {string}")
    public void checkCssInElement(String elementName, String blockName, String cssName, String cssValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        cssName = getPropertyOrStringVariableOrValue(cssName);
        cssValue = getPropertyOrStringVariableOrValue(cssValue);
        element.shouldHave(cssValue(cssName, cssValue));
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     */
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} содержит текст {string}")
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} содержит текст")
    public void testFieldContainsInnerText(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
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
    @И("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} не содержит текст {string}")
    @А("кнопка/ссылка/поле/чекбокс/радиокнопка/текст/элемент {string} в блоке {string} не содержит текст")
    public void testFieldNotContainsInnerText(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
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
    @И("текст кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string} равен {string}")
    @А("текст кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string} равен")
    public void compareValInFieldAndFromStep(String elementName, String blockName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                or("Текст элемента равен",
                        exactText(expectedValue),
                        exactValue(expectedValue)));
    }

    /**
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @И("текст кнопки/ссылки/поля/чекбокса/радиокнопки/текста/элемента {string} в блоке {string} содержит {int} символов")
    public void checkFieldSymbolsCount(String elementName, String blockName, int num) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);

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
     * -------------------------------------Проверки радиокнопок/чекбоксов в блоке--------------------------------------
     */

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("кнопка/ссылка/поле/радиокнопка/текст/элемент {string} в блоке {string} выбрана/выбрано/выбран")
    public void radioButtonIsSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(selected);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("кнопка/ссылка/поле/радиокнопка/текст/элемент {string} в блоке {string} не выбрана/выбрано/выбран")
    public void radioButtonIsNotSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(not(selected));
    }

    /**
     * Проверка, что чекбокс отмечен
     */
    @И("чекбокс {string} в блоке {string} выбран")
    public void checkBoxIsChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(checked);
    }

    /**
     * Проверка, что чекбокс не отмечен
     */
    @И("чекбокс {string} в блоке {string} не выбран")
    public void checkBoxIsNotChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName);
        element.shouldHave(not(checked));
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * ---------------------------------------------Вспомогательные методы----------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

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

        assertEquals(String.format("Элемент вне видимой части браузера. Видимая область: %d %d %d %d Координаты элемента: %d %d %d %d",
                winLeftBound, winUpperBound, winRightBound, winLowerBound, elementLeftBound, elementUpperBound, elementRightBound, elementLowerBound),
                true, inBounds);
    }

}
