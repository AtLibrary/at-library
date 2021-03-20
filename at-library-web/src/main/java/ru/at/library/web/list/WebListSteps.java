package ru.at.library.web.list;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.web.step.result.core.IStepResult;
import ru.at.library.web.step.result.entities.CommonStepResult;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static java.lang.String.format;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.getRandom;

@Log4j2
public class WebListSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

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
     * Проверка появления списка на странице в течение Configuration.timeout.
     *
     * @param listName название элемента
     */
    @И("^список элементов \"([^\"]*)\" отображается на странице$")
    public IStepResult listIsPresentedOnPage(String listName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.first().shouldHave(visible);
        return new CommonStepResult(elements.first());
    }

    /**
     *
     */
    @И("список элементов \"([^\"]*)\" включает в себя список из таблицы$")
    public IStepResult checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        for (String expectedText : textTable) {
            elements.find(text(expectedText)).shouldHave(text(expectedText));
        }
        return new CommonStepResult(elements);
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     */
    @И("^список элементов \"([^\"]*)\" равен списку из таблицы$")
    public IStepResult checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
        return new CommonStepResult(elements);
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^нажатие на элемент с (?:текстом|значением) \"([^\"]*)\" в списке \"([^\"]*)\"$")
    public IStepResult checkIfSelectedListElementMatchesValue(String expectedValue, String listName) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = elements.find(exactText(expectedValue));
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    @И("^нажатие на элемент содержащий (?:текст|значение) \"([^\"]*)\" в списке \"([^\"]*)\"$")
    public IStepResult selectElementInListIfFoundByText(String expectedValue, String listName) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        CorePage currentPage = coreScenario.getCurrentPage();
        ElementsCollection elements = currentPage.getElementsList(listName);
        SelenideElement element = elements.find(text(expectedValue));
        element.click();
        return new CommonStepResult(element);
    }

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

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    @И("^нажатие на \"(\\d+)\" элемент в списке \"([^\"]*)\"$")
    public IStepResult selectElementNumberFromList(int number, String listName) {
        SelenideElement element = coreScenario.getCurrentPage().getElementsList(listName)
                .get(number - 1);
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     *
     * @return
     */
    @И("^нажатие на случайный элемент в списке \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromList(String listName) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage = listOfElementsFromPage.filter(visible);
        SelenideElement element = getRandomElementFromCollection(listOfElementsFromPage.filter(visible));
        element.click();
        log.trace("Выбран случайный элемент: " + element);
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @И("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromListAndSaveVar(String listName, String varName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = getRandomElementFromCollection(elements.filter(visible));
        String text = element.getText();
        coreScenario.setVar(varName, text);
        log.trace(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
        return new CommonStepResult(element);
    }

    /**
     * Проверка текста в элементе списка
     */
    @И("^текст в \"(\\d+)\" элементе списка \"([^\"]*)\" равен тексту \"([^\"]*)\"$")
    public IStepResult checkTextElementInListElement(int number, String listName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElementsList(listName)
                .get(number - 1).shouldHave(text(expectedValue));
        return new CommonStepResult(element);
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" содержит элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkListElementsContainsText(String listName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = elements.find(Condition.text(expectedValue)).shouldHave(text(expectedValue));
        return new CommonStepResult(element);
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" не содержит элемент с текстом \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
        elements.filter(Condition.text(expectedValue)).shouldHaveSize(0);
    }


    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список элементов \"([^\"]*)\" состоит из \"([^\"]*)\" элементов")
    public IStepResult listContainsNumberOfElements(String listName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName)
                .shouldHaveSize(numberOfElements);
        return new CommonStepResult(elements);
    }


    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @И("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public IStepResult listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeGreaterThan(quantity));
        } else
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeLessThan(quantity));
        return new CommonStepResult(listOfElementsFromPage);
    }

    public static SelenideElement getRandomElementFromCollection(ElementsCollection elementsCollection) {
        return elementsCollection.get(getRandom(elementsCollection.size())).shouldBe(visible);
    }
}
