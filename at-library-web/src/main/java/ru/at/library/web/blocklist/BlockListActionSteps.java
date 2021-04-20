package ru.at.library.web.blocklist;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.И;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.core.steps.OtherSteps;
import ru.at.library.web.step.result.core.IStepResult;
import ru.at.library.web.step.result.entities.BlockListStepResult;

import java.util.List;
import java.util.stream.Collectors;

import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.web.blocklist.OtherMethodBlockCorePageList.*;

public class BlockListActionSteps {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------Действия списка блоков------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    @И("^выполнено нажатие на кнопку \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult clickButtonInBlockListWhereTextEquals(String elementNameClick, String blockListName, String elementNameText, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);

        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);
        SelenideElement element = corePageByTextInElement.getElement(elementNameClick);
        element.shouldHave(Condition.visible);
        element.hover();
        element.click();

        return new BlockListStepResult(corePageByTextInElement, elementNameClick, elementNameText);
    }

    @И("^введено значение \"([^\"]*)\" в поле \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult inputValueInBlockListWhereTextEquals(String value, String elementName, String blockListName, String elementNameText, String expectedText) throws Exception {
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        SelenideElement element = corePageByTextInElement.getElement(elementName);
        element.shouldHave(Condition.visible);
        element.click();
        element.clear();
        element.sendKeys(value);

        return new BlockListStepResult(corePageByTextInElement, elementName, elementNameText);
    }

    @И("^выполнено наведение на элемент \"([^\"]*)\" в списке блоков \"([^\"]*)\" где в элементе \"([^\"]*)\" текст равен \"([^\"]*)\"$")
    public IStepResult hoverOnElementInBlockListWhereTextEquals(String elementName, String blockListName, String elementNameText, String expectedText) {
        expectedText = OtherSteps.getPropertyOrStringVariableOrValue(expectedText);
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);
        CorePage corePageByTextInElement = findCorePageByTextInElement(blocksList, elementNameText, expectedText);

        corePageByTextInElement.getElement(elementName).hover();

        return new BlockListStepResult(corePageByTextInElement, elementName, elementNameText);
    }

    @И("^выполнено нажатие на элемент \"([^\"]*)\" в (\\d+) блоке в списке блоков \"([^\"]*)\"$")
    public IStepResult clickOnElementBlockInBlockList(String blockElement, int blockIndex, String blockListName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        CorePage block = blocksList.get(blockIndex-1);
        block.getElement(blockElement).click();
        return new BlockListStepResult(block, blockElement);
    }

    @И("^выполнено нажатие на (\\d+) элемент в списке блоков \"([^\"]*)\"$")
    public IStepResult clickOnBlockInBlockList(int blockIndex, String blockListName) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        CorePage block = blocksList.get(blockIndex-1);

        block.getSelf().shouldBe(Condition.enabled).click();
        return new BlockListStepResult(block);
    }

    @И("^в списке блоков \"([^\"]*)\" выполнено нажатие на блок элементы которого соответствуют списку$")
    public IStepResult clickOnBlockInBlockListWIthComplexCondition(String blockListName, DataTable conditionsTable) {
        List<CorePage> blocksList =
                getBlockListWithCheckingTheQuantity(blockListName, CustomCondition.Comparison.more, 0);

        List<CorePage> resultList = getBlockListWithComplexCondition(blockListName, conditionsTable);

        if (resultList.size() != 1) {
            throw new IllegalArgumentException("По заданному списку условий найдено 0 или более 1 блока\n" + blockListToString(resultList));
        }

        resultList.get(0).getSelf().shouldBe(Condition.enabled).click();

        return new BlockListStepResult(resultList,
                conditionsTable.asLists().stream().map(conditionRow -> conditionRow.get(0)).collect(Collectors.toList()));
    }
}
