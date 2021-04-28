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

import com.codeborne.selenide.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.at.library.core.cucumber.annotations.Hidden;
import ru.at.library.core.cucumber.annotations.Mandatory;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.selenide.ElementCheck;
import ru.at.library.core.cucumber.selenide.IElementCheck;
import ru.at.library.core.cucumber.utils.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static ru.at.library.core.core.helpers.PropertyLoader.loadProperty;
import static ru.at.library.core.cucumber.selenide.ElementChecker.checkElements;
import static ru.at.library.core.cucumber.selenide.ElementChecker.elementCheckListAsString;

/**
 * Класс для реализации паттерна PageObject
 */
@Slf4j
public abstract class CorePage extends ElementsContainer {

    public static boolean isAppeared = Boolean.parseBoolean(loadProperty("isAppeared", "false"));
    public static boolean checkMandatory = Boolean.parseBoolean(loadProperty("checkMandatory", "true"));

    /**
     * Имя страницы
     */
    @Getter
    @Setter
    private String name;

    /**
     * Список всех элементов страницы
     */
    private Map<String, PageElement> namedElements;

    public CorePage() {
        super();
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
        CorePage corePage = (CorePage) object;
        return Selenide.page(corePage).initialize();
    }

    /**
     * Получение элемента со страницы по имени (аннотированного "Name")
     */
    public SelenideElement getElement(String elementName) {
        return (SelenideElement) java.util.Optional.ofNullable(namedElements.get(elementName).getElement())
                .orElseThrow(() -> new IllegalArgumentException("SelenideElement " + elementName + " не описан на странице " + this.getClass().getName()));
    }

    /**
     * Получение элемента-списка со страницы по имени
     */
    public ElementsCollection getElementsList(String listName) {
        Object value = namedElements.get(listName).getElement();
        if (!(value instanceof ElementsCollection)) {
            throw new IllegalArgumentException("ElementsCollection " + listName + " не описан на странице " + this.getClass().getName());
        }
        return (ElementsCollection) value;
    }

    /**
     * Получение блока со страницы по имени (аннотированного "Name")
     */
    public CorePage getBlock(String blockName) {
        CorePage corePageBlockName = (CorePage) Optional.ofNullable(namedElements.get(blockName).getElement())
                .orElseThrow(() -> new IllegalArgumentException("CorePage " + blockName + " не описан на странице " + this.getClass().getName()));

        return castToCorePage(corePageBlockName);
    }

    /**
     * Получение списка блоков со страницы по имени (аннотированного "Name")
     */
    @SuppressWarnings("unchecked")
    public List<CorePage> getBlocksList(String listCorePage) {
        Object value = namedElements.get(listCorePage).getElement();
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("List<CorePage> " + listCorePage + " не описан на странице " + this.getClass().getName());
        }
        Stream<Object> stream = ((List<Object>) value).stream();

