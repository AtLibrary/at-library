/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.at.library.core.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Hidden;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.utils.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.at.library.core.core.helpers.PropertyLoader.tryLoadProperty;

/**
 * Класс для реализации паттерна PageObject
 */
@Slf4j
public abstract class CorePage extends ElementsContainer {

    public static boolean isAppeared = Boolean.parseBoolean(tryLoadProperty("isAppeared"));

    /**
     * Список всех элементов страницы
     */
    private Map<String, Object> namedElements;
    /**
     * Список элементов страницы, не помеченных аннотацией "Optional" или "Hidden"
     */
    private List<SelenideElement> primaryElements;
    /**
     * Список элементов страницы, помеченных аннотацией "Hidden"
     */
    private List<SelenideElement> hiddenElements;

    public CorePage() {
        super();
    }

    /**
     * Поиск элемента по имени внутри списка элементов
     */
    public static SelenideElement getButtonFromListByName(List<SelenideElement> listButtons, String nameOfButton) {
        List<String> names = new ArrayList<>();
        for (SelenideElement button : listButtons) {
            names.add(button.getText());
        }
        return listButtons.get(names.indexOf(nameOfButton));
    }

    /**
     * Приведение объекта к типу SelenideElement
     */
    private static SelenideElement castToSelenideElement(Object object) {
        if (object instanceof SelenideElement) {
            return (SelenideElement) object;
        }
        return null;
    }

    private static CorePage castToCorePage(Object object) {
        if (object instanceof CorePage) {
            return (CorePage) object;
        }
        return null;
    }

    /**
     * Получение блока со страницы по имени (аннотированного "Name")
     */
    public CorePage getBlock(String blockName) {
        return (CorePage) java.util.Optional.ofNullable(namedElements.get(blockName))
                .orElseThrow(() -> new IllegalArgumentException("Блок " + blockName + " не описан на странице " + this.getClass().getName()));
    }

