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
package ru.bcs.at.library.mobile;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.bcs.at.library.mobile.MobileTestConfig.DEFAULT_TIMEOUT;

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

public class MobileCheckSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

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
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @Тогда("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(visibilityOf(element));
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
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait(seconds).until(visibilityOf(element));
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
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(visibilityOf(element)));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(textToBePresentInElementValue(element, expectedValue));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле для ввода пусто
     * </p>
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        String expectedValue = "";
        compareValInFieldAndFromStep(elementName, expectedValue);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Сохранение значения элемента в переменную
     * </p>
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        String elementText = element.getText();
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(elementToBeClickable(element));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait(second).until(elementToBeClickable(element));
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что кнопка/ссылка недоступна для нажатия
     * </p>
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementToBeClickable(element)));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка выбрана
     * </p>
     */
    @Тогда("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementSelectionStateToBe(element, true)));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка не выбрана
     * </p>
     */
    @Тогда("^радиокнопка \"([^\"]*)\" не выбрана$")
    public void radioButtonIsNotSelected(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(not(elementSelectionStateToBe(element, false)));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле нередактируемо
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Производится проверка количества символов в поле со значением, указанным в шаге
     * </p>
     */
    @Тогда("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String elementName, int num) {
        int length = getWebElementInCurrentPage(elementName).getText().length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
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
//        WebElement element = coreScenario.getCurrentPage().getBlock(blockName)
//                .getElement(elementName).getWrappedElement();
//
//        String elementText = element.getText();
//        coreScenario.setVar(variableName, elementText);
//        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
        throw new cucumber.api.PendingException("шаг не реализован");
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
//        String expectedValue = coreScenario.getVar(variableName).toString();
//        WebElement element = coreScenario.getCurrentPage().getBlock(blockName)
//                .getElement(elementName).getWrappedElement();
//
//        coreScenario.getCurrentPage().getBlock(blockName).getElement(elementName)
//                .shouldHave(exactText(expectedValue));
        throw new cucumber.api.PendingException("шаг не реализован");
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
//        coreScenario.setVar(varName, coreScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
        throw new cucumber.api.PendingException("шаг не реализован");
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
//        coreScenario.setVar(varName,
//                coreScenario.getCurrentPage()
//                        .getBlock(blockName)
//                        .getElementsList(listName)
//                        .stream()
//                        .map(WebElement::getText)
//                        .collect(Collectors.toList()));
        throw new cucumber.api.PendingException("шаг не реализован");
    }

    private WebElement getWebElementInCurrentPage(String elementName) {
        return coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
    }

    private WebDriverWait driverWait() {
        return driverWait(DEFAULT_TIMEOUT);
    }

    private WebDriverWait driverWait(int second) {
        return new WebDriverWait(WebDriverRunner.getWebDriver(), second);
    }

//    private void checkElementText(WebElement element, String expectedValue) {
//        String elementText = "";
//        for (int i = 0; i < DEFAULT_TIMEOUT; ) {
//            elementText = element.getText();
//            if (elementText.equals(expectedValue)) {
//                break;
//            }
//            i = i + 100;
//        }
//        Assert.assertEquals(expectedValue, elementText);
//    }

}
