package ru.at.library.web.step.elementcollection;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.web.core.IStepResult;
import ru.at.library.web.entities.CommonStepResult;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getRandom;


/**
 * Проверки ElementsCollection
 */
@Log4j2
public class ElementsCollectionCheckSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * ######################################################################################################################
     */

    @И("^список элементов \"([^\"]*)\" отображается на странице$")
    public IStepResult listIsPresentedOnPage(String listName) {
        return listIsPresentedOnPage(coreScenario.getCurrentPage().getElementsList(listName));
    }

    @И("^в блоке \"([^\"]*)\" список элементов \"([^\"]*)\" отображается на странице$")
    public IStepResult listIsPresentedOnPage(String blockName, String listName) {
        return listIsPresentedOnPage(coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

    /**
     * Проверка отображения списка на странице
     */
    private IStepResult listIsPresentedOnPage(ElementsCollection elements) {
        elements.first().shouldHave(visible);
        return new CommonStepResult(elements.first());
    }

    /**
     * ######################################################################################################################
     */

    @И("^список элементов \"([^\"]*)\" не отображается на странице$")
    public IStepResult listIsNotVisibleOnPage(String listName) {
        return listIsNotVisibleOnPage(coreScenario.getCurrentPage().getElementsList(listName));
    }

    @И("^в блоке \"([^\"]*)\" список элементов \"([^\"]*)\" не отображается на странице$")
    public IStepResult listIsNotVisibleOnPage(String blockName, String listName) {
        return listIsNotVisibleOnPage(coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

    /**
     * Проверка не отображения списка на странице
     */
    private IStepResult listIsNotVisibleOnPage(ElementsCollection elements) {
        elements.first().shouldHave(not(visible));
        return new CommonStepResult(elements.first());
    }

    /**
     * ######################################################################################################################
     */

    @И("^список элементов \"([^\"]*)\" включает в себя список из таблицы$")
    public IStepResult checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        return checkIfListConsistsOfTableElements(
                coreScenario.getCurrentPage().getElementsList(listName),
                textTable);
    }

    @И("^в блоке \"([^\"]*)\" список элементов \"([^\"]*)\" включает в себя список из таблицы$")
    public IStepResult checkIfListConsistsOfTableElements(String blockName, String listName, List<String> textTable) {
        return checkIfListConsistsOfTableElements(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                textTable);
    }

    private IStepResult checkIfListConsistsOfTableElements(ElementsCollection elements, List<String> textTable) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        for (String expectedText : textTable) {
            elements.find(text(expectedText)).shouldHave(text(expectedText));
        }
        return new CommonStepResult(elements);
    }

    /**
     * ######################################################################################################################
     */

    @И("^список элементов \"([^\"]*)\" равен списку из таблицы$")
    public IStepResult checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        return checkIfListInnerTextConsistsOfTableElements(
                coreScenario.getCurrentPage().getElementsList(listName),
                textTable);
    }

    @И("^в блоке \"([^\"]*)\" список элементов \"([^\"]*)\" равен списку из таблицы$")
    public IStepResult checkIfListInnerTextConsistsOfTableElements(String blockName, String listName, List<String> textTable) {
        return checkIfListInnerTextConsistsOfTableElements(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                textTable);
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     */
    public IStepResult checkIfListInnerTextConsistsOfTableElements(ElementsCollection elements, List<String> textTable) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
        return new CommonStepResult(elements);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" текст любого из элементов сохранен в переменную \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromListAndSaveVar(String listName, String varName) {
        return selectRandomElementFromListAndSaveVar(
                coreScenario.getCurrentPage().getElementsList(listName),
                varName
        );
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" текст любого из элементов сохранен в переменную \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromListAndSaveVar(String blockName, String listName, String varName) {
        return selectRandomElementFromListAndSaveVar(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                varName
        );
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    public IStepResult selectRandomElementFromListAndSaveVar(ElementsCollection elements, String varName) {
        SelenideElement element = getRandomElementFromCollection(elements.filter(visible));
        String text = element.getText();
        coreScenario.setVar(varName, text);
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" текст в элементе \"(\\d+)\" равен \"([^\"]*)\"$")
    public IStepResult checkTextElementInListElement(String listName, int number, String expectedValue) {
        return checkTextElementInListElement(
                coreScenario.getCurrentPage().getElementsList(listName),
                number,
                expectedValue
        );
    }


    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" текст в элементе \"(\\d+)\" равен \"([^\"]*)\"$")
    public IStepResult checkTextElementInListElement(String blockName, String listName, int number, String expectedValue) {
        return checkTextElementInListElement(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                number,
                expectedValue
        );
    }

    /**
     * Проверка текста в элементе списка
     */
    public IStepResult checkTextElementInListElement(ElementsCollection elements, int number, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement selenideElement = elements.get(number - 1);
        SelenideElement element = selenideElement.shouldHave(text(expectedValue));
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списоке элементов \"([^\"]*)\" содержится элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkListElementsContainsText(String listName, String expectedValue) {
        return checkListElementsContainsText(
                coreScenario.getCurrentPage().getElementsList(listName),
                expectedValue);
    }

    @И("^в блоке \"([^\"]*)\" в списоке элементов \"([^\"]*)\" содержится элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkListElementsContainsText(String blockName, String listName, String expectedValue) {
        return checkListElementsContainsText(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                expectedValue);
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    public IStepResult checkListElementsContainsText(ElementsCollection elements, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = elements.find(Condition.exactText(expectedValue))
                .shouldHave(exactText(expectedValue));
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списоке элементов \"([^\"]*)\" не содержится элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        checkListElementsNotContainsText(
                coreScenario.getCurrentPage().getElementsList(listName),
                expectedValue);
    }

    @И("^в блоке \"([^\"]*)\" в списоке элементов \"([^\"]*)\" не содержится элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String blockName, String listName, String expectedValue) {
        checkListElementsNotContainsText(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                expectedValue);
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    public void checkListElementsNotContainsText(ElementsCollection elements, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        elements.filter(Condition.exactText(expectedValue)).shouldHaveSize(0);
    }

    /**
     * ######################################################################################################################
     */

    @И("^список элементов \"([^\"]*)\" состоит из \"([^\"]*)\" элементов")
    public IStepResult listContainsNumberOfElements(String listName, String quantity) {
        return listContainsNumberOfElements(
                coreScenario.getCurrentPage().getElementsList(listName),
                quantity);
    }

    @И("^в блоке \"([^\"]*)\" список элементов \"([^\"]*)\" состоит из \"([^\"]*)\" элементов")
    public IStepResult listContainsNumberOfElements(String blockName, String listName, String quantity) {
        return listContainsNumberOfElements(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                quantity);
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    public IStepResult listContainsNumberOfElements(ElementsCollection elements, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        elements.shouldHaveSize(numberOfElements);
        return new CommonStepResult(elements);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" содержится (более|менее) \"([^\"]*)\" (?:элементов|элемента)")
    public IStepResult listContainsMoreOrLessElements(String listName, String moreOrLess, String quantity) {
        return listContainsMoreOrLessElements(
                coreScenario.getCurrentPage().getElementsList(listName),
                moreOrLess,
                quantity
        );
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" содержится (более|менее) \"([^\"]*)\" (?:элементов|элемента)")
    public IStepResult listContainsMoreOrLessElements(String blockName, String listName, String moreOrLess, String quantity) {
        return listContainsMoreOrLessElements(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                moreOrLess,
                quantity
        );
    }

    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    public IStepResult listContainsMoreOrLessElements(ElementsCollection elements, String moreOrLess, String quantity) {
        int quantityOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        if ("более".equals(moreOrLess)) {
            elements.shouldHave(CollectionCondition.sizeGreaterThan(quantityOfElements));
        } else {
            elements.shouldHave(CollectionCondition.sizeLessThan(quantityOfElements));
        }
        return new CommonStepResult(elements);
    }

    /**
     * ######################################################################################################################
     */

    public static SelenideElement getRandomElementFromCollection(ElementsCollection elementsCollection) {
        return elementsCollection.get(getRandom(elementsCollection.size())).shouldBe(visible);
    }
}
