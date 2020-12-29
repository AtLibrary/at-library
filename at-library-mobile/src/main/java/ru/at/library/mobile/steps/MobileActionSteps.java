/*
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
package ru.at.library.mobile.steps;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.То;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.at.library.core.cucumber.api.CoreScenario;
import ru.at.library.mobile.utils.CustomMethods;
import ru.at.library.mobile.utils.MobileTestConfig;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static ru.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.at.library.core.steps.OtherSteps.*;


/**
 * MOBILE шаги
 * <p>
 * Объект coreScenario используется как хранилище переменных.
 * Для сохранения/изъятия переменных используются методы setVar/getVar
 * <p>
 * Каждый экран, с которым предполагается взаимодействие, должен быть описан в соответствующем классе наследующем CorePage.
 * Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать в шагах элемент по имени, а не по селектору.
 * Селекторы следует хранить только в классе экрана, не в степах, в степах - взаимодействие по имени элемента
 */

@Log4j2
public class MobileActionSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * На экране происходит click по заданному элементу, проверяя наличие кнопки|поля|блока на текущей странице
     *
     * @param elementName название кнопки|поля|блока
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) \"([^\"]+)\" если отображается$")
    public void clickOnElementIfDisplayed(String elementName) {
        if (MobileTestConfig.isDisplayedSelenideElementInCurrentPage(elementName)) {
            clickOnElement(elementName);
        }
    }

    /**
     * На экране происходит click по заданному элементу
     *
     * @param elementName название кнопки|поля|блока
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) \"([^\"]+)\"$")
    public void clickOnElement(String elementName) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * На экране происходит click по заданному элементу с указанием %% по высоте и ширине элемента
     *
     * @param elementName название кнопки|поля|блока
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) \"([^\"]+)\" (\\d+)% по высоте, (\\d+)% по ширине$")
    public void clickOnElementByPercents(String elementName, Integer heightPercent, Integer widthPercent) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));

        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(element).moveByOffset(-width / 2 + (int) (width * widthPercent * 0.01), -height / 2 + (int) (height * heightPercent * 0.01)).click().perform();
    }

    /**
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента),
     * проверяя наличие кнопки|поля|блока на текущей странице
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) с текстом \"([^\"]+)\" если отображается$")
    public void findElementIfDisplayed(String text) {
        if (MobileTestConfig.isDisplayedSelenideElementInCurrentPage(text)) {
            findElement(text);
        }
    }

    /**
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) с текстом \"([^\"]+)\"$")
    public void findElement(String text) {
        By xpath = By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)));
        WebElement element = WebDriverRunner.getWebDriver().findElement(xpath);

        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Выполняется нажатие на кнопку и подгружается указанный файл
     * Селектор кнопки должны быть строго на input элемента
     * Можно указать путь до файла. Например, src/test/resources/example.pdf
     */
    @И("^выполнено нажатие на кнопку \"([^\"]+)\" и загружен файл \"([^\"]+)\"$")
    public void clickOnButtonAndUploadFile(String buttonName, String fileName) {
        String file = loadValueFromFileOrPropertyOrVariableOrDefault(fileName);
        File attachmentFile = new File(file);
        coreScenario.getCurrentPage().getElement(buttonName).uploadFile(attachmentFile);
    }

    /**
     * Клик по заданному элементу в блоке
     *
     * @param elementName имя элемента
     * @param blockName   имя блока
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радиокнопку|текст|элемент) \"([^\"]+)\" в блоке \"([^\"]+)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        WebElement element = MobileTestConfig.getWebElementInBlockCurrentPage(blockName, elementName);
        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     */
    @То("^в поле \"([^\"]+)\" введено значение$")
    @И("^в поле \"([^\"]+)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));
        cleanField(elementName);
        element.sendKeys(value);
    }


    /**
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     */
    @То("^в поле \"([^\"]+)\" дописывается значение$")
    @И("^в поле \"([^\"]+)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        element.sendKeys(value);
    }

    /**
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @И("^поле \"([^\"]+)\" заполняется текущей датой в формате \"([^\"]+)\"$")
    public void currentDate(String elementName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.trace("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }

        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);

        element.clear();
        element.sendKeys(currentStringDate);
        log.trace("Текущая дата " + currentStringDate);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     */
    @И("^в поле \"([^\"]+)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, int seqLength, String lang) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        element.sendKeys(charSeq);
        log.trace("Строка случайных символов равна :" + charSeq);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     */
    @И("^в поле \"([^\"]+)\" введено (\\d+) случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]+)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, int seqLength, String lang, String varName) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        element.sendKeys(charSeq);
        coreScenario.setVar(varName, charSeq);
        log.trace("Строка случайных символов равна :" + charSeq);
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины
     */
    @И("^в поле \"([^\"]+)\" введено случайное число из (\\d+) (?:цифр|цифры)$")
    public void inputRandomNumSequence(String elementName, int seqLength) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        element.sendKeys(numSeq);
        log.trace(String.format("В поле [%s] введено значение [%s]", elementName, numSeq));
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     */
    @И("^в поле \"([^\"]+)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]+)\"$")
    public void inputAndSetRandomNumSequence(String elementName, int seqLength, String varName) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);

        element.sendKeys(numSeq);
        coreScenario.setVar(varName, numSeq);
        log.trace(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                elementName, numSeq, varName));
    }

    /**
     * Очищается заданное поле
     */
    @И("^очищено поле \"([^\"]+)\"$")
    public void cleanField(String elementName) {
        WebElement element = MobileTestConfig.getWebElementInCurrentPage(elementName);
        MobileTestConfig.driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
    }

    /**
     * Свайп на экране мобильного устройства
     */
    @И("^выполнен свайп \"(UP|DOWN|LEFT|RIGHT)\"(?: (\\d+)-(\\d+)%%|)(?: по (\\d+)%|)$")
    public void swipe(String direction, Integer startPercent, Integer endPercent, Integer otherAxisPercent) {
        if (startPercent != null && endPercent != null) {
            if ((direction.equals("UP") || direction.equals("LEFT")) && startPercent < endPercent ||
                    (direction.equals("DOWN") || direction.equals("RIGHT")) && startPercent > endPercent) {
                log.warn(String.format("%d-%d%% не соответствует направлению свайпа %s, " +
                                "будет выполнен свайп %d-%d%%",
                        startPercent, endPercent, direction, endPercent, startPercent));
            }

            if (startPercent > endPercent) {
                Integer temp = startPercent;
                startPercent = endPercent;
                endPercent = temp;
            }
        }

        CustomMethods.swipe(direction, startPercent, endPercent, otherAxisPercent);

        sleep(1000L);
    }

    /**
     * Скроллит экран до нужного элемента, имеющегося на экране, но видимого только в нижней/верхней части экрана.
     */
    @И("^экран свайпается \"(UP|DOWN|LEFT|RIGHT)\" до элемента \"([^\"]+)\"")
    public void scrollPageToElement(String direction, String elementName) {
        //TODO  разобраться
//        String platform = AtCoreConfig.platformName.toLowerCase();
        WebElement element = null;

        for (int i = 1; i <= MobileTestConfig.DEFAULT_SWIPE_NUMBER; i++) {
            if (MobileTestConfig.isDisplayedSelenideElementInCurrentPage(elementName))
                element = MobileTestConfig.getWebElementInCurrentPage(elementName);
            //TODO  разобраться
//            if (platform.equals("android")) {
//                if (element != null) {
//                    break;
//                }
//            }
//        //TODO  разобраться
//            if (platform.equals("ios")) {
//                if (element != null || element != null && element.isDisplayed()) {
//                    break;
//                }
//            }
            if (direction.equals("DOWN") || direction.equals("RIGHT")) {
                swipe(direction, 50, 80, null);
            } else {
                swipe(direction, 80, 50, null);
            }
            Selenide.sleep(2000L);
        }
        MobileTestConfig.driverWait().until(visibilityOf(element));
    }

}
