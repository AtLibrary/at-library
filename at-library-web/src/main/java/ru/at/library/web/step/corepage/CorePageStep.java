package ru.at.library.web.step.corepage;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.ru.И;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.core.cucumber.api.CoreScenario;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static ru.at.library.core.steps.OtherSteps.getPropertyOrStringVariableOrValue;
import static ru.at.library.core.utils.helpers.ScopedVariables.resolveVars;

/**
 * Шаги с  CorePage
 */
@Log4j2
public class CorePageStep {

    private final CoreScenario coreScenario = CoreScenario.getInstance();

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из properties. Если свойство не найдено, время таймаута равно 8 секундам
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @И("^(?:страница|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        CorePage page = coreScenario.getPage(nameOfPage);
        coreScenario.setCurrentPage(page);
        coreScenario.getCurrentPage().isAppeared();
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из properties. Если свойство не найдено, время таймаута равно 8 секундам
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @И("^блок \"([^\"]*)\" загрузился$")
    public void loadBlock(String nameOfPage) {
        CorePage page = coreScenario.getCurrentPage().getBlock(nameOfPage);
        coreScenario.setCurrentPage(page);
        coreScenario.getCurrentPage().isAppeared();
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional, не появились на странице
     *
     * @param nameOfPage название страница|блок|форма|вкладка
     */
    @И("^(?:страница|форма|вкладка) \"([^\"]*)\" не (?:загрузилась|загрузился)$")
    public void loadPageFailed(String nameOfPage) {
        coreScenario.setCurrentPage(coreScenario.getPage(nameOfPage));
        coreScenario.getCurrentPage().isDisappeared();
    }


    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlOrName) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(urlOrName));
        log.trace(" url = " + url);
        open(url);
        loadPage(pageName);
    }

    /**
     * Выполняется переход по заданной ссылке в новой вкладке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" в новой вкладке по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLinkNewTab(String pageName, String urlOrName) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(urlOrName));
        log.trace(" url = " + url);
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
        log.trace(" url = " + url());
    }


    /**
     * -----------------------------------------------------------------------------------------------------------------
     * ------------------------------------------------Проверки страниц-------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    /**
     * Проверка того, что все основные и обязательные элементы текущей страницы отображаются
     */
    @И("^все элементы текущей страницы отображаются$")
    public void pageAppeared() {
        this.coreScenario.setCurrentPage(
                this.coreScenario.getPage(this.coreScenario.getCurrentPage().getName())
        );
        this.coreScenario.getCurrentPage().checkPrimary(true);
    }

    /**
     * Проверка того, что блок отображается
     */
    @И("^блок \"([^\"]*)\" отображается на странице$")
    public void blockAppeared(String blockName) {
        CorePage block = this.coreScenario.getCurrentPage().getBlock(blockName);
        block.isAppeared();
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @И("^блок \"([^\"]*)\" не отображается на странице$")
    public void blockDisappeared(String blockName) {
        CorePage block = this.coreScenario.getCurrentPage().getBlock(blockName);
        block.isDisappeared();
    }
}
