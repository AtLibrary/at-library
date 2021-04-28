package ru.at.library.web.page.wiki;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Mandatory;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;
import ru.at.library.web.page.wiki.block.SideMenuMainLinksBlock;

@Name("Вики - тест")
public class WikiTestPage extends CorePage {

    @Mandatory
    @Name("Заголовок")
    @FindBy(xpath = ".//*[@id='Добро_пожаловать_в_Википедию,']")
    public SelenideElement headerLabel;

    @Mandatory
    @Name("Основные ссылки - блок")
    @FindBy(xpath = ".//ul[.//*[text()='Заглавная страница']]")
    public SideMenuMainLinksBlock sideMenuMainLinksBlock;

    @Name("Поиск")
    @FindBy(css = "input#searchInput")
    public SelenideElement searchInput;

    @Name("Платформа сайта")
    @FindBy(css = "#footer-poweredbyico")
    public SelenideElement platformButton;

    @Name("Заявление о куки")
    @FindBy(css = "#footer-places-cookiestatement>a")
    public SelenideElement aboutCookiesBtn;
}
