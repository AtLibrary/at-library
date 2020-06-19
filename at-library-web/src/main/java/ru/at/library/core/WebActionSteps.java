package ru.at.library.core;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.То;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.url;
import static ru.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.at.library.core.cucumber.ScopedVariables.resolveVars;
import static ru.at.library.core.steps.OtherSteps.*;

/**
 * WEB шаги
 * <p>
 * В coreScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем CorePage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
public class WebActionSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlOrName) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(urlOrName));
        coreScenario.write(" url = " + url);
        open(url);
        loadPage(pageName);
    }

    /**
     * Выполняется переход по заданной ссылке в новой вкладке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" в новой вкладке по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLinkNewTab(String pageName, String urlOrName) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(urlOrName));
        coreScenario.write(" url = " + url);
        ((JavascriptExecutor) WebDriverRunner.getWebDriver())
                .executeScript("window.open('" + url + "','_blank');");
        int numberThisTab = WebDriverRunner.getWebDriver().getWindowHandles().size() - 1;
        Selenide.switchTo().window(numberThisTab);
        loadPage(pageName);
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        coreScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        coreScenario.write(" url = " + url());
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @И("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        if (isIE()) {
            coreScenario.getCurrentPage().ieAppeared();
        } else {
            coreScenario.getCurrentPage().appeared();
        }
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional, не появились на странице
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @И("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" не (?:загрузилась|загрузился)$")
    public void loadPageFailed(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        if (isIE()) {
            coreScenario.getCurrentPage().ieDisappeared();
        } else {
            coreScenario.getCurrentPage().disappeared();
        }
    }

    /**
     * На странице происходит click по заданному элементу
     *
     * @param elementName название элемента
     */
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        element.click();
    }

    /**
     * На странице происходит click по заданному элементу
     * Если элемент Displayed то будет попытка click по родительскому элементу
     *
     * @param elementName название элемента
     */
    @И("^выполнено умное нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\"$")
    public void clickElementOrParent(String elementName) {
        SelenideElement element = coreScenario.getCurrentPage().getElement(elementName);
        try {
            element.click();
        } catch (Throwable e) {
            element.parent().click();
        }
    }

    /**
     * На странице происходит hover и click по заданному элементу
     *
     * @param elementName название элемента
     */

    @И("^выполнено нажатие c ховером на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\"$")
    public void clickOnElementWithHover(String elementName) {
        CorePage currentPage = coreScenario.getCurrentPage();
        SelenideElement element = currentPage.getElement(elementName);
        element
                .hover()
                .click();
    }

    /**
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        switchTo().activeElement()
                .sendKeys(getKeyOrCharacter(buttonName));
    }

    /**
     * <p style="color: green; font-size: 1.5em">
     *
     * @param keyNames название клавиши
     *                 Эмулирует нажатие сочетания клавиш на клавиатуре.
     *                 Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     *                 | CONTROL |
     *                 | a       |
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
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     *
     * @return
     */
    @То("^в поле \"([^\"]*)\" введено значение$")
    @И("^в поле \"([^\"]*)\" введено значение \"([^\"]*)\"$")
    public String setFieldValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.setValue(value);
        return value;
    }

    /**
     * Набирается значение посимвольно (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле очищается
     */
    @То("^в поле \"([^\"]*)\" посимвольно набирается значение$")
    @И("^в поле \"([^\"]*)\" посимвольно набирается значение \"([^\"]*)\"$")
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
     * Очищается заданное поле
     */
    @И("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(nameOfField);
        valueInput.clear();
        if (valueInput.is(Condition.not(Condition.empty))) {
            valueInput.sendKeys(Keys.chord(Keys.CONTROL + "a" + Keys.BACK_SPACE));
        }
    }

    /**
     * Выполняется наведение курсора на элемент
     */
    @И("^выполнен ховер на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = coreScenario.getCurrentPage().getElement(elementName);
        field.hover();
    }

    /**
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     */
    @То("^в (?:поле|элемент) \"([^\"]*)\" дописывается значение$")
    @И("^в (?:поле|элемент) \"([^\"]*)\" дописывается значение \"([^\"]*)\"$")
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
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^выполнено нажатие на элемент с текстом \"([^\"]*)\"$")
    public void findElement(String text) {
        $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(text)))).click();
    }

    /**
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @И("^(?:поле|элемент) \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
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
     * Ввод в поле указанного текста (в приоритете: из property, из переменной сценария, значение аргумента),
     * используя буфер обмена и клавиши SHIFT + INSERT
     */
    @И("^вставлено значение \"([^\"]*)\" в (?:поле|элемент) \"([^\"]*)\" с помощью горячих клавиш$")
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
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @И("^файл по пути \"([^\"]*)\" выгрузился в поле \"([^\"]*)\"$")
    public void uploadFile(String path, String fieldName) {
        coreScenario.getCurrentPage()
                .getElement(fieldName)
                .uploadFile(new File(path));
    }

    /**
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     */
    @Deprecated
    @И("^страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String elementName) {
        coreScenario.getCurrentPage().getElement(elementName).scrollTo();
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     */
    @Deprecated
    @И("^в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице)$")
    public void setRandomCharSequence(String elementName, String seqLengthString, String lang) {
        int seqLength = Integer.parseInt(seqLengthString);
        SelenideElement valueInput = coreScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        valueInput.setValue(charSeq);
        coreScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     */
    @Deprecated
    @И("^в поле \"([^\"]*)\" введено \"([^\"]*)\" случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, String seqLengthString, String lang, String varName) {
        int seqLength = Integer.parseInt(seqLengthString);
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
     * Ввод в поле случайной последовательности цифр задаваемой длины
     *
     * @return сгенерированное число
     */
    @Deprecated
    @И("^в поле \"([^\"]*)\" введено случайное число из \"([^\"]*)\" (?:цифр|цифры)$")
    public String inputRandomNumSequence(String elementName, String seqLengthString) {
        int seqLength = Integer.parseInt(seqLengthString);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        return setFieldValue(elementName, numSeq);
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     */
    @Deprecated
    @И("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String elementName, String seqLengthString, String varName) {
        String value = inputRandomNumSequence(elementName, seqLengthString);
        coreScenario.setVar(varName, value);
        coreScenario.write(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                elementName, value, varName));
    }

    /**
     * Выполняется запуск js-скрипта с указанием в js.executeScript его логики
     * Скрипт можно передать как аргумент метода или значение из application.properties
     */
    @И("^выполнен js-скрипт \"([^\"]*)\"")
    public void executeJsScript(String scriptName) {
        String content = loadValueFromFileOrPropertyOrVariableOrDefault(scriptName);
        Selenide.executeJavaScript(content);
    }

    /**
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String elementName) {
        do {
            SelenideElement el = coreScenario.getCurrentPage().getElement(elementName);
            el.scrollTo();
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
    }

    /**
     * Скроллит страницу вниз до появления элемента с текстом из property файла, из переменной сценария или указанному в шаге каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
        SelenideElement el = $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))));
        ((JavascriptExecutor) WebDriverRunner.getWebDriver()).executeScript("arguments[0].scrollIntoView();", el);
        el.click();
    }

    /**
     * Выполняется нажатие на кнопку и подгружается указанный файл
     * Селектор кнопки должны быть строго на input элемента
     * Можно указать путь до файла. Например, src/test/resources/example.pdf
     */
    @И("^выполнено нажатие на кнопку \"([^\"]*)\" и загружен файл \"([^\"]*)\"$")
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
    @И("^выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        CorePage currentPage = coreScenario.getCurrentPage();
        coreScenario.getPage(blockName).getElement(elementName).click();
        coreScenario.setCurrentPage(currentPage);
    }

}
