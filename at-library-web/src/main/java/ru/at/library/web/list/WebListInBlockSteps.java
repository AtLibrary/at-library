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
import static ru.at.library.web.list.WebListSteps.getRandomElementFromCollection;

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
    public IStepResult listIsPresentedOnPage(String listName, String blockName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.first().shouldHave(visible);
        return new CommonStepResult(elements.first());
    }

    /**
     *
     */
    @И("список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" включает в себя список из таблицы$")
    public IStepResult checkIfListConsistsOfTableElements(String listName, List<String> textTable, String blockName) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
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
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" равен списку из таблицы$")
    public IStepResult checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable, String blockName) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
        return new CommonStepResult(elements);
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^нажатие на элемент с (?:текстом|значением) \"([^\"]*)\" в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult checkIfSelectedListElementMatchesValue(String expectedValue, String listName, String blockName) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        CorePage page = coreScenario.getCurrentPage().getBlock(blockName);
        ElementsCollection elements = page.getElementsList(listName);
        SelenideElement element = elements.find(exactText(expectedValue));
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    @И("^нажатие на элемент содержащий (?:текст|значение) \"([^\"]*)\" в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult selectElementInListIfFoundByText(String expectedValue, String listName, String blockName) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        CorePage page = coreScenario.getCurrentPage().getBlock(blockName);
        ElementsCollection elements = page.getElementsList(listName);
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
    public IStepResult selectElementNumberFromList(int number, String listName, String blockName) {
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1);
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     *
     * @return
     */
    @И("^нажатие на случайный элемент в списке \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromList(String listName, String blockName) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        SelenideElement element = getRandomElementFromCollection(listOfElementsFromPage.filter(visible));
        element.click();
        log.trace("Выбран случайный элемент: " + element);
        return new CommonStepResult(element);
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @И("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult selectRandomElementFromListAndSaveVar(String listName, String varName, String blockName) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        SelenideElement element = getRandomElementFromCollection(listOfElementsFromPage.filter(visible));
        String text = element.getText();
        coreScenario.setVar(varName, text);
        log.trace(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
        return new CommonStepResult(element);
    }

    /**
     * Проверка текста в элементе списка
     */
    @И("^текст в \"(\\d+)\" элементе списка \"([^\"]*)\" в блоке \"([^\"]*)\" равен тексту \"([^\"]*)\"$")
    public IStepResult checkTextElementInListElement(int number, String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1).shouldHave(text(expectedValue));
        return new CommonStepResult(element);
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    @И("^список элементов \"([^\"]*)\" в блоке \"([^\"]*)\" содержит элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkListElementsContainsText(String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        SelenideElement element = elements.find(Condition.text(expectedValue)).shouldHave(text(expectedValue));
        return new CommonStepResult(element);
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
    public IStepResult listContainsNumberOfElements(String listName, String blockName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.shouldHaveSize(numberOfElements);
        return new CommonStepResult(elements);
    }


    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @И("^в списке элементов \"([^\"]*)\" в блоке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public IStepResult listContainsMoreOrLessElements(String listName, String blockName, String moreOrLess, int quantity) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeGreaterThan(quantity));
        } else
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeLessThan(quantity));
        return new CommonStepResult(listOfElementsFromPage);
    }
}
