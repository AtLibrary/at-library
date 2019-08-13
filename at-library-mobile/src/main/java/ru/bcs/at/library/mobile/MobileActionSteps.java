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
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;
import ru.bcs.at.library.mobile.utils.CustomMethods;

import java.io.File;
import java.text.SimpleDateFormat;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.steps.OtherSteps.*;
import static ru.bcs.at.library.mobile.MobileTestConfig.*;


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
    @И("^выполнено нажатие на (?:кнопку|поле|блок|ссылку|текст|чекбокс|радокнопку) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок|ссылку|текст|чекбокс|радокнопку) с текстом \"(.*)\"$")
    public void findElement(String text) {
        By xpath = By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)));
        WebElement element = WebDriverRunner.getWebDriver().findElement(xpath);

        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
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
    @И("^выполнено нажатие на (?:кнопку|поле|блок|ссылку|текст|чекбокс|радокнопку) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        WebElement element = getWebElementInBlockCurrentPage(blockName, elementName);
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
        cleanField(elementName);
        element.sendKeys(value);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = getWebElementInCurrentPage(elementName);
        element.sendKeys(value);
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
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     * </p>
     */
    @Тогда("^страница прокручена (UP|DOWN|LEFT|RIGHT) до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String direction, String elementName) {
        AppiumDriver driver = (AppiumDriver) WebDriverRunner.getWebDriver();
        WebElement element = null;

        for (int i = 1; i <= DEFAULT_SWIPE_NUMBER; i++) {
            try {
                element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
            } catch (NoSuchElementException ex) {
            }

            if (driver.getPlatformName().toLowerCase().equals("android")) {
                if (element != null) break;
            }

            if (driver.getPlatformName().toLowerCase().equals("ios")) {
                if (element.isDisplayed()) break;
            }

            CustomMethods.swipe(direction);
        }

        if (driver.getPlatformName().toLowerCase().equals("android"))
            if (element == null) throw new NoSuchElementException(elementName);

        if (driver.getPlatformName().toLowerCase().equals("ios"))
            if (!element.isDisplayed()) throw new NoSuchElementException(elementName);
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
//        By xpath = By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)));
//        WebElement element = WebDriverRunner.getWebDriver().findElement(xpath);
//
//        driverWait().until(ExpectedConditions.elementToBeClickable(element));
//        element.click();
//
//        AppiumDriver driver = ((AppiumDriver) WebDriverRunner.getWebDriver());

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
     * <p style="color: green; font-size: 1.5em">
     * Свайп на экране мобильного устройства
     * </p>
     */
    @И("^выполнен свайп (UP|DOWN|LEFT|RIGHT)$")
    public void swipe(String direction) {
        CustomMethods.swipe(direction);
    }
}
