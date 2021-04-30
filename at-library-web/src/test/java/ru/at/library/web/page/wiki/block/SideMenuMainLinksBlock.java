package ru.at.library.web.page.wiki.block;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Основные ссылки - блок")
public class SideMenuMainLinksBlock extends CorePage {

    @Name("Заглавная страница")
    @FindBy(xpath = ".//li[1]")
    public SelenideElement mainPageLink;

}