        return stream.map(CorePage::castToCorePage).collect(toList());
    }

    /**
     * Получение всех элементов страницы, помеченных аннотацией "Mandatory"
     */
    public List<PageElement> getMandatoryElements() {
        return namedElements.values().stream()
                .filter(pageElement -> pageElement.getMode().equals(ElementMode.MANDATORY))
                .flatMap(v -> v.getType().equals(ElementType.LIST_CORE_PAGE)
                        ? ((List<?>) v.getElement()).stream().map(subElement -> new PageElement(subElement, v.getName(), ElementType.CORE_PAGE, ElementMode.MANDATORY))
                        : Stream.of(v))
                .flatMap(v -> v.getType().equals(ElementType.CORE_PAGE)
                        ? castToCorePage(v.getElement()).getMandatoryAndPrimaryDeep().stream()
                        : Stream.of(v))
                .collect(toList());
    }

    public List<PageElement> getMandatoryAndPrimaryDeep() {
        return namedElements.values().stream()
                .filter(pageElement -> pageElement.getMode().equals(ElementMode.MANDATORY) || pageElement.getMode().equals(ElementMode.PRIMARY))
                .flatMap(v -> v.getType().equals(ElementType.LIST_CORE_PAGE)
                    ? ((List<?>) v.getElement()).stream().map(subElement -> new PageElement(subElement, v.getName(), ElementType.getType(subElement), v.getMode()))
                    : Stream.of(v))
                .flatMap(v -> v.getType().equals(ElementType.CORE_PAGE)
                    ? castToCorePage(v.getElement()).getMandatoryAndPrimaryDeep().stream()
                    : Stream.of(v))
                .collect(toList());
    }

    /**
     * Получение всех элементов страницы, не помеченных аннотацией "Optional" или "Hidden"
     */
    public List<PageElement> getPrimaryElementsDeep() {
        return namedElements.values().stream()
                .filter(pageElement -> pageElement.getMode().equals(ElementMode.PRIMARY))
                .flatMap(v -> v.getType().equals(ElementType.LIST_CORE_PAGE)
                        ? ((List<?>) v.getElement()).stream().map(subElement -> new PageElement(subElement, v.getName(), ElementType.getType(subElement), v.getMode()))
                        : Stream.of(v))
                .flatMap(v -> v.getType().equals(ElementType.CORE_PAGE)
                        ? castToCorePage(v.getElement()).getMandatoryAndPrimaryDeep().stream()
                        : Stream.of(v))
                .collect(toList());
    }

    /**
     * Получение всех элементов страницы, помеченных аннотацией "Hidden"
     */
    public List<PageElement> getHiddenElementsDeep() {
        return namedElements.values().stream()
                .filter(pageElement -> pageElement.getMode().equals(ElementMode.HIDDEN))
                .flatMap(v -> v.getType().equals(ElementType.LIST_CORE_PAGE)
                        ? ((List<?>) v.getElement()).stream().map(subElement -> new PageElement(subElement, v.getName(), ElementType.getType(subElement), v.getMode()))
                        : Stream.of(v))
                .flatMap(v -> v.getType().equals(ElementType.CORE_PAGE)
                        ? castToCorePage(v.getElement()).getHiddenElementsDeep().stream()
                        : Stream.of(v))
                .collect(toList());
    }

    /**
     * Обертка над CorePage.isAppeared
     * Ex: CorePage.appeared().doSomething();
     */
    public final CorePage appeared() {
        isAppeared();
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
        if (checkMandatory) checkMandatory();
        if (isAppeared) checkPrimary();
    }

    private void checkMandatory() {
        String template = "Элемент '%s' %s";
        List<IElementCheck> checkResult = checkElements(
                getMandatoryElements().stream()
                        .map(pageElement -> pageElementToElementCheck(pageElement, Condition.appear, String.format(template, pageElement.getName(), "отображается на странице")))
                        .collect(toList()),
                Configuration.timeout);
        if (!checkResult.stream().allMatch(IElementCheck::getStatus)) throw new AssertionError("На текущей странице не отобразились все обязательные элементы:\n" + elementCheckListAsString(checkResult.stream().filter(r -> !r.getStatus()).collect(toList())));
    }

    private void checkPrimary() {
        String template = "Элемент '%s' %s";
        List<IElementCheck> checkResult;
        List<IElementCheck> elementCheckList = new ArrayList<>();
        elementCheckList.addAll(getPrimaryElementsDeep().stream()
                .map(pageElement -> pageElementToElementCheck(pageElement, Condition.appear, String.format(template, pageElement.getName(), "отображается на странице")))
                .collect(toList())
        );
        elementCheckList.addAll(getHiddenElementsDeep().stream()
                .map(pageElement -> pageElementToElementCheck(pageElement, Condition.hidden, String.format(template, pageElement.getName(), "не отображается на странице")))
                .collect(toList())
        );
        checkResult = checkElements(elementCheckList, Configuration.timeout);
        CoreScenario.getInstance().getAssertionHelper().hamcrestAssert("На текущей странице не отобразились все основные элементы:\n" + elementCheckListAsString(checkResult.stream().filter(r -> !r.getStatus()).collect(toList())), checkResult.stream().allMatch(IElementCheck::getStatus), is(equalTo(true)));
    }

    private IElementCheck pageElementToElementCheck(PageElement pageElement, Condition condition, String message) {
        SelenideElement element = pageElement.getType().equals(ElementType.ELEMENTS_COLLECTION)
                ? ((ElementsCollection) pageElement.getElement()).first()
                : castToSelenideElement(pageElement.getElement());
        return new ElementCheck(pageElement.getName(), element, condition, message);
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional" или "Hidden", исчезли
     */
    protected void isDisappeared() {
        List<IElementCheck> checkResult = checkElements(
            getPrimaryElementsDeep().stream()
                    .map(pageElement -> new ElementCheck(pageElement.getName(), castToSelenideElement(pageElement.getElement()), Condition.hidden, String.format("Элемент '%s' %s", pageElement.getName(), "не отображается на странице")))
                    .collect(toList()), Configuration.timeout);
        assertThat("Все описанные на странице элементы исчезли со страницы", checkResult.stream().allMatch(IElementCheck::getStatus), is(equalTo(true)));
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
        isAppeared();
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional" или "Hidden", исчезли
     * Вместо parallelStream используется stream из-за медленной работы IE
     */
    protected void isDisappearedInIe() {
        isDisappeared();
    }

    public CorePage initialize() {
        namedElements = readNamedElements();
        return this;
    }

    /**
     * Поиск и инициализации элементов страницы
     */
    private Map<String, PageElement> readNamedElements() {
        checkNamedAnnotations();
        Map<String, PageElement> resultMap = new HashMap<>();
        Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(Name.class) != null)
                .peek(this::checkFieldType)
                .forEach(f -> {
                    String name = f.getAnnotation(Name.class).value();
                    Object obj = extractFieldValueViaReflection(f);
                    PageElement pageElement = new PageElement(obj, name, ElementType.getType(obj), ElementMode.getMode(f));
                    resultMap.put(name, pageElement);
                });
        return resultMap;
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
        Set<String> uniques = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        Arrays.stream(getClass().getFields())
                .filter(f -> f.getAnnotation(Name.class) != null)
                .map(f -> f.getAnnotation(Name.class).value())
                .forEach(name -> {
                    if (!uniques.add(name)) {
                        duplicates.add(name);
                    }
                });
        if (!duplicates.isEmpty()) {
            throw new IllegalStateException(String.format("Найдено несколько аннотаций @Name с одинаковым значением в классе %s\nДубликаты: %s", this.getClass().getName(), duplicates));
        }
    }

    private Object extractFieldValueViaReflection(Field field) {
        return Reflection.extractFieldValue(field, this);
    }

    @Data
    @AllArgsConstructor
    static class PageElement {
        private Object element;
        private String name;
        private ElementType type;
        private ElementMode mode;
    }

    public enum ElementType {
        SELENIDE_ELEMENT,
        ELEMENTS_COLLECTION,
        CORE_PAGE,
        LIST_CORE_PAGE;

        public static ElementType getType(Object obj) {
            ElementType type = null;
            if (obj instanceof SelenideElement) {
                type = SELENIDE_ELEMENT;
            } else if (obj instanceof ElementsCollection) {
                type = ELEMENTS_COLLECTION;
            } else if (obj instanceof CorePage) {
                type = CORE_PAGE;
            } else if (obj instanceof List) {
                type = LIST_CORE_PAGE;
            }
            return type;
        }
    }

    public enum ElementMode {
        MANDATORY,
        PRIMARY,
        OPTIONAL,
        HIDDEN;

        public static ElementMode getMode(Field field) {
            ElementMode elementMode;
            if (field.getAnnotation(Mandatory.class) != null) {
                elementMode = MANDATORY;
            } else if (field.getAnnotation(ru.at.library.core.cucumber.annotations.Optional.class) != null) {
                elementMode = OPTIONAL;
            } else if (field.getAnnotation(Hidden.class) != null) {
                elementMode = HIDDEN;
            } else elementMode = PRIMARY;
            return elementMode;
        }
    }

}
