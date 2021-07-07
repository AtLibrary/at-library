package ru.at.library.web.entities;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import ru.at.library.web.core.CastToWebElements;
import ru.at.library.web.core.IStepResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Класс используемый для выделения элементов на скриншоте страницы в результате выполения
 * шагов с проверкой элементов {@link SelenideElement} или {@link ElementsCollection}
 * <p>
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
     * @param elementsCollection список {@link ElementsCollection} для выделения на скриншоте страницы
     */
    public CommonStepResult(ElementsCollection elementsCollection) {
        this.elementsCollection = elementsCollection;
    }

    /**
     * Выделение на скриншоте страницы списка {@link ElementsCollection} с одним или несколькими элементами {@link SelenideElement}
     *
     * @param elementsCollection список {@link ElementsCollection} для выделения на скриншоте страницы
     * @param selenideElements   один или несколько имен элементов для выделения на скриншоте страницы
     */
    public CommonStepResult(ElementsCollection elementsCollection, SelenideElement... selenideElements) {
        this.elementsCollection = elementsCollection;
        this.selenideElements.addAll(Arrays.asList(selenideElements));
    }

    /**
     * Выделение на скриншоте страницы одного или нескольких элементов {@link SelenideElement}
     *
     * @param selenideElements один или несколько имен элементов для выделения на скриншоте страницы
     */
    public CommonStepResult(SelenideElement... selenideElements) {
        this(Arrays.asList(selenideElements));
    }

    /**
     * Выделение на скриншоте страницы списка элементов {@link SelenideElement}
     *
     * @param selenideElements список элементов для выделения на скриншоте страницы
     */
    public CommonStepResult(List<SelenideElement> selenideElements) {
        this.selenideElements.addAll(selenideElements);
    }

    @Override
    public Optional<List<WebElement>> getWebElements() {
        List<WebElement> resultList = new ArrayList<>();
        CastToWebElements.getElementsCollectionAsElementsList(elementsCollection).ifPresent(resultList::addAll);
        selenideElements.forEach(element -> CastToWebElements.tryGetWebElement(element).ifPresent(resultList::add));
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList);
    }
}
