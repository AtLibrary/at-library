package ru.at.library.web.step.result.entities;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import ru.at.library.web.step.result.core.IStepResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.at.library.web.step.result.core.CastToWebElements.getElementsCollectionAsElementsList;
import static ru.at.library.web.step.result.core.CastToWebElements.tryGetWebElement;

public class CommonStepResult implements IStepResult {

    private ElementsCollection elementsCollection = null;
    private final List<SelenideElement> selenideElements = new ArrayList<>();

    public CommonStepResult(ElementsCollection elementsCollection) {
        this.elementsCollection = elementsCollection;
    }

    public CommonStepResult(ElementsCollection elementsCollection, SelenideElement... selenideElements) {
        this.elementsCollection = elementsCollection;
        this.selenideElements.addAll(Arrays.asList(selenideElements));
    }

    public CommonStepResult(SelenideElement... selenideElements) {
        this.selenideElements.addAll(Arrays.asList(selenideElements));
    }

    @Override
    public Optional<List<WebElement>> getWebElements() {
        List<WebElement> resultList = new ArrayList<>();
        getElementsCollectionAsElementsList(elementsCollection).ifPresent(resultList::addAll);
        selenideElements.forEach(element -> tryGetWebElement(element).ifPresent(resultList::add));
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList);
    }
}
