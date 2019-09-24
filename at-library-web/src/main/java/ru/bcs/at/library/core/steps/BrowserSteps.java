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
package ru.bcs.at.library.core.steps;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
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
import static ru.bcs.at.library.core.cucumber.ScopedVariables.resolveVars;
import static ru.bcs.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;

/**
 * <h1>Браузер шаги</h1>
 */
@Log4j2
public class BrowserSteps {

    private CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * <p>Выполняется переход по заданной ссылке,
     *
     * @param address Ссылка берется из property / переменной по ключу, если такая переменная не найдена,
     *                то берется переданное значение
     *                при этом все ключи переменных в фигурных скобках
     *                меняются на их значения из хранилища coreScenario</p>
     */
    @И("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(address));
        open(url);
        coreScenario.write("Url = " + url);
    }

    /**
     * <p>Выполняется переход по заданной ссылке в новом окне,
     *
     * @param address Ссылка берется из property / переменной по ключу, если такая переменная не найдена,
     *                то берется переданное значение
     *                при этом все ключи переменных в фигурных скобках
     *                меняются на их значения из хранилища coreScenario</p>
     */
    public void openUrlNewTab(String address) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(address));

        ((JavascriptExecutor) WebDriverRunner.getWebDriver())
                .executeScript("window.open('" + url + "','_blank');");
    }

    /**
     * <p>Проверка, что текущий URL совпадает с ожидаемым
     *
     * @param url (берется из property / переменной, если такая переменная не найдена,
     *            то берется переданное значение)</p>
     */
    @И("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, is(expectedUrl));
    }

    /**
     * <p>Проверка, что текущий URL не совпадает с ожидаемым
     *
     * @param url (берется из property / переменной, если такая переменная не найдена,
     *            то берется переданное значение)</p>
     */
    @И("^текущий URL не равен \"([^\"]*)\"$")
    public void checkCurrentURLIsNotEquals(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL совпадает с ожидаемым", currentUrl, Matchers.not(expectedUrl));
    }

    /**
     * <p>Переключение на следующую вкладку браузера</p>
     */
    @И("выполнено переключение на следующую вкладку")
    public void switchToTheNextTab() {
        String nextWindowHandle = nextWindowHandle();
        getWebDriver().switchTo().window(nextWindowHandle);
        coreScenario.write("Текущая вкладка " + nextWindowHandle);
    }

    /**
     * <p>Выполняется обновление страницы</p>
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        refresh();
    }

    /**
     * <p>Производится закрытие текущей вкладки</p>
     */
    @И("выполнено закрытие текущей вкладки")
    public void closeCurrentTab() {
        getWebDriver().close();
    }

    /**
     * <p>Переключение на вкладку браузера с заголовком</p>
     *
     * @param title заголовок вкладки
     */
    @И("^выполнено переключение на вкладку с заголовком \"([^\"]*)\"$")
    public void switchToTheTabWithTitle(String title) {
        switchTo().window(title);
        checkPageTitle(title);
    }

    /**
     * <p>Производится сравнение заголовка страницы со значением, указанным в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)</p>
     *
     * @param expectedTitle ожидаемый заголовок текущей вкладки
     */
    @И("^заголовок страницы равен \"([^\"]*)\"$")
    public void checkPageTitle(String expectedTitle) {
        expectedTitle = getPropertyOrStringVariableOrValue(expectedTitle);
        String actualTitle = "";
        int sleepTime = 100;
        for (int time = 0; time < Configuration.timeout; time += 100) {
            actualTitle = title();
            if (actualTitle.toLowerCase().equals(expectedTitle.toLowerCase())) {
                break;
            }
            sleep(sleepTime);
        }
        assertThat(String.format("Заголовок страницы не совпадает с ожидаемым значением. Ожидаемый результат: %s, текущий результат: %s", expectedTitle, actualTitle),
                expectedTitle, equalToIgnoringCase(actualTitle));
    }

    /**
     * <p>Производится сохранение заголовка страницы в переменную</p>
     *
     * @param variableName имя переменной
     */
    @И("^заголовок страницы сохранен в переменную \"([^\"]*)\"$")
    public void savePageTitleToVariable(String variableName) {
        String titleName = title();
        coreScenario.setVar(variableName, titleName);
        coreScenario.write("Значение заголовка страницы [" + titleName + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * <p>Устанавливает размеры окна браузера</p>
     *
     * @param width  ширина
     * @param height высота
     */
    @И("^установлено разрешение экрана (\\d+) х (\\d+)$")
    public void setBrowserWindowSize(int width, int height) {
        getWebDriver().manage().window().setSize(new Dimension(width, height));
        coreScenario.write("Установлены размеры окна браузера: ширина " + width + " высота" + height);
    }

    /**
     * <p>Разворачивает окно с браузером на весь экран</p>
     */
    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        getWebDriver().manage().window().maximize();
    }

    /**
     * <p>Выполняется переход в конец страницы</p>
     */
    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * <p>Метод осуществляет снятие скриншота и прикрепление его к cucumber отчету.</p>
     */
    @И("^снят скриншот текущей страницы$")
    public void takeScreenshot() {
        final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        CoreScenario.getInstance().getScenario().embed(screenshot, "image/png");
    }

    /**
     * <p>Удалить все cookies</p>
     */
    @И("^cookies приложения очищены$")
    public void deleteCookies() {
        clearBrowserCookies();
    }

    /**
     * <p>Поиск cookie по имени.
     * Сохранение cookie в переменную для дальнейшего использования</p>
     *
     * @param nameCookie   имя cookie
     * @param variableName имя переменной
     */
    @И("^cookie с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String variableName) {
        String cookieName = resolveVars(nameCookie);
        Cookie var = getWebDriver().manage().getCookieNamed(cookieName);
        coreScenario.setVar(variableName, var);
    }

    /**
     * <p>Сохраняем все cookies в переменную для дальнейшего использования</p>
     *
     * @param variableName имя переменной
     */
    @И("^cookies сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String variableName) {
        Set cookies = getWebDriver().manage().getCookies();
        coreScenario.setVar(variableName, cookies);
    }

    /**
     * <p>Находим cookie по имени и подменяем ее значение.
     * Имя cookie и домен не меняются</p>
     *
     * @param cookieName  имя cookie
     * @param cookieValue значение cookie
     */
    @И("^добавлена cookie с именем \"([^\"]*)\" и значением \"([^\"]*)\"$")
    public void replaceCookie(String cookieName, String cookieValue) {
        String nameCookie = resolveVars(cookieName);
        String valueCookie = resolveVars(cookieValue);
        getWebDriver().manage().addCookie(new Cookie(nameCookie, valueCookie));
    }

    private String nextWindowHandle() {
        String currentWindowHandle = getWebDriver().getWindowHandle();
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        windowHandles.remove(currentWindowHandle);

        return windowHandles.iterator().next();
    }
}
