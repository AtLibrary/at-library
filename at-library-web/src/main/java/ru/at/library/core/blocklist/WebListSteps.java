package ru.at.library.core.blocklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.at.library.core.BrowserSteps.takeScreenshot;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;

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
    @И("^список блоков \"([^\"]*)\" отображается на странице$")
    public void aaa(String listName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlocksList(listName);
        blocksList.get(0).ieAppeared();
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список блоков \"([^\"]*)\" состоит из (\\d+) блоков")
    public void aaaa(String listName, int quantity) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlocksList(listName);
        assertEquals(blocksList.size(), quantity);
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список блоков \"([^\"]*)\" в блоке \"([^\"]*)\" состоит из (\\d+) блоков")
    public void aaaaa(String listName, String blockName, int quantity) {
        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        assertEquals(blocksList.size(), quantity);
    }


    @И("^в любом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public void aaaaaa(String listName, String blockName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        findCorePageByTextInElement(blocksList, elementName, expectedText);
    }

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void aaaaaaa(String elementName, String listName, String blockName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        blocksList.get(0).getElement(elementName).click();
    }

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void aaaaaaaa(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.hover();
        element.click();
    }


    private CorePage findCorePageByTextInElement(List<CorePage> blocksList, String elementName, String expectedText) {
        for (CorePage page : blocksList) {
            SelenideElement element = page.getElement(elementName);

            Selenide.executeJavaScript("arguments[0].scrollIntoView({block: \"center\", inline: \"center\"});", element);

            boolean expectedTextFind = element.is(
                    Condition.or("проверка на текст",
                            Condition.exactText(expectedText),
                            Condition.exactValue(expectedText),
                            Condition.attribute("title", expectedText)
                    ));

            if (expectedTextFind) {
                return page;
            }
        }
        takeScreenshot();
        //TODO добавить имя блок и имя элемента
        throw new AssertionError("Текс: " + expectedText + " не найден в блоках!");

    }
//    /**
//     * Производится сопоставление числа элементов списка и значения, указанного в шаге
//     */
//    @И("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
//    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
//        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
//        if ("более".equals(moreOrLess)) {
//            listOfElementsFromPage.shouldHave(CollectionCondition.sizeGreaterThan(quantity));
//        } else
//            listOfElementsFromPage.shouldHave(CollectionCondition.sizeLessThan(quantity));
//    }

//    /**
//     *
//     */
//    @И("список элементов \"([^\"]*)\" включает в себя список из таблицы$")
//    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
//        textTable = getPropertyOrStringVariableOrValue(textTable);
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        for (String expectedText : textTable) {
//            elements.find(text(expectedText)).shouldHave(text(expectedText));
//        }
//    }

//    /**
//     * Проверка, что список со страницы состоит только из элементов,
//     * перечисленных в таблице
//     * Для получения текста из элементов списка используется метод innerText()
//     */
//    @И("^список элементов \"([^\"]*)\" равен списку из таблицы$")
//    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
//        textTable = getPropertyOrStringVariableOrValue(textTable);
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        elements.shouldHave(CollectionCondition.textsInAnyOrder(textTable));
//    }

//    /**
//     * Выбор из списка со страницы элемента с заданным значением
//     * (в приоритете: из property, из переменной сценария, значение аргумента)
//     */
//    @И("^нажатие на элемент с (?:текстом|значением) \"([^\"]*)\" в списке \"([^\"]*)\"$")
//    public void checkIfSelectedListElementMatchesValue(String expectedValue, String listName) {
//        final String value = getPropertyOrStringVariableOrValue(expectedValue);
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        elements.find(exactText(value)).click();
//    }

//    /**
//     * Выбор из списка со страницы элемента, который содержит заданный текст
//     * (в приоритете: из property, из переменной сценария, значение аргумента)
//     * Не чувствителен к регистру
//     */
//    @И("^нажатие на элемент содержащий (?:текст|значение) \"([^\"]*)\" в списке \"([^\"]*)\"$")
//    public void selectElementInListIfFoundByText(String expectedValue, String listName) {
//        final String value = getPropertyOrStringVariableOrValue(expectedValue);
//        CorePage currentPage = coreScenario.getCurrentPage();
//        ElementsCollection elements = currentPage.getElementsList(listName);
//        elements.find(text(value)).click();
//    }

//    /**
//     * /**
//     * Проверка не появления списка на странице в течение Configuration.timeout.
//     *
//     * @param listName название элемента
//     */
//    @И("^список элементов \"([^\"]*)\" не отображается на странице$")
//    public void listIsNotPresentedOnPage(String listName) {
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        elements.first().shouldHave(not(visible));
//    }

//    /**
//     * Выбор n-го элемента из списка со страницы
//     * Нумерация элементов начинается с 1
//     */
//    @И("^нажатие на \"(\\d+)\" элемент в списке \"([^\"]*)\"$")
//    public void selectElementNumberFromList(int number, String listName) {
//        coreScenario.getCurrentPage().getElementsList(listName)
//                .get(number - 1).click();
//    }

//    /**
//     * Выбор из списка со страницы любого случайного элемента
//     *
//     * @return
//     */
//    @И("^нажатие на случайный элемент в списке \"([^\"]*)\"$")
//    public SelenideElement selectRandomElementFromList(String listName) {
//        ElementsCollection listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
//        listOfElementsFromPage = listOfElementsFromPage.filter(visible);
//        SelenideElement selenideElement = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size())).shouldBe(visible);
//        selenideElement.click();
//        log.trace("Выбран случайный элемент: " + selenideElement);
//        return selenideElement;
//    }

//    /**
//     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
//     */
//    @И("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
//    public void selectRandomElementFromListAndSaveVar(String listName, String varName) {
//        String text = selectRandomElementFromList(listName).getText();
//        coreScenario.setVar(varName, text);
//        log.trace(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
//                coreScenario.getVar(varName), listName));
//    }

//    /**
//     * Проверка текста в элементе списка
//     */
//    @И("^текст в \"(\\d+)\" элементе списка \"([^\"]*)\" равен тексту \"([^\"]*)\"$")
//    public void checkTextElementInListElement(int number, String listName, String expectedValue) {
//        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
//        coreScenario.getCurrentPage().getElementsList(listName)
//                .get(number - 1).shouldHave(text(expectedValue));
//    }

//    /**
//     * Проверка, что каждый элемент списка содержит ожидаемый текст
//     */
//    @И("^список элементов \"([^\"]*)\" содержит элемент с текстом \"([^\"]*)\"$")
//    public void checkListElementsContainsText(String listName, String expectedValue) {
//        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        elements.find(Condition.text(expectedValue)).shouldHave(text(expectedValue));
//    }

//    /**
//     * Проверка, что каждый элемент списка не содержит ожидаемый текст
//     */
//    @И("^список элементов \"([^\"]*)\" не содержит элемент с текстом \"([^\"]*)\"$")
//    public void checkListElementsNotContainsText(String listName, String expectedValue) {
//        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
//        ElementsCollection elements = coreScenario.getCurrentPage().getElementsList(listName);
//        elements.filter(Condition.text(expectedValue)).shouldHaveSize(0);
//    }


}
