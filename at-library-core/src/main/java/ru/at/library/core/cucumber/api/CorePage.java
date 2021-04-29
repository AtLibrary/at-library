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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.utils.helpers.Reflection;
import ru.at.library.core.utils.selenide.ElementCheck;
import ru.at.library.core.utils.selenide.IElementCheck;
import ru.at.library.core.utils.selenide.PageElement;
import ru.at.library.core.utils.selenide.PageElement.ElementMode;
import ru.at.library.core.utils.selenide.PageElement.ElementType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static ru.at.library.core.utils.helpers.PropertyLoader.loadProperty;
import static ru.at.library.core.utils.selenide.ElementChecker.*;

/**
 * Класс для реализации паттерна PageObject
 */
@Slf4j
public abstract class CorePage extends ElementsContainer {

    public static boolean isAppeared = Boolean.parseBoolean(loadProperty("isAppeared", "false"));
    public static boolean isHidden = Boolean.parseBoolean(loadProperty("isHidden", "true"));
    public static boolean isMandatory = Boolean.parseBoolean(loadProperty("isMandatory", "true"));

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
     * Поиск и инициализации элементов страницы с аннотацией @Name и сбор их в Map<String, PageElement> namedElements
     */
    public CorePage initialize() {
        checkNamedAnnotations();
        Arrays.stream(getClass().getFields())
                .filter(field -> field.getAnnotation(Name.class) != null)
                .peek(this::checkFieldType)
                .forEach(fieldCheckedType -> {
                    String name = fieldCheckedType.getAnnotation(Name.class).value();
                    Object obj = extractFieldValueViaReflection(fieldCheckedType);
                    PageElement pageElement = new PageElement(obj, name, ElementType.getType(obj), ElementMode.getMode(fieldCheckedType));
                    namedElements.put(name, pageElement);
                });
        return this;
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
     * Проверка того, что элементы, не помеченные аннотацией "Optional", отображаются,
     * а элементы, помеченные аннотацией "Hidden", скрыты.
     */
    public void isAppeared() {
        if (isMandatory){
            checkMandatory();
        }
        if (isHidden){
            checkHidden();
        }
        if (isAppeared){
            checkPrimary();
        }
    }

    /**
     * Проверка, что все (SelenideElement/ElementCollection/Наследники CorePage) на странице исчезли
     */
    public void isDisappeared() {
        List<ElementMode> modesToCheck = Arrays.asList(ElementMode.MANDATORY, ElementMode.PRIMARY, ElementMode.HIDDEN, ElementMode.OPTIONAL);
        List<IElementCheck> elementChecks = pageElementToElementCheck(
                getElementsWithModes(modesToCheck, modesToCheck),
                Condition.hidden,
                "не отображается на странице");

        List<IElementCheck> checkResult = checkElements(elementChecks, Configuration.timeout);

        CoreScenario.getInstance().getAssertionHelper()
                .hamcrestAssert(
                        "На текущей странице не исчезли все описанные на странице элементы:\n" + elementFailedCheckListAsString(checkResult),
                        checkResult.stream().allMatch(IElementCheck::getStatus),
                        is(equalTo(true))
                );
    }

    /**
     * Проверка, что все (SelenideElement/ElementCollection/Наследники CorePage) c аннотацией Mandatory отображаются на странице
     */
    private void checkMandatory() {
        List<ElementMode> parentModesToCheck = Collections.singletonList(ElementMode.MANDATORY);
        List<ElementMode> childModesToCheck = Arrays.asList(ElementMode.MANDATORY, ElementMode.PRIMARY);
        List<IElementCheck> elementChecks = pageElementToElementCheck(
                getElementsWithModes(parentModesToCheck, childModesToCheck),
                Condition.appear,
                "отображается на странице");

        List<IElementCheck> checkResult = checkElements(elementChecks, Configuration.timeout);

        if (!checkResult.stream().allMatch(IElementCheck::getStatus)) {
            throw new AssertionError("На текущей странице не отобразились все обязательные элементы:\n" + elementFailedCheckListAsString(checkResult));
        }
    }

    /**
     * Проверка, что все (SelenideElement/ElementCollection/Наследники CorePage) c аннотацией Hidden не отображаются на странице
     */
    private void checkHidden() {
        List<ElementMode> modesToCheck = Collections.singletonList(ElementMode.HIDDEN);
        List<IElementCheck> elementChecks = pageElementToElementCheck(
                getElementsWithModes(modesToCheck, modesToCheck),
                Condition.hidden,
                "не отображается на странице");

        List<IElementCheck> checkResult = checkElements(elementChecks, Configuration.timeout);

        CoreScenario.getInstance().getAssertionHelper()
                .hamcrestAssert(
                        "На текущей странице не исчезли все элементы помеченные Hidden:\n" + elementFailedCheckListAsString(checkResult),
                        checkResult.stream().allMatch(IElementCheck::getStatus),
                        is(equalTo(true))
                );
    }


    /**
     * Проверка, что все (SelenideElement/ElementCollection/Наследники CorePage) без аннотации Hidden/Optional отображаются на странице
     */
    private void checkPrimary() {
        List<ElementMode> parentModesToCheck = Collections.singletonList(ElementMode.PRIMARY);
        List<ElementMode> childModesToCheck = Arrays.asList(ElementMode.MANDATORY, ElementMode.PRIMARY);
        List<IElementCheck> elementChecks = pageElementToElementCheck(
                getElementsWithModes(parentModesToCheck, childModesToCheck),
                Condition.appear,
                "отображается на странице");

        List<IElementCheck> checkResult = checkElements(elementChecks, Configuration.timeout);

        CoreScenario.getInstance().getAssertionHelper().hamcrestAssert(
                "На текущей странице не отобразились все основные элементы:\n" + elementFailedCheckListAsString(checkResult),
                checkResult.stream().allMatch(IElementCheck::getStatus),
                is(equalTo(true)));
    }

    /**
     * Получение списка элементов {@link PageElement} из {@link #namedElements} с одним из заданных типов {@link ElementMode}
     *
     * @param parentElementsModes   список типов для элементов страницы (элемент страницы должен соответствовать одному из типов данного списка)
     * @param childElementsModes    список типов для элементов внутри блоков страницы (элемент блока страницы должен соответствовать одному из типов данного списка)
     *
     * @return  список элементов страницы и ее блоков, типы которых соответствуют переданным в качестве аргументов
     */
    private List<PageElement> getElementsWithModes(List<ElementMode> parentElementsModes, List<ElementMode> childElementsModes) {
        return namedElements.values().stream()
                .filter(pageElement -> pageElement.checkMode(parentElementsModes))
                .flatMap(elementWithMode -> elementWithMode.getType().equals(ElementType.LIST_CORE_PAGE)
                        ? ((List<?>) elementWithMode.getElement()).stream().map(subElement -> new PageElement(subElement, elementWithMode.getName(), ElementType.CORE_PAGE, elementWithMode.getMode()))
                        : Stream.of(elementWithMode))
                .flatMap(v -> v.getType().equals(ElementType.CORE_PAGE)
                        ? castToCorePage(v.getElement()).getElementsWithModes(childElementsModes, childElementsModes).stream()
                        : Stream.of(v))
                .collect(toList());
    }

    /**
     * Преобразование списка объектов {@link PageElement} в список объектов с интерфейсом {@link IElementCheck}
     *
     * @param values        список объектов {@link PageElement} для преобразования
     * @param condition     условие {@link Condition} которому должен соответствовать каждый из элементов списка values
     * @param message       сообщение, соответствующе условию condition для отображения в случае ошибки
     *
     * @return  список объектов с интерфейсом {@link IElementCheck}
     */
    private List<IElementCheck> pageElementToElementCheck(Collection<PageElement> values, Condition condition, String message) {
        return values.stream()
                .map(pageElement ->
                        pageElementToElementCheck(pageElement, condition, format("Элемент '%s' %s", pageElement.getName(), message))
                ).collect(toList());
    }

    /**
     * Преобразование объекта {@link PageElement} в объект с интерфейсом {@link IElementCheck}
     *
     * @param pageElement   объект {@link PageElement} для преобразования
     * @param condition     условие {@link Condition} которому должен соответствовать pageElement
     * @param message       сообщение, соответствующе условию condition для отображения в случае ошибки
     *
     * @return  объект с интерфейсом {@link IElementCheck}
     */
    private IElementCheck pageElementToElementCheck(PageElement pageElement, Condition condition, String message) {
        SelenideElement element = pageElement.getType().equals(ElementType.ELEMENTS_COLLECTION)
                ? ((ElementsCollection) pageElement.getElement()).first()
                : castToSelenideElement(pageElement.getElement());
        return new ElementCheck(pageElement.getName(), element, condition, message);
    }


    /**
     * Поле с аннотацией @Name должно иметь тип SelenideElement или ElementsCollection или быть наследником CorePage
     */
    private void checkFieldType(Field f) {
        if (!SelenideElement.class.isAssignableFrom(f.getType()) && !CorePage.class.isAssignableFrom(f.getType())) {
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
    }

    /**
     * Проверка всех элементов с аннотацией @Name на дубликаты
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

    /**
     * Приведение объекта к типу SelenideElement
     */
    private SelenideElement castToSelenideElement(Object element) {
        if (!(element instanceof SelenideElement)) {
            throw new IllegalArgumentException("Object: " + element.getClass() + " не является объектом SelenideElement");
        }
        return (SelenideElement) element;
    }

    /**
     * Приведение объекта к типу CorePage и инициализация его полей
     */
    private static CorePage castToCorePage(Object object) {
        CorePage corePage = (CorePage) object;
        return Selenide.page(corePage).initialize();
    }

}