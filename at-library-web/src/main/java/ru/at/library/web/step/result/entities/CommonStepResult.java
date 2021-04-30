package ru.at.library.web.step.result.entities;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.web.step.result.core.IStepResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.at.library.web.step.result.core.CastToWebElements.getElementsCollectionAsElementsList;
import static ru.at.library.web.step.result.core.CastToWebElements.tryGetWebElement;

/**
 * Класс используемый для выделения элементов на скриншоте страницы в результате выполения
 * шагов с проверкой элементов {@link SelenideElement} или {@link ElementsCollection}
 *
 * Если в результате выполнения cucumber шага в котором есть взаимодействие с элементами {@link SelenideElement} или {@link ElementsCollection}
 * необходимо выбелить на скриншоте страницы определенные элементы - этот шаг должен возвращать объект
 * реализующий интерфейс {@link IStepResult}
 */
public class CommonStepResult implements IStepResult {

    private ElementsCollection elementsCollection = null;
    private final List<SelenideElement> selenideElements = new ArrayList<>();

    /**
     * Выделение на скриншоте страницы списка {@link ElementsCollection}
     *
     * @param elementsCollection    список {@link ElementsCollection} для выделения на скриншоте страницы
     */
    public CommonStepResult(ElementsCollection elementsCollection) {
        this.elementsCollection = elementsCollection;
    }

    /**
     * Выделение на скриншоте страницы списка {@link ElementsCollection} с одним или несколькими элементами {@link SelenideElement}
     *
     * @param elementsCollection    список {@link ElementsCollection} для выделения на скриншоте страницы
     * @param selenideElements      один или несколько имен элементов для выделения на скриншоте страницы
     */
    public CommonStepResult(ElementsCollection elementsCollection, SelenideElement... selenideElements) {
        this.elementsCollection = elementsCollection;
        this.selenideElements.addAll(Arrays.asList(selenideElements));
    }

    /**
     * Выделение на скриншоте страницы одного или нескольких элементов {@link SelenideElement}
     *
     * @param selenideElements      один или несколько имен элементов для выделения на скриншоте страницы
     */
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
