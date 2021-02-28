package ru.at.library.web.step.result.entities;

import org.openqa.selenium.WebElement;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.web.step.result.core.IStepResult;

import java.util.*;

import static ru.at.library.web.step.result.core.CastToWebElements.getBlockListAsWebElementsList;
import static ru.at.library.web.step.result.core.CastToWebElements.tryGetWebElement;

public class BlockListStepResult implements IStepResult {

    private final List<CorePage> blockList = new ArrayList<>();
    private final Set<String> blockElements = new HashSet<>();

    public BlockListStepResult(List<CorePage> blockList) {
        this.blockList.addAll(blockList);
    }

    public BlockListStepResult(CorePage block) {
        this.blockList.add(block);
    }

    public BlockListStepResult(List<CorePage> blockList, List<String> elementsNames) {
        this(blockList);
        this.blockElements.addAll(elementsNames);
    }

    public BlockListStepResult(List<CorePage> blockList, String...elementsNames) {
        this(blockList);
        this.blockElements.addAll(Arrays.asList(elementsNames));
    }

    public BlockListStepResult(CorePage block, String...elementsNames) {
        this(block);
        this.blockElements.addAll(Arrays.asList(elementsNames));
    }

    @Override
    public Optional<List<WebElement>> getWebElements() {
        List<WebElement> resultList = new ArrayList<>();
        getBlockListAsWebElementsList(this.blockList).ifPresent(list -> {
            resultList.addAll(list);
            blockList.forEach(block ->
                    blockElements.forEach(elementName ->
                            tryGetWebElement(block.getElement(elementName)).ifPresent(resultList::add)));
        });
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList);
    }
}
