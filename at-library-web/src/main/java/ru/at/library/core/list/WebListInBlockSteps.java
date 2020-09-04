package ru.at.library.core.list;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.ru.И;
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
    @И("список элементов {string} в блоке {string} отображается на странице")
    public void listIsPresentedOnPage(String listName, String blockName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.first().shouldHave(visible);
    }

    /**
     *
     */
    @И("список элементов {string} в блоке {string} включает в себя список из таблицы")
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
    @И("список элементов {string} в блоке {string} равен списку из таблицы")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable, String blockName) {
        textTable = getPropertyOrStringVariableOrValue(textTable);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("нажатие на элемент с текстом/значением {string} в списке {string} в блоке {string}")
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
    @И("нажатие на элемент содержащий текст/значение {string} в списке {string} в блоке {string}")
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
    @И("список элементов {string} в блоке {string} не отображается на странице")
    public void listIsNotPresentedOnPage(String listName, String blockName) {
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.first().shouldHave(not(visible));
    }

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    @И("нажатие на \"{int}\" элемент в списке {string} в блоке {string}")
    public void selectElementNumberFromList(int number, String listName, String blockName) {
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1).click();
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     *
     * @return
     */
    @И("нажатие на случайный элемент в списке {string} в блоке {string}")
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
    @И("выбран любой элемент из списка {string} и его значение сохранено в переменную {string} в блоке {string}")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName, String blockName) {
        String text = selectRandomElementFromList(listName, blockName).getText();
        coreScenario.setVar(varName, text);
        log.trace(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
    }

    /**
     * Проверка текста в элементе списка
     */
    @И("текст в \"{int}\" элементе списка {string} в блоке {string} равен тексту {string}")
    public void checkTextElementInListElement(int number, String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .get(number - 1).shouldHave(text(expectedValue));
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     */
    @И("список элементов {string} в блоке {string} содержит элемент с текстом {string}")
    public void checkListElementsContainsText(String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.find(Condition.text(expectedValue)).shouldHave(text(expectedValue));
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @И("список элементов {string} в блоке {string} не содержит элемент с текстом {string}")
    public void checkListElementsNotContainsText(String listName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        ElementsCollection elements = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        elements.filter(Condition.text(expectedValue)).shouldHaveSize(0);
    }


    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("список элементов {string} в блоке {string} состоит из {string} элементов")
    public void listContainsNumberOfElements(String listName, String blockName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName)
                .shouldHaveSize(numberOfElements);
    }


    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @И("в списке элементов {string} в блоке {string} содержится (более/менее) {int} элементов/элемента")
    public void listContainsMoreOrLessElements(String listName, String blockName, String moreOrLess, int quantity) {
        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeGreaterThan(quantity));
        } else
            listOfElementsFromPage.shouldHave(CollectionCondition.sizeLessThan(quantity));
    }

}
