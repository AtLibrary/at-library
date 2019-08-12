package ru.bcs.at.library.core.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.not;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.getPropertyOrValue;
import static ru.bcs.at.library.core.steps.OtherSteps.*;
import static ru.bcs.at.library.core.steps.WebTestConfig.DEFAULT_TIMEOUT;

public class WebListSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля содержится в списке,
     * полученном из хранилища переменных по заданному ключу
     * </p>
     *
     * @param variableListName имя переменной
     * @param elementName      имя :поля|элемента
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\"$")
    public void checkIfListContainsValueFromField(String variableListName, String elementName) {
        String actualValue = coreScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) coreScenario.getVar(variableListName));
        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", variableListName, elementName),
                listFromVariable.contains(actualValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления списка на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название элемента
     *                    </p>
     */
    @Тогда("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElementsList(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод getText()
     * </p>
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = coreScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     * </p>
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов таблицы$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = coreScenario.getCurrentPage().getAnyElementsListInnerTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке %s: %s не совпадают с ожидаемыми значениями из таблицы %s", listName, actualValues, textTable),
                actualValues.containsAll(textTable));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim())
                .collect(toList());
        listOfElementsFromPage.stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsText)))
                .click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        listOfElementsFromPage.stream()
                .filter(element -> element.getText().trim().toLowerCase().contains(value.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsListText)))
                .click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * Для получения текста из элементов списка используется метод innerText()
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" на странице совпадает со списком \"([^\"]*)\"$")
    public void checkListInnerTextCorrespondsToListFromVariable(String listName, String listVariable) {
        List<String> expectedList = new ArrayList<>((List<String>) coreScenario.getVar(listVariable));
        List<String> actualList = new ArrayList<>(coreScenario.getCurrentPage().getAnyElementsListInnerTexts(listName));
        assertThat(String.format("Количество элементов списка %s = %s, ожидаемое значение = %s", listName, actualList.size(), expectedList.size()), actualList,
                hasSize(expectedList.size()));
        assertThat(String.format("Список со страницы %s: %s не совпадает с ожидаемым списком из переменной %s:%s", listName, actualList, listVariable, expectedList)
                , actualList, containsInAnyOrder(expectedList.toArray()));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что элемент не отображается на странице
     * </p>
     */
    @Тогда("^(?:поле|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) coreScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(coreScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), actualList, equalTo(expectedList));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы любого случайного элемента
     * </p>
     */
    @Тогда("^выбран любой элемент в списке \"([^\"]*)\"$")
    public void selectRandomElementFromList(String listName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()))
                .shouldBe(Condition.visible).click();
        coreScenario.write("Выбран случайный элемент: " + listOfElementsFromPage);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     * </p>
     */
    @Когда("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()));
        element.shouldBe(Condition.visible).click();
        coreScenario.setVar(varName, coreScenario.getCurrentPage().getAnyElementText(element).trim());
        coreScenario.write(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     * </p>
     */
    @Тогда("^выбран (\\d+)-й элемент в списке \"([^\"]*)\"$")
    public void selectElementNumberFromList(Integer elementNumber, String listName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement elementToSelect;
        Integer selectedElementNumber = elementNumber - 1;
        if (selectedElementNumber < 0 || selectedElementNumber >= listOfElementsFromPage.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("В списке %s нет элемента с номером %s. Количество элементов списка = %s",
                            listName, elementNumber, listOfElementsFromPage.size()));
        }
        elementToSelect = listOfElementsFromPage.get(selectedElementNumber);
        elementToSelect.shouldBe(Condition.visible).click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     * Не чувствителен к регистру
     * </p>
     */
    @Тогда("^элементы списка \"([^\"]*)\" содержат текст \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertTrue(String.format("Элемены списка %s: [%s] не содержат текст [%s] ", listName, elementsListText, value),
                elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     * </p>
     */
    @Тогда("^элементы списка \"([^\"]*)\" не содержат текст \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertFalse(String.format("Элемены списка %s: [%s] содержат текст [%s] ", listName, elementsListText, value),
                elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проход по списку и проверка текста у элемента на соответствие формату регулярного выражения
     * </p>
     */
    @И("элементы списка \"([^\"]*)\" соответствуют формату \"([^\"]*)\"$")
    public void checkListTextsByRegExp(String listName, String pattern) {
        coreScenario.getCurrentPage().getElementsList(listName).forEach(element -> {
            String str = coreScenario.getCurrentPage().getAnyElementText(element);
            assertTrue(format("Текст '%s' из списка '%s' не соответствует формату регулярного выражения", str, listName),
                    isTextMatches(str, pattern));
        });
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (\\d+) (?:элемент|элементов|элемента)")
    public void listContainsNumberOfElements(String listName, int quantity) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        assertTrue(String.format("Число элементов в списке отличается от ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() == quantity);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка соответствия числа элементов списка значению из property файла, из переменной сценария или указанному в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится количество элементов, равное значению из переменной \"([^\"]*)\"")
    public void listContainsNumberFromVariable(String listName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        listContainsNumberOfElements(listName, numberOfElements);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            assertTrue(String.format("Число элементов списка меньше ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() > quantity);
        } else
            assertTrue(String.format("Число элементов списка превышает ожидаемое: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() < quantity);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * </p>
     *
     * @param blockName имя блока
     * @param listName
     * @param varName
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен в переменную \"([^\"]*)\"$")
    public void getElementsList(String blockName, String listName, String varName) {
        coreScenario.setVar(varName, coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * </p>
     *
     * @param blockName имя блока
     * @param listName
     * @param varName
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен текст в переменную \"([^\"]*)\"$")
    public void getListElementsText(String blockName, String listName, String varName) {
        coreScenario.setVar(varName,
                coreScenario.getCurrentPage()
                        .getBlock(blockName)
                        .getElementsList(listName)
                        .stream()
                        .map(SelenideElement::getText)
                        .collect(Collectors.toList()));
    }
}
