package ru.at.library.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.А;
import cucumber.api.java.ru.И;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.*;
import static ru.at.library.core.steps.OtherSteps.*;

/**
 * WEB шаги
 * <p>
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
public class WebCheckSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

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
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
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
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void elementHiddenSecond(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(hidden, seconds * 1000);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\"")
    public void elemDisappears(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disappears);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение seconds
     *
     * @param elementName название
     * @param seconds     время в секундах
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в течение (\\d+) (?:секунд|секунды)")
    public void elemDisappears(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(disappears, seconds * 1000);
    }


    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @И("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else coreScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Проверка того, что блок отображается
     */
    @И("^(?:блок|форма) \"([^\"]*)\" отображается на странице")
    public void blockAppeared(String nameOfPage) {
        coreScenario.getPage(nameOfPage).isAppeared();
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
    @И("^значение (?:поля|элемента|текста) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        String text = coreScenario.getCurrentPage().getElement(elementName).getText();
        coreScenario.setVar(variableName, text);
        coreScenario.write("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" кликабельно в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
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
     * Проверка, что элемент содержит указанный класс (в приоритете: из property, из переменной сценария, значение аргумента)
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" содержит класс со значением \"([^\"]*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        checkElemContainsAtrWithValue(elementName,"class",expectedClassValue);
    }

    /**
     * Проверка, что элемент не содержит указанный класс
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" не содержит класс со значением \"([^\"]*)\"$")
    public void checkElemClassNotContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);
        element.shouldHave(not(cssClass(expectedClassValue)));
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
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disabled);
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
     * Проверка, что поле недоступно для редактирования
     */
    @И("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disabled);
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
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @И("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        int length = coreScenario.getCurrentPage().getElement(elementName).getText().length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }

//---------------------------------------------------Проверки в Блоке
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------
//---------------------------------------------------

    /**
     * Проверка появления элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" отображается на странице")
    public void elementAppeared(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.waitUntil(appear, seconds * 1000);
    }

    /**
     * Проверка не отображения элемента(не списка) на странице в течение Configuration.timeout.
     *
     * @param elementName название
     */
    @А("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не отображается на странице")
    public void elementHidden(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.waitUntil(disappears, seconds * 1000);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:доступна|доступно|доступен) для нажатия")
    public void clickableField(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:доступна|доступно|доступен) для нажатия в течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:недоступна|недоступно|недоступен) для (?:нажатия|редактирования)$")
    public void buttonIsNotActive(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(disabled);
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:недоступна|недоступно|недоступен) для (?:нажатия|редактирования) в течение (\\d+) (?:секунд|секунды)$")
    public void buttonIsNotActive(String elementName, String blockName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.waitUntil(disabled, second * 1000);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @И("^поле \"([^\"]*)\" в блоке \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(empty);
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^(?:кнопка|ссылка|поле|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" (?:выбрана|выбрано|выбран)$")
    public void radioButtonIsSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(selected);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^(?:кнопка|ссылка|поле|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" не (?:выбрана|выбрано|выбран)$")
    public void radioButtonIsNotSelected(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(not(selected));
    }

    /**
     * Проверка, что радиокнопка выбрана
     */
    @И("^чекбокс \"([^\"]*)\" в блоке \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        element.shouldHave(checked);
    }

    /**
     * Проверка, что радиокнопка не выбрана
     */
    @И("^чекбокс \"([^\"]*)\" в блоке \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String elementName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);

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
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }


    /**
     * Проверка, что у элемента есть css с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|чекбокс|радиокнопка|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public void checkCssInElement(String elementName, String blockName, String cssName, String cssValue) {
        SelenideElement element = coreScenario.getCurrentPage().getBlockElement(blockName,elementName);
        cssName = getPropertyOrStringVariableOrValue(cssName);
        cssValue = getPropertyOrStringVariableOrValue(cssValue);
        element.shouldHave(cssValue(cssName, cssValue));
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @И("^значение (?:кнопки|ссылки|поля|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String blockName, String variableName) {
        String text = coreScenario.getCurrentPage().getBlockElement(blockName,elementName).getText();
        coreScenario.setVar(variableName, text);
        coreScenario.write("Значение [" + text + "] сохранено в переменную [" + variableName + "]");
    }

}
