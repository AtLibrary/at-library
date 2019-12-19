package ru.bcs.at.library.core.steps;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.*;
import ru.bcs.at.library.core.cucumber.api.CorePage;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.*;
import static ru.bcs.at.library.core.steps.OtherSteps.*;

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
     * @param elementName название кнопки|поля|блока
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(appear);
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название кнопки|поля|блока
     * @param seconds     количество секунд
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(appear, seconds * 1000);
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение Configuration.timeout.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|блока|чекбокса|радиокнопи|текста|элемента) \"([^\"]*)\"")
    public void elemDisappears(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(disappears);
    }

    /**
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
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
     * Проверка того, что блок исчез/стал невидимым
     */
    @И("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else coreScenario.getPage(nameOfPage).disappeared();
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
    @И("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        coreScenario.setVar(variableName, coreScenario.getCurrentPage().getAnyElementText(elementName));
        coreScenario.write("Значение [" + coreScenario.getCurrentPage().getAnyElementText(elementName) + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(enabled);
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        element.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * Проверка, что элемент содержит указанный класс (в приоритете: из property, из переменной сценария, значение аргумента)
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" содержит класс со значением \"(.*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);
        element.shouldHave(cssClass(expectedClassValue));
    }

    /**
     * Проверка, что элемент не содержит указанный класс
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" не содержит класс со значением \"(.*)\"$")
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
    @И("^(?:поле|элемент|текст) \"([^\"]*)\" содержит значение \"(.*)\"$")
    @То("^(?:поле|элемент|текст) \"([^\"]*)\" содержит текст \"(.*)\"$")
    @Но("^(?:поле|элемент|текст) \"([^\"]*)\" содержит внутренний текст \"(.*)\"$")
    @А("^(?:поле|элемент|текст) \"([^\"]*)\" содержит значение$")
    @Тогда("^(?:поле|элемент|текст) \"([^\"]*)\" содержит текст$")
    @Когда("^(?:поле|элемент|текст) \"([^\"]*)\" содержит внутренний текст$")
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
    @И("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит значение \"(.*)\"$")
    @То("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит текст \"(.*)\"$")
    @Но("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит внутренний текст \"(.*)\"$")
    @А("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит значение$")
    @Тогда("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит текст$")
    @Когда("^(?:поле|элемент|текст) \"([^\"]*)\" не содержит внутренний текст$")
    public void testFieldNotContainsInnerText(String elementName, String expectedValue) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        element.shouldHave(
                and("Текст элемента не содержит",
                        not(text(expectedValue)),
                        not(value(expectedValue))));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^содержимое (?:поля|элемента|текста) \"([^\"]*)\" равно \"([^\"]*)\"$")
    @То("^содержимое (?:поля|элемента|текста) \"([^\"]*)\" совпадает с текстом \"([^\"]*)\"$")
    @Но("^значение (?:поля|элемента|текста) \"([^\"]*)\" равно \"([^\"]*)\"$")
    @А("^значение (?:поля|элемента|текста) \"([^\"]*)\" совпадает с текстом \"([^\"]*)\"$")
    @Если("^содержимое (?:поля|элемента|текста) \"([^\"]*)\" равно$")
    @Тогда("^содержимое (?:поля|элемента|текста) \"([^\"]*)\" совпадает с текстом$")
    @Когда("^значение (?:поля|элемента|текста) \"([^\"]*)\" равно$")
    @Пусть("^значение (?:поля|элемента|текста) \"([^\"]*)\" совпадает с текстом$")
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
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" (?:недоступна|недоступен) для нажатия$")
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
     * Проверка, что поле нередактируемо
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
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @И("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        int length = coreScenario.getCurrentPage().getAnyElementText(elementName).length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }


    /**
     * Получение текста элемента в блоке и сохранение его в переменную
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     */
    @И("^значение (?:элемента|поля) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveTextElementInBlock(String elementName, String blockName, String variableName) {
        CorePage block = coreScenario.getCurrentPage().getBlock(blockName);
        String elementText = block.getAnyElementText(elementName);
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка того, что значение из поля в блоке совпадает со значением заданной переменной из хранилища
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
