package ru.at.library.core.blocklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
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
import static ru.at.library.core.steps.OtherSteps.isTextMatches;

@Log4j2
public class BlockListSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();


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
            SelenideElement element = block.getElement(elementName);
            checkTextMatches(element, regExp);
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
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.hover();
        element.click();
    }


    @И("^элемент \"([^\"]*)\" отображается на странице в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void visibleElementBlockWhereTextEquals(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.shouldHave(Condition.visible);
    }

    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void checkTextInAnyBlockMatches(String elementNameClick, String regExp, String listName, String elementNameText, String expectedText, String blockName) {
        List<CorePage> blocksList = coreScenario.getCurrentPage().getBlock(blockName).getBlocksList(listName);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        checkTextMatches(element, regExp);
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
        throw new AssertionError("Текст: " + expectedText + " не найден в блоках!");
    }

    private void checkTextMatches(SelenideElement element, String regExp) {
        int timeSleep = 100;
        for (int i = 0; i < Configuration.timeout; i += timeSleep) {
            boolean textMatches = false;
            boolean valueMatches = false;
            boolean titleMatches = false;

            if (element.getText() != null) {
                textMatches = isTextMatches(element.getText(), regExp);
            }
            if (element.getValue() != null) {
                valueMatches = isTextMatches(element.getValue(), regExp);
            }
            if (element.getAttribute("title") != null) {
                titleMatches = isTextMatches(element.getAttribute("title"), regExp);
            }


            if (textMatches || valueMatches || titleMatches) {
                return;
            }

            Selenide.sleep(timeSleep);
        }

        takeScreenshot();

        element.waitUntil(Condition.matchText(regExp), 1);
    }


}
