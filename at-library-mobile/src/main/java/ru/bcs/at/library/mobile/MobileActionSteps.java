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
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.bcs.at.library.core.steps.OtherSteps.getRandCharSequence;
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

public class MobileActionSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p style="color: green; font-size: 1.5em">
     * На странице происходит click по заданному элементу
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
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
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.sendKeys(value);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Очищается заданное поле
     * </p>
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     * </p>
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = getWebElementInCurrentPage(elementName);
        element.sendKeys(value);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @И("^выполнено нажатие на элемент с текстом \"(.*)\"$")
    public void findElement(String text) {
        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     * </p>
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
    public void currentDate(String elementName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            coreScenario.write("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }

        WebElement element = getWebElementInCurrentPage(elementName);

        element.clear();
        element.sendKeys(currentStringDate);
        coreScenario.write("Текущая дата " + currentStringDate);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     * </p>
     */
    @Тогда("^страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);

        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, int seqLength, String lang) {
        WebElement element = getWebElementInCurrentPage(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        element.sendKeys(charSeq);
        coreScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, int seqLength, String lang, String varName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        element.sendKeys(charSeq);
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
        WebElement element = getWebElementInCurrentPage(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        element.sendKeys(numSeq);
        coreScenario.write(String.format("В поле [%s] введено значение [%s]", elementName, numSeq));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String elementName, int seqLength, String varName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        element.sendKeys(numSeq);
        coreScenario.setVar(varName, numSeq);
        coreScenario.write(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                elementName, numSeq, varName));
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
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     * </p>
     */
    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String elementName) {
//        WebElement el = null;
//        do {
//            el = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
//            if (el.exists()) {
//                break;
//            }
//            executeJavaScript("return window.scrollBy(0, 250);");
//            sleep(1000);
//        } while (!atBottom());
//        el.shouldHave(enabled);
        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Скроллит страницу вниз до появления элемента с текстом из property файла, из переменной сценария или указанному в шаге каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     * </p>
     */
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
//        WebElement el = null;
//        do {
//            el = $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))));
//            if (el.exists()) {
//                break;
//            }
//            executeJavaScript("return window.scrollBy(0, 250);");
//            sleep(1000);
//        } while (!atBottom());
//        el.shouldHave(enabled);
        throw new cucumber.api.PendingException("шаг не реализован");
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
//        coreScenario.getCurrentPage().getBlock(blockName)
//                .getElement(elementName).getWrappedElement()
//                .click();
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

}