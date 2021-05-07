package ru.at.library.web.step.elementcollection;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.web.core.IStepResult;
import ru.at.library.web.entities.CommonStepResult;

import static com.codeborne.selenide.Condition.*;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.web.step.elementcollection.ElementsCollectionCheckSteps.getRandomElementFromCollection;

/**
 * Действия с ElementsCollection
 */
@Log4j2
public class ElementsCollectionActionSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" выполнено нажатие на элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkIfSelectedListElementMatchesValue(String listName, String expectedValue) {
        return checkIfSelectedListElementMatchesValue(
                coreScenario.getCurrentPage().getElementsList(listName),
                expectedValue
        );
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" выполнено нажатие на элемент с текстом \"([^\"]*)\"$")
    public IStepResult checkIfSelectedListElementMatchesValue(String blockName, String listName, String expectedValue) {
        return checkIfSelectedListElementMatchesValue(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                expectedValue
        );
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    private IStepResult checkIfSelectedListElementMatchesValue(ElementsCollection elements, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = elements.find(Condition.or(
                "Поиск элемента с тектом для дальнейшего нажатия",
                exactText(expectedValue),
                exactValue(expectedValue)
                )
        );

        element.click();
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" выполнено нажатие на элемент содержащий текст \"([^\"]*)\"$")
    public IStepResult selectElementInListIfFoundByText(String listName, String expectedValue) {
        return selectElementInListIfFoundByText(
                coreScenario.getCurrentPage().getElementsList(listName),
                expectedValue);
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" выполнено нажатие на элемент содержащий текст \"([^\"]*)\"$")
    public IStepResult selectElementInListIfFoundByText(String blockName, String listName, String expectedValue) {
        return selectElementInListIfFoundByText(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                expectedValue);
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    private IStepResult selectElementInListIfFoundByText(ElementsCollection elements, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = elements.find(Condition.or(
                "Поиск элемента содержащего текст для дальнейшего нажатия",
                text(expectedValue),
                value(expectedValue)
        ));
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" выполнено нажатие на \"(\\d+)\" элемент$")
    public IStepResult selectElementNumberFromList(String listName, int number) {
        return selectElementNumberFromList(
                coreScenario.getCurrentPage().getElementsList(listName),
                number);
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" выполнено нажатие на \"(\\d+)\" элемент$")
    public IStepResult selectElementNumberFromList(String blockName, String listName, int number) {
        return selectElementNumberFromList(
                coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName),
                number);
    }

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    public IStepResult selectElementNumberFromList(ElementsCollection elements, int number) {
        SelenideElement element = elements.get(number - 1);
        element.click();
        return new CommonStepResult(element);
    }

    /**
     * ######################################################################################################################
     */

    @И("^в списке элементов \"([^\"]*)\" выполнено нажатие на случайный элемент$")
    public IStepResult selectRandomElementFromList(String listName) {
        return selectRandomElementFromList(coreScenario.getCurrentPage().getElementsList(listName));
    }

    @И("^в блоке \"([^\"]*)\" в списке элементов \"([^\"]*)\" выполнено нажатие на случайный элемент$")
    public IStepResult selectRandomElementFromList(String blockName, String listName) {
        return selectRandomElementFromList(coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

    /**
     * Выполнено нажатие на случайный элемент
     */
    private IStepResult selectRandomElementFromList(ElementsCollection elements) {
        elements = elements.filter(visible);
        SelenideElement element = getRandomElementFromCollection(elements.filter(visible));
        element.click();
        log.trace("Выполнено нажатие на случайный элемент: " + element);
        return new CommonStepResult(element);
    }

}
