/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.mobile;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static ru.bcs.at.library.core.cucumber.api.CoreScenario.sleep;
import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.bcs.at.library.mobile.MobileTestConfig.*;

/**
 * <h1>MOBILE шаги</h1>
 *
 * <p>Объект coreScenario используется как хранилище переменных.
 * Для сохранения/изъятия переменных используются методы setVar/getVar
 * <p>
 * Каждый экран, с которым предполагается взаимодействие, должен быть описан в соответствующем классе наследующем CorePage.
 * Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать в шагах элемент по имени, а не по селектору.
 * Селекторы следует хранить только в классе экрана, не в степах, в степах - взаимодействие по имени элемента</p>
 */

public class MobileCheckSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Проверка того, что все элементы, которые описаны в классе экрана с аннотацией @Name,
     * но без аннотации @Optional появились на экране
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам</p>
     *
     * @param nameOfPage название экран|блок|форма
     */
    @И("^(?:экран|блок|форма) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        coreScenario.getCurrentPage().appeared();
        //TODO нужно чтоб успел загрузится экран
        sleep(1);
    }

    /**
     * <p>Проверка появления элемента(не списка) на экране в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отображается на экране$")
    public void elemIsPresentedOnPage(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        AssertMobile.display(element, true, DEFAULT_TIMEOUT);
    }

    /**
     * <p>Проверка появления элемента(не списка) на экране в течение
     * заданного количества секунд
     *
     * @param elementName название кнопки|поля|блока
     * @param seconds     количество секунд
     *                    </p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" отобразился на экране в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        WebElement element = getWebElementInCurrentPage(elementName);
        AssertMobile.display(element, true, seconds);
    }

    /**
     * <p>Проверка того, что элемент исчезнет со экрана (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^ожидается исчезновение (?:кнопки|ссылки|поля|блока|чекбокса|радиокнопки|текста|элемента) \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        AssertMobile.display(element, false, DEFAULT_TIMEOUT);
    }

    /**
     * <p>Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)</p>
     */
    @То("^значение (?:поля|элемента|текста) \"([^\"]*)\" равно$")
    @И("^значение (?:поля|элемента|текста) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        WebElement element = getWebElementInCurrentPage(elementName);
        AssertMobile.expectedText(element, expectedValue, DEFAULT_TIMEOUT);
    }

    /**
     * <p>Проверка, что поле для ввода пусто</p>
     */
    @И("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        String expectedValue = "";
        compareValInFieldAndFromStep(elementName, expectedValue);
    }

    /**
     * <p>Сохранение значения элемента в переменную</p>
     */
    @И("^значение (?:поля|элемента|текста)  \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        String elementText = element.getText();
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p>Проверка, что элемент на экране кликабелен</p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельна$")
    public void clickableField(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(elementToBeClickable(element));
    }

    /**
     * <p>Проверка, что элемент на экране кликабелен</p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait(second).until(elementToBeClickable(element));
    }

    /**
     * <p>Проверка, что кнопка/ссылка недоступна для нажатия</p>
     */
    @И("^(?:кнопка|ссылка|поле|блок|чекбокс|радиокнопа|текст|элемент) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementToBeClickable(element)));
    }

    /**
     * <p>Проверка, что радиокнопка выбрана</p>
     */
    @И("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementSelectionStateToBe(element, true)));
    }

    /**
     * <p>Проверка, что радиокнопка не выбрана</p>
     */
    @И("^радиокнопка \"([^\"]*)\" не выбрана$")
    public void radioButtonIsNotSelected(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementSelectionStateToBe(element, false)));
    }

    /**
     * <p>Проверка, что поле нередактируемо</p>
     */
    @И("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        assertTrue(String.format("Элемент [%s] доступен для редактирования", elementName), element.isDisplayed());
    }

    /**
     * <p>Производится проверка количества символов в поле со значением, указанным в шаге</p>
     */
    @И("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        int length = getWebElementInCurrentPage(elementName).getText().length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }

    /**
     * <p>Получение текста элемента в блоке и сохранение его в переменную
     *
     * @param elementName  имя элемента
     * @param blockName    имя блока
     * @param variableName имя переменной
     *                     </p>
     */
    @И("^значение (?:элемента|поля|текста) \"([^\"]*)\" в блоке \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveTextElementInBlock(String elementName, String blockName, String variableName) {
        WebElement element = getWebElementInBlockCurrentPage(blockName, elementName);
        driverWait().until(ExpectedConditions.visibilityOf(element));

        String elementText = element.getText();

        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p>Проверка того, что значение из поля в блоке совпадает со значением заданной переменной из хранилища</p>
     *
     * @param elementName   имя элемента
     * @param blockName     имя блока
     * @param expectedValue имя переменной
     */
    @И("^значение (?:поля|элемента|текста) \"([^\"]*)\" в блоке \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String blockName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        WebElement element = getWebElementInBlockCurrentPage(blockName, elementName);
        driverWait().until(ExpectedConditions.textToBePresentInElement(element, expectedValue));
    }

}