    /**
     * Получение списка блоков со страницы по имени (аннотированного "Name")
     */
    @SuppressWarnings("unchecked")
    public List<CorePage> getBlocksList(String listName) {
        Object value = namedElements.get(listName);
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Список " + listName + " не описан на странице " + this.getClass().getName());
        }
        Stream<Object> s = ((List) value).stream();
        return s.map(CorePage::castToCorePage).collect(toList());
    }

    /**
     * Получение списка из элементов блока со страницы по имени (аннотированного "Name")
     */
    public List<SelenideElement> getBlockElements(String blockName) {
        return getBlock(blockName).namedElements.entrySet().stream()
                .map(x -> ((SelenideElement) x.getValue())).collect(toList());
    }

    /**
     * Получение элемента блока со страницы по имени (аннотированного "Name")
     */
    public SelenideElement getBlockElement(String blockName, String elementName) {
        return ((SelenideElement) getBlock(blockName).namedElements.get(elementName));
    }

    /**
     * Получение элемента со страницы по имени (аннотированного "Name")
     */
    public SelenideElement getElement(String elementName) {
        return (SelenideElement) java.util.Optional.ofNullable(namedElements.get(elementName))
                .orElseThrow(() -> new IllegalArgumentException("Элемент " + elementName + " не описан на странице " + this.getClass().getName()));
    }

    /**
     * Получение элемента-списка со страницы по имени
     */
    @SuppressWarnings("unchecked")
    public ElementsCollection getElementsList(String listName) {
        Object value = namedElements.get(listName);
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Список " + listName + " не описан на странице " + this.getClass().getName());
        }
        FindBy listSelector = Arrays.stream(this.getClass().getFields())
                .filter(f -> f.getAnnotation(Name.class) != null && f.getAnnotation(Name.class).value().equals(listName))
                .map(f -> f.getAnnotation(FindBy.class))
                .findFirst().get();
        FindBy.FindByBuilder findByBuilder = new FindBy.FindByBuilder();
        return $$(findByBuilder.buildIt(listSelector, null));
    }

    /**
     * Получение всех элементов страницы, не помеченных аннотацией "Optional" или "Hidden"
     */
    public List<SelenideElement> getPrimaryElements() {
        if (primaryElements == null) {
            primaryElements = readWithWrappedElements();
        }
        return new ArrayList<>(primaryElements);
    }

    /**
     * Получение всех элементов страницы, помеченных аннотацией "Hidden"
     */
    public List<SelenideElement> getHiddenElements() {
        if (hiddenElements == null) {
            hiddenElements = readWithHiddenElements();
        }
        return new ArrayList<>(hiddenElements);
    }

    /**
     * Обертка над CorePage.isAppeared
     * Ex: CorePage.appeared().doSomething();
     */
    public final CorePage appeared() {
        if (isAppeared) {
            isAppeared();
        }
        return this;
    }

    /**
     * Обертка над CorePage.isDisappeared
     * Ex: CorePage.disappeared().doSomething();
     */
    public final CorePage disappeared() {
        isDisappeared();
        return this;
    }

    /**
     * Проверка того, что элементы, не помеченные аннотацией "Optional", отображаются,
     * а элементы, помеченные аннотацией "Hidden", скрыты.
     */
    public void isAppeared() {
        getPrimaryElements().forEach(elem ->
                elem.shouldHave(Condition.appear));
        getHiddenElements().forEach(elem ->
                elem.shouldHave(Condition.hidden));
        eachForm(CorePage::isAppeared);
    }

    private void eachForm(Consumer<CorePage> func) {
        Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(ru.at.library.core.cucumber.annotations.Optional.class) == null && f.getAnnotation(Hidden.class) == null)
                .forEach(f -> {
                    if (CorePage.class.isAssignableFrom(f.getType())) {
                        CorePage corePage = CoreScenario.getInstance().getPage((Class<? extends CorePage>) f.getType()).initialize();
                        func.accept(corePage);
                    }
                });
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional" или "Hidden", исчезли
     */
    protected void isDisappeared() {
        getPrimaryElements().parallelStream().forEach(elem ->
                elem.waitWhile(Condition.exist, timeout));
    }

    /**
     * Обертка над CorePage.isAppearedInIe
     * Ex: CorePage.ieAppeared().doSomething();
     * Используется при работе с IE
     */
    public final CorePage ieAppeared() {
        if (isAppeared) {
            isAppearedInIe();
        }
        return this;
    }

    /**
     * Обертка над CorePage.isDisappearedInIe
     * Ex: CorePage.ieDisappeared().doSomething();
     * Используется при работе с IE
     */
    public final CorePage ieDisappeared() {
        isDisappearedInIe();
        return this;
    }

    /**
     * Проверка того, что элементы, не помеченные аннотацией "Optional", отображаются,
     * а элементы, помеченные аннотацией "Hidden", скрыты.
     * Вместо parallelStream используется stream из-за медленной работы IE
     */
    protected void isAppearedInIe() {
        getPrimaryElements().stream().forEach(elem ->
                elem.shouldHave(Condition.appear));
        getHiddenElements().stream().forEach(elem ->
                elem.shouldHave(Condition.hidden));
        eachForm(CorePage::isAppearedInIe);
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional" или "Hidden", исчезли
     * Вместо parallelStream используется stream из-за медленной работы IE
     */
    protected void isDisappearedInIe() {
        getPrimaryElements().stream().forEach(elem ->
                elem.waitWhile(Condition.exist, timeout));
    }


    @Override
    public void setSelf(SelenideElement self) {
        super.setSelf(self);
        initialize();
    }

    public CorePage initialize() {
        namedElements = readNamedElements();
        primaryElements = readWithWrappedElements();
        hiddenElements = readWithHiddenElements();
        return this;
    }

    /**
     * Поиск и инициализации элементов страницы
     */
    private Map<String, Object> readNamedElements() {
        checkNamedAnnotations();
        return Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(Name.class) != null)
                .peek(this::checkFieldType)
                .collect(toMap(f -> f.getAnnotation(Name.class).value(), this::extractFieldValueViaReflection));
    }

    private void checkFieldType(Field f) {
        if (!SelenideElement.class.isAssignableFrom(f.getType())
                && !CorePage.class.isAssignableFrom(f.getType())) {
            checkCollectionFieldType(f);
        }
    }

    private void checkCollectionFieldType(Field f) {
        if (ElementsCollection.class.isAssignableFrom(f.getType())) {
            return;
        } else if (ElementsCollection.class.isAssignableFrom(f.getType()) || List.class.isAssignableFrom(f.getType())) {
            ParameterizedType listType = (ParameterizedType) f.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            if (SelenideElement.class.isAssignableFrom(listClass) || CorePage.class.isAssignableFrom(listClass)) {
                return;
            }
        }
        throw new IllegalStateException(
                format("Поле с аннотацией @Name должно иметь тип SelenideElement или ElementsCollection.\n" +
                        "Если поле описывает блок, оно должно принадлежать классу, унаследованному от CorePage.\n" +
                        "Найдено поле с типом %s", f.getType()));
    }

    /**
     * Поиск по аннотации "Name"
     */
    private void checkNamedAnnotations() {
        List<String> list = Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(Name.class) != null)
                .map(f -> f.getAnnotation(Name.class).value())
                .collect(toList());
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalStateException("Найдено несколько аннотаций @Name с одинаковым значением в классе " + this.getClass().getName());
        }
    }

    /**
     * Поиск и инициализация элементов страницы без аннотации Optional или Hidden
     */
    private List<SelenideElement> readWithWrappedElements() {
        return Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(ru.at.library.core.cucumber.annotations.Optional.class) == null && f.getAnnotation(Hidden.class) == null)
                .map(this::extractFieldValueViaReflection)
                .flatMap(v -> v instanceof List ? ((List<?>) v).stream() : Stream.of(v))
                .map(CorePage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    /**
     * Поиск и инициализация элементов страницы c аннотацией Hidden
     */
    private List<SelenideElement> readWithHiddenElements() {
        return Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(Hidden.class) != null)
                .map(this::extractFieldValueViaReflection)
                .flatMap(v -> v instanceof List ? ((List<?>) v).stream() : Stream.of(v))
                .map(CorePage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Object extractFieldValueViaReflection(Field field) {
        return Reflection.extractFieldValue(field, this);
    }


}
