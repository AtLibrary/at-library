/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Matchers;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import ru.bcs.at.library.core.cucumber.api.CoreScenario;

import java.util.Set;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadProperty;
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveVars;
import static ru.bcs.at.library.core.steps.DefaultWebSteps.getPropertyOrStringVariableOrValue;

/**
 * Шаги для работы с браузером
 */
@Log4j2
public class DefaultManageBrowserSteps {

    private DefaultWebSteps ds = new DefaultWebSteps();
    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * @author Anton Pavlov
     * Удаляем все cookies
     */
    @Когда("^cookies приложения очищены$")
    public void deleteCookies() {
        clearBrowserCookies();
    }

    /**
     * @author Anton Pavlov
     * Ищем cookie по имени. Сохраняем cookie в переменную для дальнейшего использования
     */
    @Когда("^cookie с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String cookieVar) {
        String cookieName = resolveVars(nameCookie);
        Cookie var = getWebDriver().manage().getCookieNamed(cookieName);
        coreScenario.setVar(cookieVar, var);
    }

    /**
     * @author Anton Pavlov
     * Сохраняем все cookies в переменную для дальнейшего использования
     */
    @Когда("^cookies сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String variableName) {
        Set cookies = getWebDriver().manage().getCookies();
        coreScenario.setVar(variableName, cookies);
    }

    /**
     * @author Anton Pavlov
     * Находим cookie по имени и подменяем ее значение. Имя cookie и домен не меняются
     */
    @Когда("^добавлена cookie с именем \"([^\"]*)\" и значением \"([^\"]*)\"$")
    public void replaceCookie(String cookieName, String cookieValue) {
        String nameCookie = resolveVars(cookieName);
        String valueCookie = resolveVars(cookieValue);
        getWebDriver().manage().addCookie(new Cookie(nameCookie, valueCookie));
    }

    /**
     * @author Anton Pavlov
     * Переключение на следующую вкладку браузера
     */
    @Когда("выполнено переключение на следующую вкладку")
    public void switchToTheNextTab() {
        String nextWindowHandle = nextWindowHandle();
        getWebDriver().switchTo().window(nextWindowHandle);
        coreScenario.write("Текущая вкладка " + nextWindowHandle);
    }

    /**
     * @author Anton Pavlov
     * Переключение на вкладку браузера с заголовком
     */
    @Когда("^выполнено переключение на вкладку с заголовком \"([^\"]*)\"$")
    public void switchToTheTabWithTitle(String title) {
        switchTo().window(title);
        checkPageTitle(title);
    }

    /**
     * @author Anton Pavlov
     * Производится сравнение заголовка страницы со значением, указанным в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^заголовок страницы равен \"([^\"]*)\"$")
    public void checkPageTitle(String pageTitleName) {
        pageTitleName = getPropertyOrStringVariableOrValue(pageTitleName);
        String currentTitle = getWebDriver().getTitle().trim();
        assertThat(String.format("Заголовок страницы не совпадает с ожидаемым значением. Ожидаемый результат: %s, текущий результат: %s", pageTitleName, currentTitle),
                pageTitleName, equalToIgnoringCase(currentTitle));
    }

    /**
     * @author Anton Pavlov
     * Производится сохранение заголовка страницы в переменную
     */
    @И("^заголовок страницы сохранен в переменную \"([^\"]*)\"$")
    public void savePageTitleToVariable(String variableName) {
        String titleName = getWebDriver().getTitle().trim();
        coreScenario.setVar(variableName, titleName);
        coreScenario.write("Значение заголовка страницы [" + titleName + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * @author Anton Pavlov
     * Производится закрытие текущей вкладки
     */
    @И("выполнено закрытие текущей вкладки")
    public void closeCurrentTab() {
        getWebDriver().close();
    }

    /**
     * @param propertyVariableName - ключ в файле application.properties
     * @param variableName         - имя переменной
     *                             Значение заданной переменной из application.properties сохраняется в переменную в coreScenario
     *                             для дальнейшего использования
     * @author Anton Pavlov
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        propertyVariableName = loadProperty(propertyVariableName);
        coreScenario.setVar(variableName, propertyVariableName);
        coreScenario.write("Значение сохраненной переменной " + propertyVariableName);
    }

    /**
     * @author Anton Pavlov
     * Выполняется обновление страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        refresh();
    }

    /**
     * @author Anton Pavlov
     * Выполняется переход по заданной ссылке,
     * Ссылка берется из property / переменной по ключу @param address, если такая переменная не найдена,
     * то берется переданное значение
     * при этом все ключи переменных в фигурных скобках
     * меняются на их значения из хранилища coreScenario
     */
    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(address));
        open(url);
        coreScenario.write("Url = " + url);
    }

    /**
     * @author Anton Pavlov
     * Проверка, что текущий URL совпадает с ожидаемым
     * (берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение)
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, is(expectedUrl));
    }

    /**
     * @author Anton Pavlov
     * Проверка, что текущий URL не совпадает с ожидаемым
     * (берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение)
     */
    @Тогда("^текущий URL не равен \"([^\"]*)\"$")
    public void checkCurrentURLIsNotEquals(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL совпадает с ожидаемым", currentUrl, Matchers.not(expectedUrl));
    }

    /**
     * @author Anton Pavlov
     * Устанавливает размеры окна браузера
     */
    @И("^установлено разрешение экрана (\\d+) х (\\d+)$")
    public void setBrowserWindowSize(int width, int height) {
        getWebDriver().manage().window().setSize(new Dimension(width, height));
        coreScenario.write("Установлены размеры окна браузера: ширина " + width + " высота" + height);
    }

    /**
     * @author Anton Pavlov
     * Разворачивает окно с браузером на весь экран
     */
    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        getWebDriver().manage().window().maximize();
    }


    /**
     * @author Anton Pavlov
     * Выполняется переход в конец страницы
     */
    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }


    /**
     * @author Anton Pavlov
     * Метод осуществляет снятие скриншота и прикрепление его к cucumber отчету.
     */
    @И("^снят скриншот текущей страницы$")
    public void takeScreenshot() {
        final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        CoreScenario.getInstance().getScenario().embed(screenshot, "image/png");
    }

    private String nextWindowHandle() {
        String currentWindowHandle = getWebDriver().getWindowHandle();
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        windowHandles.remove(currentWindowHandle);

        return windowHandles.iterator().next();
    }
}
