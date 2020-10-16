package ru.at.library.web.blocklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.web.BrowserSteps;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.steps.OtherSteps.isTextMatches;

@Log4j2
public class BlockListSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Проверки списка блоков------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */


    @И("^список блоков \"([^\"]*)\" отображается на странице$")
    public void listBlockVisible(String listName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlocksList(listName);
        blocksList.get(0).ieAppeared();
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список блоков \"([^\"]*)\" состоит из (\\d+) блоков")
    public void listBlockSize(String listName, int quantity) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlocksList(listName);
        assertEquals(blocksList.size(), quantity);
    }

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public void checkTextInAnyBlock(String listName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlocksList(listName);
        findCorePageByTextInElement(blocksList, elementName, expectedText);
    }


    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст содержит \"([^\"]*)\"$")
    public void checkContainTextInAnyBlock(String listName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlocksList(listName);
        findCorePageByTextContainInElement(blocksList, elementName, expectedText);
    }


    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public void checkTextInAnyBlockMatches(String elementNameCheck, String regExp, String listName, String elementNameText, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        checkTextMatches(corePageByTextInElement, elementNameCheck, regExp);
    }



    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Проверки списка блоков в блоке------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @И("^список блоков \"([^\"]*)\" в блоке \"([^\"]*)\" состоит из (\\d+) блоков")
    public void listBlockSize(String listName, String blockName, int quantity) {
        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        assertEquals(blocksList.size(), quantity);
    }

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public void checkTextInAnyBlock(String listName, String blockName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        findCorePageByTextInElement(blocksList, elementName, expectedText);
    }


    @И("^в каждом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" в элементе \"([^\"]*)\" текст в формате \"([^\"]*)\"$")
    public void checkTextInAnyBlockMatches(String listName, String blockName, String elementName, String regExp) {
        regExp = getPropertyOrStringVariableOrValue(regExp);
        List<CorePage> blocksList =
                coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);

        for (CorePage block : blocksList) {
            checkTextMatches(block, elementName, regExp);
        }
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" элемент \"([^\"]*)\" отображается на странице$")
    public void elementVisibleAnyBlock(String listName, String blockName, String elementName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);

        for (CorePage block : blocksList) {
            block.getElement(elementName).shouldHave(Condition.visible);
        }
    }

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickButtonInBlock(String elementName, String listName, String blockName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        blocksList.get(0).getElement(elementName)
                .hover()
                .click();
    }

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickButtonInBlockWhereTextEquals(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.hover();
        element.click();
    }

    @И("^элемент \"([^\"]*)\" отображается на странице в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void visibleElementBlockWhereTextEquals(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.shouldHave(Condition.visible);
    }

    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void checkTextInAnyBlockMatches(String elementNameCheck, String regExp, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        checkTextMatches(corePageByTextInElement, elementNameCheck, regExp);
    }





    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Дополнительные методы---------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */


    @Step("Поиск блока в котором текст элемента '{elementName}' равен : '{expectedText}'")
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
        BrowserSteps.takeScreenshot();
        //TODO добавить имя блок и имя элемента
        throw new AssertionError(
                "Во всех блоках в элементах " + elementName + " не найден текст:" + expectedText
                        + "\nРазмер блоков: " + blocksList.size()
                        + "\nСодержимое блоков: " + blocksList);
    }

    @Step("Поиск блока в котором текст элемента '{elementName}' содержит : '{expectedText}'")
    private CorePage findCorePageByTextContainInElement(List<CorePage> blocksList, String elementName, String expectedText) {
        for (CorePage page : blocksList) {
            SelenideElement element = page.getElement(elementName);

            Selenide.executeJavaScript("arguments[0].scrollIntoView({block: \"center\", inline: \"center\"});", element);

            boolean expectedTextFind = element.is(
                    Condition.or("проверка на текст",
                            Condition.text(expectedText),
                            Condition.value(expectedText),
                            Condition.attribute("title", expectedText)
                    ));

            if (expectedTextFind) {
                return page;
            }
        }
        BrowserSteps.takeScreenshot();
        //TODO добавить имя блок и имя элемента
        throw new AssertionError(
                "Во всех блоках в элементах " + elementName + " не найден текст:" + expectedText
                        + "\nРазмер блоков: " + blocksList.size()
                        + "\nСодержимое блоков: " + blocksList);
    }



    @Step("Проверка что текст элемента '{elementName}' равен: '{expectedText}'")
    private void checkText(CorePage block, String elementName, String expectedText) {
        SelenideElement element = block.getElement(elementName);

        element.shouldHave(Condition.or("проверка на текст",
                Condition.exactText(expectedText),
                Condition.exactValue(expectedText),
                Condition.attribute("title", expectedText)
        ));
    }


    @Step("Проверка что TextMatches элемента '{elementName}' : '{regExp}'")
    private void checkTextMatches(CorePage block, String elementName, String regExp) {
        SelenideElement element = block.getElement(elementName);

        boolean textMatches = element.is(Condition.matchText(regExp));
        boolean valueMatches = false;
        boolean titleMatches = false;

        if (element.getValue() != null) {
            valueMatches = isTextMatches(element.getValue(), regExp);
        }
        if (element.getAttribute("title") != null) {
            titleMatches = isTextMatches(element.getAttribute("title"), regExp);
        }

        if (textMatches || valueMatches || titleMatches) {
            return;
        }

        element.waitUntil(Condition.matchText(regExp), 1);
    }


}
