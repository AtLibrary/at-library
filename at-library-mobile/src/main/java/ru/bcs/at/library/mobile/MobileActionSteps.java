/*
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

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;
import ru.bcs.at.library.core.setup.AtCoreConfig;
import ru.bcs.at.library.mobile.utils.CustomMethods;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.codeborne.selenide.Selenide.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.steps.OtherSteps.*;
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

@Log4j2
public class MobileActionSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>На экране происходит click по заданному элементу
     *
     * @param elementName название кнопки|поля|блока
     *                    </p>
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радокнопку|текст|элемент) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * <p>Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)</p>
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радокнопку|текст|элемент) с текстом \"(.*)\"$")
    public void findElement(String text) {
        By xpath = By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)));
        WebElement element = WebDriverRunner.getWebDriver().findElement(xpath);

        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Выполняется нажатие на кнопку и подгружается указанный файл
     * Селектор кнопки должны быть строго на input элемента
     * Можно указать путь до файла. Например, src/test/resources/example.pdf</p>
     */
    @И("^выполнено нажатие на кнопку \"([^\"]*)\" и загружен файл \"([^\"]*)\"$")
    public void clickOnButtonAndUploadFile(String buttonName, String fileName) {
        String file = loadValueFromFileOrPropertyOrVariableOrDefault(fileName);
        File attachmentFile = new File(file);
        coreScenario.getCurrentPage().getElement(buttonName).uploadFile(attachmentFile);
    }

    /**
     * <p>Клик по заданному элементу в блоке
     *
     * @param elementName имя элемента
     * @param blockName   имя блока
     *
     *                    </p>
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|блок|чекбокс|радокнопку|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        WebElement element = getWebElementInBlockCurrentPage(blockName, elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * <p>Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить</p>
     */
    @То("^в поле \"([^\"]*)\" введено значение$")
    @И("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        cleanField(elementName);
        element.sendKeys(value);
    }


    /**
     * <p>Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке</p>
     */
    @То("^в поле \"([^\"]*)\" дописывается значение$")
    @И("^в поле \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        WebElement element = getWebElementInCurrentPage(elementName);
        element.sendKeys(value);
    }

    /**
     * <p>Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy</p>
     */
    @И("^поле \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
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
     * <p>Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины</p>
     */
    @И("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
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
     * <p>Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную</p>
     */
    @И("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
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
     * <p>Ввод в поле случайной последовательности цифр задаваемой длины</p>
     */
    @И("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры)$")
    public void inputRandomNumSequence(String elementName, int seqLength) {
        WebElement element = getWebElementInCurrentPage(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        element.sendKeys(numSeq);
        coreScenario.write(String.format("В поле [%s] введено значение [%s]", elementName, numSeq));
    }

    /**
     * <p>Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную</p>
     */
    @И("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
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
     * <p>Очищается заданное поле</p>
     */
    @И("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String elementName) {
        WebElement element = getWebElementInCurrentPage(elementName);
        driverWait().until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
    }

    /**
     * <p>Свайп на экране мобильного устройства</p>
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
     * <p>Скроллит экран до нужного элемента, имеющегося на экране, но видимого только в нижней/верхней части экрана.</p>
     */
    @И("^экран свайпается \"(UP|DOWN|LEFT|RIGHT)\" до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String direction, String elementName) {
        String platform = AtCoreConfig.platformName.toLowerCase();
        WebElement element = null;

        for (int i = 1; i <= DEFAULT_SWIPE_NUMBER; i++) {
            try {
                element = getWebElementInCurrentPage(elementName);
            } catch (NoSuchElementException ex) {
                coreScenario.write("Элемент: \"" + elementName + "\n не найден на экране. Будет сделан SWIPE №" + i);
            }

            if (platform.equals("android")) {
                if (element != null) {
                    break;
                }
            }

            if (platform.equals("ios")) {
                if (element != null && element.isDisplayed()) {
                    break;
                }
            }
            CustomMethods.swipe(direction);
        }
        driverWait().until(visibilityOf(element));

        sleep(1000L);
    }

}
