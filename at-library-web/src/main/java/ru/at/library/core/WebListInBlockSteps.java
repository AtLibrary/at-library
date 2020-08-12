package ru.at.library.core;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static java.lang.String.format;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getRandom;

@Log4j2
public class WebListInBlockSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();


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
     * Проверка появления списка на странице в течение Configuration.timeout.
     *
     * @param listName название элемента
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String listName, String blockName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.first().shouldHave(visible);
    }

    /**
     *
     */
    @И("список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" включает в себя список из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable, String blockName) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        for (String expectedText : textTable) {
            elements.find(text(expectedText)).shouldHave(text(expectedText));
        }
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" равен списку из таблицы$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable, String blockName) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^нажатие на элемент с (?:текстом|значением) \"([^\"]*)\" в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void checkIfSelectedListElementMatchesValue(String expectedValue, String listName, String blockName) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        CorePage page = coreScenario.getCurrentPage().getBlock(blockName);
        ElementsCollection elements = page.getElementsList(listName);
        elements.find(exactText(value)).click();
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    @И("^нажатие на элемент содержащий (?:текст|значение) \"([^\"]*)\" в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String expectedValue, String listName, String blockName) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        CorePage page = coreScenario.getCurrentPage().getBlock(blockName);
        ElementsCollection elements = page.getElementsList(listName);
        elements.find(text(value)).click();
    }

    /**
     * /**
     * Проверка не появления списка на странице в течение Configuration.timeout.
     *
     * @param listName название элемента
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" не отображается на странице$")
    public void listIsNotPresentedOnPage(String listName, String blockName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.first().shouldHave(not(visible));
    }

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    @И("^нажатие на \"(\\d+)\" элемент в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void selectElementNumberFromList(int number, String listName, String blockName) {
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1).click();
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     *
     * @return
     */
    @И("^нажатие на случайный элемент в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public SelenideElement selectRandomElementFromList(String listName, String blockName) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        listOfElementsFromPage = listOfElementsFromPage.filter(visible);
        SelenideElement selenideElement = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size())).shouldBe(visible);
        selenideElement.click();
        log.trace("Выбран случайный элемент: " + selenideElement);
        return selenideElement;
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @И("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName, String blockName) {
        String text = selectRandomElementFromList(listName, blockName).getText();
        coreScenario.setVar(varName, text);
        log.trace(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
    }

    /**
     * Проверка текста в элементе списка
     */
    @И("^текст в \"(\\d+)\" элементе списка \"([^\"]*)\" в блоке \"([^\"]*)\" равен тексту \"([^\"]*)\"$")
    public void checkTextElementInListElement(int number, String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1).shouldHave(text(expectedValue));
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" содержит элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.find(Condition.text(expectedValue)).shouldHave(text(expectedValue));
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" не содержит элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.filter(Condition.text(expectedValue)).shouldHaveSize(0);
    }


    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" состоит из \"([^\"]*)\" элементов")
    public void listContainsNumberOfElements(String listName, String blockName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .shouldHaveSize(numberOfElements);
    }


    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @И("^в списке элементов \"([^\"]*)\" в блоке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public void listContainsMoreOrLessElements(String listName, String blockName, String moreOrLess, int quantity) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeGreaterThan(quantity));
        } else
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeLessThan(quantity));
    }

}
