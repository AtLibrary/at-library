package ru.at.library.core;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static java.lang.String.format;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getRandom;

public class WebListSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

//    /**
//     * Проверка того, что значение из поля содержится в списке,
//     * полученном из хранилища переменных по заданному ключу
//     *
//     * @param variableListName имя переменной
//     * @param elementName      имя :поля|элемента
//     */
//    @SuppressWarnings("unchecked")
//    @И("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\"$")
//    public void checkIfListContainsValueFromField(String variableListName, String elementName) {
//        String actualValue = coreScenario.getCurrentPage().getAnyElementText(elementName);
//        List<String> listFromVariable = ((List<String>) coreScenario.getVar(variableListName));
//        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", variableListName, elementName),
//                listFromVariable.contains(actualValue));
//    }

    /**
     * Проверка появления списка на странице в течение Configuration.timeout.
     *
     * @param listName название элемента
     */
    @И("^список элементов \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String listName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.first().shouldHave(visible);
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод getText()
     */
    @И("список элементов \"([^\"]*)\" включает в себя список из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        //TODO добавить getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.shouldHave(CollectionCondition.texts(textTable));
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     */
    @И("^список элементов \"([^\"]*)\" равен списку из таблицы$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        //TODO добавить getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.shouldHave(CollectionCondition.exactTexts(textTable));
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^нажатие на первый элемент с (?:текстом|значением) \"(.*)\" в списке \"([^\"]*)\"$")
    public void checkIfSelectedListElementMatchesValue(String expectedValue, String listName) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.find(exactText(value)).click();
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    @И("^нажатие на первый элемент содержащий (?:текстом|значением) \"(.*)\" в списке \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.find(text(value)).click();
    }

//    /**
//     * Проверка, что список со страницы совпадает со списком из переменной
//     * без учёта порядка элементов
//     * Для получения текста из элементов списка используется метод innerText()
//     */
//    @SuppressWarnings("unchecked")
//    @И("^список \"([^\"]*)\" на странице совпадает со списком \"([^\"]*)\"$")
//    public void checkListInnerTextCorrespondsToListFromVariable(String listName, String listVariable) {
//        List<String> expectedList = new ArrayList<>((List<String>) coreScenario.getVar(listVariable));
//        List<String> actualList = new ArrayList<>(coreScenario.getCurrentPage().getAnyElementsListInnerTexts(listName));
//        assertThat(String.format("Количество элементов списка %s = %s, ожидаемое значение = %s", listName, actualList.size(), expectedList.size()), actualList,
//                hasSize(expectedList.size()));
//        assertThat(String.format("Список со страницы %s: %s не совпадает с ожидаемым списком из переменной %s:%s", listName, actualList, listVariable, expectedList)
//                , actualList, containsInAnyOrder(expectedList.toArray()));
//    }

    /**
     * /**
     * Проверка не появления списка на странице в течение Configuration.timeout.
     *
     * @param listName название элемента
     */
    @И("^список элементов \"([^\"]*)\" не отображается на странице$")
    public void listIsNotPresentedOnPage(String listName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.first().shouldHave(not(visible));
    }

//
//    /**
//     * Проверка, что список со страницы совпадает со списком из переменной
//     * без учёта порядка элементов
//     */
//    @SuppressWarnings("unchecked")
//    @И("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
//    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
//        HashSet<String> expectedList = new HashSet<>((List<String>) coreScenario.getVar(listVariable));
//        HashSet<String> actualList = new HashSet<>(coreScenario.getCurrentPage().getAnyElementsListTexts(listName));
//        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), actualList, equalTo(expectedList));
//    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     *
     * @return
     */
    @И("^нажатие на случайный элемент в списке \"([^\"]*)\"$")
    public SelenideElement selectRandomElementFromList(String listName) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement selenideElement = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size())).shouldBe(visible);
        selenideElement.click();
        coreScenario.write("Выбран случайный элемент: " + selenideElement);
        return selenideElement;
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @И("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName) {
        String text = selectRandomElementFromList(listName).getText();
        coreScenario.setVar(varName, text);
        coreScenario.write(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
    }

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    @И("^нажатие на \"([^\"]*)\"-й элемент в списке \"([^\"]*)\"$")
    public void selectElementNumberFromList(String number, String listName) {
        coreScenario.getCurrentPage().getElementsList(listName)
                .get(Integer.parseInt(number)).click();
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" содержит элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.shouldHave(CollectionCondition.exactTexts(value));
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" не содержит элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.shouldHave(CollectionCondition.exactTexts(value));
        //TODO исправить при первом падении
    }

//    /**
//     * Проход по списку и проверка текста у элемента на соответствие формату регулярного выражения
//     */
//    @И("элементы списка \"([^\"]*)\" соответствуют формату \"([^\"]*)\"$")
//    public void checkListTextsByRegExp(String listName, String pattern) {
//        coreScenario.getCurrentPage().getElementsList(listName).forEach(element -> {
//            String str = coreScenario.getCurrentPage().getAnyElementText(element);
//            assertTrue(format("Текст '%s' из списка '%s' не соответствует формату регулярного выражения", str, listName),
//                    isTextMatches(str, pattern));
//        });
//    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список элементов \"([^\"]*)\" состоит из \"([^\"]*)\" элементов")
    public void listContainsNumberOfElements(String listName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        coreScenario.getCurrentPage().getElementsList(listName)
                .shouldHaveSize(numberOfElements);
    }

//
//    /**
//     * Производится сопоставление числа элементов списка и значения, указанного в шаге
//     */
//    @И("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
//    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
//        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
//        if ("более".equals(moreOrLess)) {
//            assertTrue(String.format("Число элементов списка меньше ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() > quantity);
//        } else
//            assertTrue(String.format("Число элементов списка превышает ожидаемое: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() < quantity);
//    }

//    /**
//     * @param blockName имя блока
//     * @param listName
//     * @param varName
//     */
//    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен в переменную \"([^\"]*)\"$")
//    public void getElementsList(String blockName, String listName, String varName) {
//        coreScenario.setVar(varName, coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
//    }
//
//    /**
//     * @param blockName имя блока
//     * @param listName
//     * @param varName
//     */
//    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен текст в переменную \"([^\"]*)\"$")
//    public void getListElementsText(String blockName, String listName, String varName) {
//        coreScenario.setVar(varName,
//                coreScenario.getCurrentPage()
//                        .getBlock(blockName)
//                        .getElementsList(listName)
//                        .stream()
//                        .map(SelenideElement::getText)
//                        .collect(Collectors.toList()));
//    }
}
