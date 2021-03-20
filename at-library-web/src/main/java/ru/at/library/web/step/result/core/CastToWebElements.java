package ru.at.library.web.step.result.core;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import ru.at.library.core.cucumber.api.CorePage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class CastToWebElements {

    public static Optional<List<WebElement>> getBlockListAsWebElementsList(List<CorePage> blockList) {
        if (blockList == null || blockList.isEmpty()) {
            return Optional.empty();
        }
        List<WebElement> webElements = new ArrayList<>();
        for (CorePage block:blockList) {
            tryGetWebElement(block.getSelf()).ifPresent(webElements::add);
        }
        if (webElements.isEmpty()) {
            return Optional.empty();
        } else return Optional.of(webElements);
    }

    public static Optional<List<WebElement>> getElementsCollectionAsElementsList(ElementsCollection elementsCollection) {
        if (elementsCollection == null) return Optional.empty();
        List<WebElement> webElements = new ArrayList<>();
        elementsCollection.filter(Condition.visible).forEach(element ->
                tryGetWebElement(element).ifPresent(webElements::add));
        return webElements.isEmpty() ? Optional.empty() : Optional.of(webElements);
    }

    public static Optional<WebElement> tryGetWebElement(SelenideElement selenideElement) {
        WebElement webElement;
        try {
            webElement = selenideElement.toWebElement();
        } catch (Exception | ElementNotFound e) {
            return Optional.empty();
        }
        return Optional.of(webElement);
    }

}
