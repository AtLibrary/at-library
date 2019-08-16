package ru.bcs.at.library.core.steps;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import cucumber.api.java.ru.И;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.*;
import static ru.bcs.at.library.core.steps.OtherSteps.*;
import static ru.bcs.at.library.core.steps.WebTestConfig.DEFAULT_TIMEOUT;

/**
 * <h1 style="color: green; font-size: 2.2em">
 * WEB шаги
 * </h1>
 *
 * <p style="color: green; font-size: 1.5em">
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.</p>
 */
public class WebCheckSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(appear, DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название кнопки|поля|блока
     * @param seconds     количество секунд
     *                    </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(appear, seconds * 1000);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|блока|чекбокса|радиокнопи|текста|элемента) \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(disappears, DEFAULT_TIMEOUT);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
     * </p>
     *
     * @param elementName  название поля|элемента
     * @param variableName имя переменной
     */
    @И("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        String expectedValue = coreScenario.getVar(variableName).toString();
        element.shouldHave(exactText(expectedValue));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что блок исчез/стал невидимым
     * </p>
     */
    @И("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else coreScenario.getPage(nameOfPage).disappeared();
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле для ввода пусто
     * </p>
     */
    @И("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(empty, DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сохранение значения элемента в переменную
     * </p>
     */
    @И("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        coreScenario.setVar(variableName, coreScenario.getCurrentPage().getAnyElementText(elementName));
        coreScenario.write("Значение [" + coreScenario.getCurrentPage().getAnyElementText(elementName) + "] сохранено в переменную [" + variableName + "]");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, DEFAULT_TIMEOUT);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);
        currentElement.waitUntil(attribute(attribute, expectedAttributeValue), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент содержит указанный класс (в приоритете: из property, из переменной сценария, значение аргумента)
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" содержит класс со значением \"(.*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);

        currentElement.waitUntil(
                attribute("class", expectedClassValue), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент не содержит указанный класс
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" не содержит класс со значением \"(.*)\"$")
    public void checkElemClassNotContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);

        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);

        currentElement.waitUntil(
                not(attribute("class", expectedClassValue)), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле содержит значение (в приоритете: из property, из переменной сценария, значение аргумента),
     * указанное в шаге
     * </p>
     */
    @И("^(?:поле|элемент|текст) \"([^\"]*)\" содержит (?:значение|текст) \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        coreScenario.getCurrentPage().getElement(elementName)
                .waitUntil(text(expectedValue), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     * </p>
     */
    @И("^(?:поле|элемент|текст) \"([^\"]*)\" содержит внутренний текст \"(.*)\"$")
    public void testFieldContainsInnerText(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.waitUntil(text(expectedValue), DEFAULT_TIMEOUT);
        } catch (ElementShould ex) {
            element.waitUntil(value(expectedValue), DEFAULT_TIMEOUT);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Не чувствителен к регистру
     * </p>
     */
    @И("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит внутренний текст \"(.*)\"$")
    public void testFieldNotContainsInnerText(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.waitUntil(not(text(expectedValue)), DEFAULT_TIMEOUT);
        } catch (ElementShould ex) {
            element.waitUntil(not(value(expectedValue)), DEFAULT_TIMEOUT);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @И("^(?:значение|содержимое) (?:поля|элемента|текста) \"([^\"]*)\" (?:совпадает с текстом|равно) \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.waitUntil(exactText(expectedValue), DEFAULT_TIMEOUT);
        } catch (ElementShould ex) {
            element.waitUntil(exactValue(expectedValue), DEFAULT_TIMEOUT);
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что кнопка/ссылка недоступна для нажатия
     * </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(disabled, DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка выбрана
     * </p>
     */
    @И("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(selected, DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка не выбрана
     * </p>
     */
    @И("^радиокнопка \"([^\"]*)\" не выбрана")
    public void radioButtonIsNotSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(not(selected), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка выбрана
     * </p>
     */
    @И("^чекбокс \"([^\"]*)\" выбран$")
    public void checkBoxIsChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(checked, DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка не выбрана
     * </p>
     */
    @И("^чекбокс \"([^\"]*)\" не выбран$")
    public void checkBoxIsNotChecked(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(not(checked), DEFAULT_TIMEOUT);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле нередактируемо
     * </p>
     */
    @И("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(disabled, DEFAULT_TIMEOUT);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     * </p>
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
     * <p style="color: green; font-size: 1.5em">
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     * </p>
     */
    @И("^файл \"(.*)\" загрузился в папку /Downloads$")
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
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка количества символов в поле со значением, указанным в шаге
     * </p>
     */
    @И("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String element, int num) {
        int length = coreScenario.getCurrentPage().getAnyElementText(element).length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Получение текста элемента в блоке и сохранение его в переменную
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     *                     </p>
     */
    @И("^значение (?:элемента|поля) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveTextElementInBlock(String elementName, String blockName, String variableName) {
        String elementText = coreScenario.getCurrentPage().getBlock(blockName).getAnyElementText(elementName);
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля в блоке совпадает со значением заданной переменной из хранилища
     * </p>
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     */
    @И("^значение (?:поля|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String blockName, String variableName) {
        String expectedValue = coreScenario.getVar(variableName).toString();
        coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName)
                .shouldHave(exactText(expectedValue));
    }

}
