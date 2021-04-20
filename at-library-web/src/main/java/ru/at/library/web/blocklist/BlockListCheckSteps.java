package ru.at.library.web.blocklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.core.steps.OtherSteps;
import ru.at.library.web.step.result.core.IStepResult;
import ru.at.library.web.step.result.entities.BlockListStepResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.web.blocklist.OtherMethodBlockCorePageList.*;

@Log4j2
public class BlockListCheckSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Проверки списка блоков------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    @И("^список блоков \"([^\"]*)\" отображается на странице$")
    public IStepResult listBlockVisible(String listName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage corePage : blocksList) {
            corePage.ieAppeared();
        }

        return new BlockListStepResult(blocksList);
    }

    @И("^количество блоков в списке блоков \"([^\"]*)\" (равно|не равно|больше|меньше|больше или равно|меньше или равно) (\\d+)$")
    public IStepResult checkBlockListSize(String listName, String condition, int expectedCountBlock) {
        CustomCondition.Comparison comparison = CustomCondition.Comparison.valueOf(condition);

        List<CorePage> blocksList = getBlockListWithCheckingTheQuantity(
                listName, comparison, expectedCountBlock);


        return new BlockListStepResult(blocksList);
    }

    /**
     * -----------------------------------------------В КАЖДОМ------------------------------------------------
     */

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" отображается на странице$")
    public IStepResult elementVisibleInBlockList(String listName, String elementName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            block.getElement(elementName).shouldHave(Condition.visible);
        }
        return new BlockListStepResult(blocksList, elementName);
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" является изображением и отображается на странице$")
    public void checkImageInBlockList(String listName, String elementName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            SelenideElement element = block.getElement(elementName);
            element
                    .shouldHave(Condition.image)
                    .shouldHave(Condition.visible);
        }
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" содержит текст в формате \"([^\"]*)\"$")
    public IStepResult checkTextInBlockListMatches(String listName, String elementName, String regExp) {
        regExp = OtherSteps.getPropertyOrStringVariableOrValue(regExp);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            shouldHaveTextMatches(block, elementName, regExp);
        }
        return new BlockListStepResult(blocksList, elementName);
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public IStepResult checkCssInBlockList(String listName, String elementName, String cssName, String cssValue) {
        cssValue = OtherSteps.getPropertyOrStringVariableOrValue(cssValue);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            methodCheckHasCssInBlockList(block, elementName, cssName, cssValue);
        }

        return new BlockListStepResult(this.coreScenario.getCurrentPage().getBlocksList(listName), elementName);
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" не содержит css \"([^\"]*)\" со значением \"([^\"]*)\"$")
    public IStepResult checkHasNotCssInBlockList(String listName, String elementName, String cssName, String cssValue) {
        cssValue = OtherSteps.getPropertyOrStringVariableOrValue(cssValue);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            methodCheckNotHasCssInBlockList(block, elementName, cssName, cssValue);
        }

        return new BlockListStepResult(this.coreScenario.getCurrentPage().getBlocksList(listName), elementName);
    }

    @И("^список блоков \"([^\"]*)\" соответствует списку$")
    @И("^в каждом блоке в списке блоков \"([^\"]*)\" элементы соответствуют списку$")
    public IStepResult everyBlockInBlockListMatchesComplexCondition(String blockListName, DataTable conditionsTable) {
        List<CorePage> blockList = this.coreScenario.getCurrentPage().getBlocksList(blockListName);
        List<List<String>> conditionsRows = conditionsTable.asLists();
        int counter = 0;
        for (CorePage block : blockList) {
            counter++;
            for (List<String> conditionsRow : conditionsRows) {
                String elementName = conditionsRow.get(0);
                String textCondition = conditionsRow.get(1);
                String expectedText = getPropertyOrStringVariableOrValue(conditionsRow.get(2));

                if (counter == 0) {
                    block.getElement(elementName).shouldHave(getSelenideCondition(textCondition, expectedText));
                } else {
                    block.getElement(elementName).shouldHave(getSelenideCondition(textCondition, expectedText), Duration.ZERO);
                }
            }
        }
        return new BlockListStepResult(blockList, conditionsRows.stream().map(conditionsRow -> conditionsRow.get(0)).collect(Collectors.toList()));
    }

    /**
     * -----------------------------------------------В КАКОМ-ТО ОДНОМ ИЛИ НЕСКОЛЬКИХ------------------------------------------------
     */

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlock(String listName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePage = findCorePageByTextInElement(blocksList, elementName, expectedText);
        return new BlockListStepResult(corePage, elementName);
    }

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст соответствует регулярному выражению \"([^\"]*)\"$")
    public IStepResult checkByRegExpInElementInAnyBlock(String listName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePage = findCorePageByRegExpInElement(blocksList, elementName, expectedText);
        return new BlockListStepResult(corePage, elementName);
    }

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст содержит$")
    @И("^в любом блоке в списке блоков \"([^\"]*)\" в элементе \"([^\"]*)\" текст содержит \"([^\"]*)\"$")
    public IStepResult checkContainTextInAnyBlock(String listName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePage = findCorePageByTextContainInElement(blocksList, elementName, expectedText);
        return new BlockListStepResult(corePage, elementName);
    }

    @И("^в (\\d+) блоках в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" содержит текст в формате \"([^\"]*)\"$")
    public IStepResult checkTextInBlockListMatches(int blockNumber, String blockListName, String elementName, String regExp) {
        regExp = getPropertyOrStringVariableOrValue(regExp);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        List<CorePage> filterListBlock = new ArrayList<>();

        for (CorePage block : blocksList) {
            if (checkTextMatches(block, elementName, regExp)) {
                filterListBlock.add(block);
            }
        }

        Assert.assertEquals(filterListBlock.size(), blockNumber,
                "Условия поиска: " +
                        "\nЭлемент '" + elementName + "' содержит текст в формате : '" + regExp + "'" +
                        "\nОжидаемое количество блоков: " + blockNumber +
                        "\nАктуальное количество блоков: " + filterListBlock.size()
        );

        return new BlockListStepResult(filterListBlock, elementName);
    }

    @И("^в (\\d+) блоках в списке блоков \"([^\"]*)\" элемент \"([^\"]*)\" отображается на странице$")
    public IStepResult elementVisibleInBlockList(int blockNumber, String blockListName, String elementName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        List<CorePage> filterListBlock = new ArrayList<>();

        for (CorePage block : blocksList) {
            if (block.getElement(elementName).is(Condition.visible)) {
                filterListBlock.add(block);
            }
        }

        Assert.assertEquals(filterListBlock.size(), blockNumber,
                "Условия поиска: " +
                        "\nЭлемент '" + elementName + "' отображается в блоке" +
                        "\nОжидаемое количество блоков: " + blockNumber +
                        "\nАктуальное количество блоков: " + filterListBlock.size()
        );

        return new BlockListStepResult(filterListBlock, elementName);
    }

    /**
     * Метод проверяет что в списке блоков есть блок, текст элемента(ов) которого соответствует условию conditionsTable
     *
     * @param blockListName   Название списка блоков
     * @param conditionsTable Список проверяемых условий в блоке
     *                        пример:
     *                        |<Название элемента 1>|(текст равен|текст содержит|текст в формате|отображается на странице|не отображается на странице|не существует на странице|изображение загрузилось)|<Имя переменной/Имя свойства/Ожидаемый текст/Регулярное выражение>|
     *                        ...
     *                        |<Название элемента N>|(текст равен|текст содержит|текст в формате|отображается на странице|не отображается на странице|не существует на странице|изображение загрузилось)|<Имя переменной/Имя свойства/Ожидаемый текст/Регулярное выражение>|
     */
    @И("^в списке блоков \"([^\"]*)\" есть блок элементы которого соответствуют списку$")
    public IStepResult checkBlockListForComplexCondition(String blockListName, DataTable conditionsTable) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        List<CorePage> resultList = getBlockListWithComplexCondition(blockListName, conditionsTable);

        //TODO что тут происходит и зачем?
        return new BlockListStepResult(resultList,
                conditionsTable.asLists().stream().map(conditionRow -> conditionRow.get(0)).collect(Collectors.toList()));
    }


    /**
     * -----------------------------------------------В КОНКРЕТНОМ------------------------------------------------
     */

    @И("^элемент \"([^\"]*)\" отображается в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult elementDisplayedInBlockWhereTextEquals(String elementName, String listName, String elementNameText, String expectedText) {
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);
        corePageByTextInElement.getElement(elementName).shouldBe(Condition.visible);
        return new BlockListStepResult(corePageByTextInElement, elementName, elementNameText);
    }


    @И("^элемент \"([^\"]*)\" отображается в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст соответствует регулярному выражению \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlockMatches(String elementName, String listName, String elementNameText, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByRegExpInElement(blocksList, elementNameText, expectedText);
        corePageByTextInElement.getElement(elementName).shouldBe(Condition.visible);
        return new BlockListStepResult(corePageByTextInElement, elementName, elementNameText);
    }

    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlock(String elementNameCheck, String regExp, String listName, String elementNameText, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        shouldHaveTextMatches(corePageByTextInElement, elementNameCheck, regExp);
        return new BlockListStepResult(corePageByTextInElement, elementNameCheck, elementNameText);
    }

    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст соответствует регулярному выражению \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlockMatches(String elementNameClick, String regExp, String listName, String elementNameText, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        regExp = getPropertyOrStringVariableOrValue(regExp);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByRegExpInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.shouldHave(Condition.matchText(getPropertyOrStringVariableOrValue(regExp)), Duration.ZERO);

        return new BlockListStepResult(corePageByTextInElement, elementNameClick, elementNameText);
    }

    @И("^css атрибут \"([^\"]*)\" в элементе \"([^\"]*)\" равен \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult checkCssInAnyBlock(String cssName, String elementNameCheck, String cssValue, String listName, String elementNameText, String expectedText) {
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);
        cssName = OtherSteps.getPropertyOrStringVariableOrValue(cssName);
        cssValue = OtherSteps.getPropertyOrStringVariableOrValue(cssValue);

        SelenideElement element = corePageByTextInElement.getElement(elementNameCheck);
        element.shouldHave(Condition.cssValue(cssName, cssValue));
        return new BlockListStepResult(corePageByTextInElement, elementNameCheck, elementNameText);
    }

    @И("^в списке блоков \"([^\"]*)\" текст элемента \"([^\"]*)\" в (\\d+) блоке сохранен в переменную \"([^\"]*)\"$")
    public IStepResult saveElementTextForNthBlockFromBlockList(String listName, String elementName, int blockIndex, String varName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(listName, CustomCondition.Comparison.more, 0);
        CorePage block = blocksList.get(blockIndex - 1);

        SelenideElement element = block.getElement(elementName);

        this.coreScenario.getEnvironment().setVar(varName, element.shouldBe(Condition.visible).getText());

        return new BlockListStepResult(block, elementName);
    }

    @И("(\\d+) блок в списке блоков \"([^\"]*)\" содержит элемент \"([^\"]*)\" текст которого соответствует регулярному выражению \"([^\"]*)\"$")
    public IStepResult checkElementInBlockListMatchesText(int blockIndex, String blockListName, String elementName, String expectedText) {
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        CorePage block = blocksList.get(blockIndex - 1);

        shouldHaveTextMatches(block, elementName, expectedText);

        return new BlockListStepResult(block, elementName);
    }

    @И("(\\d+) блок в списке блоков \"([^\"]*)\" содержит элемент \"([^\"]*)\" текст которого (равен|содержит) \"([^\"]*)\"$")
    public IStepResult checkElementInBlockListForText(int blockIndex, String blockListName, String elementName, String conditionString, String expectedText) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        CorePage block = blocksList.get(blockIndex - 1);

        Condition condition = null;
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);
        switch (conditionString) {
            case "равен":
                condition = Condition.exactText(expectedText);
                break;
            case "содержит":
                condition = Condition.text(expectedText);
                break;
        }

        block.getElement(elementName).should(condition);
        return new BlockListStepResult(block, elementName);
    }

    @И("^выполнено сохранение текста элемента \"([^\"]*)\" в переменную \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult saveElementTextToVarInBlockListWhereTextEquals(String elementToSaveText, String varName, String blockListName, String elementToCheckText, String expectedText) {
        List<CorePage> blocksList = this.coreScenario.getCurrentPage().getBlocksList(blockListName);
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementToCheckText, expectedText);
        SelenideElement element = corePageByTextInElement.getElement(elementToSaveText);
        CoreScenario.getInstance().getEnvironment().setVar(varName, element.getText());
        return new BlockListStepResult(corePageByTextInElement, elementToCheckText, elementToSaveText);
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
    public IStepResult listBlockSize(String listName, String blockName, int expectedCountBlock) {
        List<CorePage> blocksList = getBlockListWithCheckingTheQuantity(
                blockName, listName, CustomCondition.Comparison.equal, expectedCountBlock);

        return new BlockListStepResult(blocksList);
    }

    @И("^в любом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlock(String listName, String blockName, String elementName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);

        CorePage corePage = findCorePageByTextInElement(blocksList, elementName, expectedText);
        return new BlockListStepResult(corePage, elementName);
    }


    @И("^в каждом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" в элементе \"([^\"]*)\" текст в формате \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlockMatches123(String listName, String blockName, String elementName, String regExp) {
        regExp = getPropertyOrStringVariableOrValue(regExp);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            shouldHaveTextMatches(block, elementName, regExp);
        }
        return new BlockListStepResult(blocksList, Collections.singletonList(elementName));
    }

    @И("^в каждом блоке в списке блоков \"([^\"]*)\" в блоке \"([^\"]*)\" элемент \"([^\"]*)\" отображается на странице$")
    public IStepResult elementVisibleAnyBlock(String listName, String blockName, String elementName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);

        for (CorePage block : blocksList) {
            block.getElement(elementName).shouldHave(Condition.visible);
        }
        return new BlockListStepResult(blocksList, Collections.singletonList(elementName));
    }

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult clickButtonInBlockWhereTextEquals(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.hover();
        element.click();
        return new BlockListStepResult(corePageByTextInElement, elementNameClick, elementNameText);
    }

    @И("^элемент \"([^\"]*)\" отображается на странице в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult visibleElementBlockWhereTextEquals(String elementNameClick, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.shouldHave(Condition.visible);
        return new BlockListStepResult(corePageByTextInElement, elementNameClick, elementNameText);
    }

    @И("^текст в элементе \"([^\"]*)\" равен формату \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public IStepResult checkTextInAnyBlockMatches(String elementNameCheck, String regExp, String listName, String elementNameText, String expectedText, String blockName) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockName, listName, CustomCondition.Comparison.more, 0);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        shouldHaveTextMatches(corePageByTextInElement, elementNameCheck, regExp);
        return new BlockListStepResult(corePageByTextInElement, elementNameCheck, elementNameText);
    }

}
