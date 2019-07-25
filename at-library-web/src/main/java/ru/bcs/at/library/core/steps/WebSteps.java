/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p style="color: green; font-size: 1.5em">
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p style="color: green; font-size: 1.5em">
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementShould;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.*;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveVars;
import static ru.bcs.at.library.core.steps.OtherSteps.*;


/**
 * <h1 style="color: green; font-size: 2.2em">
 * WEB шаги
 * </h1>
 *
 * <p style="color: green; font-size: 1.5em">
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.</p>
 */

//@Log4j2
public class WebSteps {

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p style="color: green; font-size: 1.5em">
     * На странице происходит click по заданному элементу
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок|ссылка|чекбокс) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * На странице происходит click по заданному элементу
     * Если элемент Displayed то будет попытка click по родительскому элементу
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^выполнено умное нажатие на (?:кнопку|поле|блок|ссылка|чекбокс) \"([^\"]*)\"$")
    public void clickElementOrParent(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        if (element.isDisplayed()) {
            element.click();
        } else {
            element.parent().click();
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @Тогда("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     *
     * @param elementName название кнопки|поля|блока
     * @param seconds     количество секунд
     *                    </p>
     */
    @Тогда("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        coreScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, coreScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка появления списка на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название элемента
     *                    </p>
     */
    @Тогда("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElementsList(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @Тогда("^ожидается исчезновение элемента \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам
     * </p>
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        if (isIE()) {
            coreScenario.getCurrentPage().ieAppeared();
        } else {
            coreScenario.getCurrentPage().appeared();
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional, не появились на странице
     * </p>
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" не (?:загрузилась|загрузился)$")
    public void loadPageFailed(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        if (isIE()) {
            coreScenario.getCurrentPage().ieDisappeared();
        } else {
            coreScenario.getCurrentPage().disappeared();
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
     * </p>
     *
     * @param elementName  название поля|элемента
     * @param variableName имя переменной
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        String expectedValue = coreScenario.getVar(variableName).toString();
        element.shouldHave(exactText(expectedValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля содержится в списке,
     * полученном из хранилища переменных по заданному ключу
     * </p>
     *
     * @param variableListName имя переменной
     * @param elementName      имя :поля|элемента
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\"$")
    public void checkIfListContainsValueFromField(String variableListName, String elementName) {
        String actualValue = coreScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) coreScenario.getVar(variableListName));
        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", variableListName, elementName),
                listFromVariable.contains(actualValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     * </p>
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, resolveVars(urlOrName));
        coreScenario.write(" url = " + address);
        open(address);
        loadPage(pageName);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется переход по заданной ссылке в новой вкладке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     * </p>
     */
    @И("^совершен переход на страницу \"([^\"]*)\" в новой вкладке по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLinkNewTab(String pageName, String urlOrName) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(urlOrName));
        coreScenario.write(" url = " + url);
        ((JavascriptExecutor) WebDriverRunner.getWebDriver())
                .executeScript("window.open('" + url + "','_blank');");
        loadPage(pageName);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что блок исчез/стал невидимым
     * </p>
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            coreScenario.getPage(nameOfPage).ieDisappeared();
        } else coreScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Эмулирует нажатие клавиш на клавиатуре
     * </p>
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        switchTo().activeElement().sendKeys(key);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @param keyNames название клавиши
     *                 Эмулирует нажатие сочетания клавиш на клавиатуре.
     *                 Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     *                 | CONTROL |
     *                 | a       |
     *                 </p>
     */
    @И("^выполнено нажатие на сочетание клавиш из таблицы$")
    public void pressKeyCombination(List<String> keyNames) {
        Iterable<CharSequence> listKeys = keyNames.stream()
                .map(this::getKeyOrCharacter)
                .collect(Collectors.toList());
        String combination = Keys.chord(listKeys);
        switchTo().activeElement().sendKeys(combination);
    }

    private CharSequence getKeyOrCharacter(String key) {
        try {
            return Keys.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return key;
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.setValue(value);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Набирается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" набирается значение \"(.*)\"$")
    public void sendKeys(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.sendKeys(value);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Набирается значение посимвольно (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     * </p>
     */
    @Тогда("^в поле \"([^\"]*)\" посимвольно набирается значение \"([^\"]*)\"$")
    public void sendKeysCharacterByCharacter(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        valueInput.clear();

        WebDriver webDriver = WebDriverRunner.getWebDriver();

        sleep(200);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", valueInput);
        sleep(200);
        valueInput.sendKeys(Keys.CONTROL, "a");
        sleep(200);
        valueInput.sendKeys(Keys.DELETE);
        sleep(200);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", valueInput);
        sleep(200);
        valueInput.clear();

        valueInput.sendKeys(Keys.CONTROL, "a");
        for (char character : value.toCharArray()) {
            valueInput.sendKeys(String.valueOf(character));
            sleep(100);
        }
        sleep(200);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Очищается заданное поле
     * </p>
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(nameOfField);
        valueInput.clear();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле для ввода пусто
     * </p>
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        coreScenario.getCurrentPage().getElement(fieldName)
                .shouldHave(empty);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод getText()
     * </p>
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = coreScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     * </p>
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов таблицы$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = coreScenario.getCurrentPage().getAnyElementsListInnerTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке %s: %s не совпадают с ожидаемыми значениями из таблицы %s", listName, actualValues, textTable),
                actualValues.containsAll(textTable));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim())
                .collect(toList());
        listOfElementsFromPage.stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsText)))
                .click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        listOfElementsFromPage.stream()
                .filter(element -> element.getText().trim().toLowerCase().contains(value.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsListText)))
                .click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * Для получения текста из элементов списка используется метод innerText()
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" на странице совпадает со списком \"([^\"]*)\"$")
    public void checkListInnerTextCorrespondsToListFromVariable(String listName, String listVariable) {
        List<String> expectedList = new ArrayList<>((List<String>) coreScenario.getVar(listVariable));
        List<String> actualList = new ArrayList<>(coreScenario.getCurrentPage().getAnyElementsListInnerTexts(listName));
        assertThat(String.format("Количество элементов списка %s = %s, ожидаемое значение = %s", listName, actualList.size(), expectedList.size()), actualList,
                hasSize(expectedList.size()));
        assertThat(String.format("Список со страницы %s: %s не совпадает с ожидаемым списком из переменной %s:%s", listName, actualList, listVariable, expectedList)
                , actualList, containsInAnyOrder(expectedList.toArray()));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сохранение значения элемента в переменную
     * </p>
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        coreScenario.setVar(variableName, coreScenario.getCurrentPage().getAnyElementText(elementName));
        coreScenario.write("Значение [" + coreScenario.getCurrentPage().getAnyElementText(elementName) + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Переход на страницу по клику и проверка, что страница загружена
     * </p>
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        coreScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        coreScenario.write(" url = " + url());
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Шаг авторизации.
     * Для того, чтобы шаг работал, на текущей странице должны быть указаны элементы
     * со значениями аннотации @Name:
     * "Логин" - для поля ввода логина,
     * "Пароль" - для поля ввода пароля и
     * "Войти" - для кнопки входа.
     * Также должны быть указаны логин и пароль в файле application.properties.
     * Например для шага: "Пусть пользователь user ввел логин и пароль"
     * логин и пароль должны быть указаны со следующими ключами:
     * user.login - для логина и
     * user.password - для пароля
     * </p>
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        coreScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        coreScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        coreScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется наведение курсора на элемент
     * </p>
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = coreScenario.getCurrentPage().getElement(elementName);
        field.hover();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что элемент не отображается на странице
     * </p>
     */
    @Тогда("^(?:поле|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elementName) {
        coreScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), DEFAULT_TIMEOUT, coreScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(enabled);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.waitUntil(enabled, second * 1000);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);
        currentElement.shouldHave(attribute(attribute, expectedAttributeValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент содержит указанный класс (в приоритете: из property, из переменной сценария, значение аргумента)
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     * </p>
     */
    @Тогда("^элемент \"([^\"]*)\" содержит класс со значением \"(.*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);

        currentElement.shouldHave(
                attribute("class", expectedClassValue)
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент не содержит указанный класс
     * </p>
     */
    @Тогда("^элемент \"([^\"]*)\" не содержит класс со значением \"(.*)\"$")
    public void checkElemClassNotContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = coreScenario.getCurrentPage().getElement(elementName);

        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);

        currentElement.shouldHave(
                not(attribute("class", expectedClassValue))
        );
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле содержит значение (в приоритете: из property, из переменной сценария, значение аргумента),
     * указанное в шаге
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        coreScenario.getCurrentPage().getElement(elementName)
                .shouldHave(text(expectedValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Используется метод innerText(), который получает как видимый, так и скрытый текст из элемента,
     * обрезая перенос строк и пробелы в конце и начале строчки.
     * Не чувствителен к регистру
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит внутренний текст \"(.*)\"$")
    public void testFieldContainsInnerText(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.shouldHave(text(expectedValue));
        } catch (ElementShould ex) {
            element.shouldHave(value(expectedValue));
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.shouldHave(text(expectedValue));
        } catch (ElementShould ex) {
            element.shouldHave(value(expectedValue));
        }
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что кнопка/ссылка недоступна для нажатия
     * </p>
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка выбрана
     * </p>
     */
    @Тогда("^(?:радиокнопка|чекбокс) \"([^\"]*)\" (?:выбрана$|выбран)")
    public void radioButtonIsSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(selected);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка не выбрана
     * </p>
     */
    @Тогда("^(?:радиокнопка|чекбокс) \"([^\"]*)\" не (?:выбрана$|выбран)")
    public void radioButtonIsNotSelected(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.shouldHave(not(selected));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле нередактируемо
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] доступен для редактирования", elementName), element.is(Condition.disabled));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) coreScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(coreScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), actualList, equalTo(expectedList));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     * </p>
     */
    @Тогда("^открыта read-only форма$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue("Форма не read-only. Количество input-полей: " + inputsCount, inputsCount == 0);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue("Форма не read-only. Количество элементов textarea: " + textareasCount, textareasCount == 0);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
                .filter(SelenideElement::isDisplayed)
                .collect(Collectors.toList());
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     * </p>
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement field = coreScenario.getCurrentPage().getElement(elementName);
        String oldValue = field.getValue();
        if (oldValue.isEmpty()) {
            oldValue = field.getText();
        }
        field.setValue("");
        field.setValue(oldValue + value);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @И("^выполнено нажатие на элемент с текстом \"(.*)\"$")
    public void findElement(String text) {
        $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)))).click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     * </p>
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
    public void currentDate(String fieldName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            coreScenario.write("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
        coreScenario.write("Текущая дата " + currentStringDate);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле указанного текста (в приоритете: из property, из переменной сценария, значение аргумента),
     * используя буфер обмена и клавиши SHIFT + INSERT
     * </p>
     */
    @Когда("^вставлено значение \"([^\"]*)\" в элемент \"([^\"]*)\" с помощью горячих клавиш$")
    public void pasteValueToTextField(String value, String fieldName) {
        value = getPropertyOrStringVariableOrValue(value);
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        coreScenario.getCurrentPage().getElement(fieldName).sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     * </p>
     */
    @Тогда("^файл \"(.*)\" загрузился в папку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым названием, содержащим текст [%s]", fileName),
                expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     * </p>
     */
    @Тогда("^файл по пути \"(.*)\" выгрузился в поле \"(.*)\"$")
    public void uploadFile(String path, String fieldName) {
        coreScenario.getCurrentPage()
                .getElement(fieldName)
                .uploadFile(new File(path));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     * </p>
     */
    @Тогда("^страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String elementName) {
        coreScenario.getCurrentPage().getElement(elementName).scrollTo();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы любого случайного элемента
     * </p>
     */
    @Тогда("^выбран любой элемент в списке \"([^\"]*)\"$")
    public void selectRandomElementFromList(String listName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()))
                .shouldBe(Condition.visible).click();
        coreScenario.write("Выбран случайный элемент: " + listOfElementsFromPage);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     * </p>
     */
    @Когда("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()));
        element.shouldBe(Condition.visible).click();
        coreScenario.setVar(varName, coreScenario.getCurrentPage().getAnyElementText(element).trim());
        coreScenario.write(format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
                coreScenario.getVar(varName), listName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     * </p>
     */
    @Тогда("^выбран (\\d+)-й элемент в списке \"([^\"]*)\"$")
    public void selectElementNumberFromList(Integer elementNumber, String listName) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        SelenideElement elementToSelect;
        Integer selectedElementNumber = elementNumber - 1;
        if (selectedElementNumber < 0 || selectedElementNumber >= listOfElementsFromPage.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("В списке %s нет элемента с номером %s. Количество элементов списка = %s",
                            listName, elementNumber, listOfElementsFromPage.size()));
        }
        elementToSelect = listOfElementsFromPage.get(selectedElementNumber);
        elementToSelect.shouldBe(Condition.visible).click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     * Не чувствителен к регистру
     * </p>
     */
    @Тогда("^элементы списка \"([^\"]*)\" содержат текст \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertTrue(String.format("Элемены списка %s: [%s] не содержат текст [%s] ", listName, elementsListText, value),
                elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     * </p>
     */
    @Тогда("^элементы списка \"([^\"]*)\" не содержат текст \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertFalse(String.format("Элемены списка %s: [%s] содержат текст [%s] ", listName, elementsListText, value),
                elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, int seqLength, String lang) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        valueInput.setValue(charSeq);
        coreScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, int seqLength, String lang, String varName) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        valueInput.setValue(charSeq);
        coreScenario.setVar(varName, charSeq);
        coreScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности цифр задаваемой длины
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры)$")
    public void inputRandomNumSequence(String elementName, int seqLength) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        valueInput.setValue(numSeq);
        coreScenario.write(String.format("В поле [%s] введено значение [%s]", elementName, numSeq));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String elementName, int seqLength, String varName) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        valueInput.setValue(numSeq);
        coreScenario.setVar(varName, numSeq);
        coreScenario.write(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                elementName, numSeq, varName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проход по списку и проверка текста у элемента на соответствие формату регулярного выражения
     * </p>
     */
    @И("элементы списка \"([^\"]*)\" соответствуют формату \"([^\"]*)\"$")
    public void checkListTextsByRegExp(String listName, String pattern) {
        coreScenario.getCurrentPage().getElementsList(listName).forEach(element -> {
            String str = coreScenario.getCurrentPage().getAnyElementText(element);
            assertTrue(format("Текст '%s' из списка '%s' не соответствует формату регулярного выражения", str, listName),
                    isTextMatches(str, pattern));
        });
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется запуск js-скрипта с указанием в js.executeScript его логики
     * Скрипт можно передать как аргумент метода или значение из application.properties
     * </p>
     */
    @Когда("^выполнен js-скрипт \"([^\"]*)\"")
    public void executeJsScript(String scriptName) {
        String content = loadValueFromFileOrPropertyOrVariableOrDefault(scriptName);
        Selenide.executeJavaScript(content);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка количества символов в поле со значением, указанным в шаге
     * </p>
     */
    @Тогда("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String element, int num) {
        int length = coreScenario.getCurrentPage().getAnyElementText(element).length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (\\d+) (?:элемент|элементов|элемента)")
    public void listContainsNumberOfElements(String listName, int quantity) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        assertTrue(String.format("Число элементов в списке отличается от ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() == quantity);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка соответствия числа элементов списка значению из property файла, из переменной сценария или указанному в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится количество элементов, равное значению из переменной \"([^\"]*)\"")
    public void listContainsNumberFromVariable(String listName, String quantity) {
        int numberOfElements = Integer.parseInt(getPropertyOrStringVariableOrValue(quantity));
        listContainsNumberOfElements(listName, numberOfElements);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     * </p>
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
        List<SelenideElement> listOfElementsFromPage = coreScenario.getCurrentPage().getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            assertTrue(String.format("Число элементов списка меньше ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() > quantity);
        } else
            assertTrue(String.format("Число элементов списка превышает ожидаемое: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() < quantity);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     * </p>
     */
    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String elementName) {
        SelenideElement el = null;
        do {
            el = coreScenario.getCurrentPage().getElement(elementName);
            if (el.exists()) {
                break;
            }
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
        el.shouldHave(enabled);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Скроллит страницу вниз до появления элемента с текстом из property файла, из переменной сценария или указанному в шаге каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     * </p>
     */
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
        SelenideElement el = null;
        do {
            el = $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))));
            if (el.exists()) {
                break;
            }
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
        el.shouldHave(enabled);
    }

    /**
     * Проверка совпадения значения из переменной и значения из property
     * </p>
     */
    @Тогда("^значения из переменной \"([^\"]*)\" и из property файла \"([^\"]*)\" совпадают$")
    public void checkIfValueFromVariableEqualPropertyVariable(String envVarible, String propertyVariable) {
        assertThat("Переменные " + envVarible + " и " + propertyVariable + " не совпадают",
                (String) coreScenario.getVar(envVarible), equalToIgnoringCase(loadProperty(propertyVariable)));
    }

    /**
     * Выполняется нажатие на кнопку и подгружается указанный файл
     * Селектор кнопки должны быть строго на input элемента
     * Можно указать путь до файла. Например, src/test/resources/example.pdf
     * </p>
     */
    @Когда("^выполнено нажатие на кнопку \"([^\"]*)\" и загружен файл \"([^\"]*)\"$")
    public void clickOnButtonAndUploadFile(String buttonName, String fileName) {
        String file = loadValueFromFileOrPropertyOrVariableOrDefault(fileName);
        File attachmentFile = new File(file);
        coreScenario.getCurrentPage().getElement(buttonName).uploadFile(attachmentFile);
    }

    /**
     * Выполняется чтение файла с шаблоном и заполнение его значениями из таблицы
     * </p>
     */
    @И("^шаблон \"([^\"]*)\" заполнен данными из таблицы и сохранён в переменную \"([^\"]*)\"$")
    public void fillTemplate(String templateName, String varName, DataTable table) {
        String template = loadValueFromFileOrPropertyOrVariableOrDefault(templateName);
        boolean error = false;
        for (List<String> list : table.asLists()) {
            String regexp = list.get(0);
            String replacement = list.get(1);
            if (template.contains(regexp)) {
                template = template.replaceAll(regexp, replacement);
            } else {
                coreScenario.write("В шаблоне не найден элемент " + regexp);
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("В шаблоне не найдены требуемые регулярные выражения");
        coreScenario.setVar(varName, template);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Клик по заданному элементу в блоке
     *
     * @param elementName имя элемента
     * @param blockName   имя блока
     *
     *                    </p>
     */
    @И("^выполнено нажатие на (?:кнопку|поле) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName).click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Получение текста элемента в блоке и сохранение его в переменную
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     *                     </p>
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveTextElementInBlock(String elementName, String blockName, String variableName) {
        String elementText = coreScenario.getCurrentPage().getBlock(blockName).getAnyElementText(elementName);
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка того, что значение из поля в блоке совпадает со значением заданной переменной из хранилища
     * </p>
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" в блоке \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String blockName, String variableName) {
        String expectedValue = coreScenario.getVar(variableName).toString();
        coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName)
                .shouldHave(exactText(expectedValue));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * </p>
     *
     * @param blockName имя блока
     * @param listName
     * @param varName
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен в переменную \"([^\"]*)\"$")
    public void getElementsList(String blockName, String listName, String varName) {
        coreScenario.setVar(varName, coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * </p>
     *
     * @param blockName имя блока
     * @param listName
     * @param varName
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен в переменную \"([^\"]*)\"$")
    public void clickCheckbox(String blockName, String listName, String varName) {
        coreScenario.setVar(varName, coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * </p>
     *
     * @param blockName имя блока
     * @param listName
     * @param varName
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен текст в переменную \"([^\"]*)\"$")
    public void getListElementsText(String blockName, String listName, String varName) {
        coreScenario.setVar(varName,
                coreScenario.getCurrentPage()
                        .getBlock(blockName)
                        .getElementsList(listName)
                        .stream()
                        .map(SelenideElement::getText)
                        .collect(Collectors.toList()));
    }
}
