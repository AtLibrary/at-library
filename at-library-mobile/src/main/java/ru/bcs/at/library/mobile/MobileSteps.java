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

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import io.cucumber.datatable.DataTable;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertEquals;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.*;
import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.bcs.at.library.core.steps.OtherSteps.getRandCharSequence;


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
public class MobileSteps {

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        element.click();
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
        throw new cucumber.api.PendingException("шаг не реализован");
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
        throw new cucumber.api.PendingException("шаг не реализован");
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
        throw new cucumber.api.PendingException("шаг не реализован");
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        String expectedValue = coreScenario.getVar(variableName).toString();
        String elementText = element.getText();
        Assert.assertEquals(expectedValue, elementText);
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     * </p>
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        checkInTime(elementName, expectedValue);
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        element.sendKeys(value);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Очищается заданное поле
     * </p>
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        element.clear();
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле для ввода пусто
     * </p>
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        String elementText = element.getText();
        Assert.assertEquals("", elementText);
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Сохранение значения элемента в переменную
     * </p>
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
        String elementText = element.getText();
        coreScenario.setVar(variableName, elementText);
        coreScenario.write("Значение [" + elementText + "] сохранено в переменную [" + variableName + "]");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Выполняется наведение курсора на элемент
     * </p>
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что элемент на странице кликабелен
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельнов течение (\\d+) (?:секунд|секунды)$")
    public void clickableField(String elementName, int second) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что кнопка/ссылка недоступна для нажатия
     * </p>
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
//        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка выбрана
     * </p>
     */
    @Тогда("^радиокнопка \"([^\"]*)\" выбрана$")
    public void radioButtonIsSelected(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что радиокнопка не выбрана
     * </p>
     */
    @Тогда("^радиокнопка \"([^\"]*)\" не выбрана$")
    public void radioButtonIsNotSelected(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Проверка, что поле нередактируемо
     * </p>
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }


    /**
     * <p style="color: green; font-size: 1.5em">
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     * </p>
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);

        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

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

        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();

        throw new cucumber.api.PendingException("шаг не реализован");
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     * </p>
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, int seqLength, String lang) {
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
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
        WebElement element = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement();
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
    public void checkFieldSymbolsCount(String element, int num) {
        int length = coreScenario.getCurrentPage().getElement(element).getText().length();
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
        coreScenario.getCurrentPage().getBlock(blockName)
                .getElement(elementName).getWrappedElement()
                .click();
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
        WebElement element = coreScenario.getCurrentPage().getBlock(blockName)
                .getElement(elementName).getWrappedElement();

        String elementText = element.getText();
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
                        .map(WebElement::getText)
                        .collect(Collectors.toList()));
    }

    private void checkInTime(String elementName, String expectedValue) {
        String elementText = "";
        for (int i = 0; i < DEFAULT_TIMEOUT; ) {
            elementText = coreScenario.getCurrentPage().getElement(elementName).getWrappedElement().getText();
            if (elementText.equals(expectedValue)) {
                break;
            }
            i = i + 100;
        }
        Assert.assertEquals(expectedValue, elementText);
    }

}
