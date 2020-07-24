package ru.at.library.core.page;

import com.codeborne.selenide.ElementsCollection;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Википедия")
public class WikipediaPage extends CorePage {

    @Name("Заглавная страница")
    @FindBy(css = "[id=\"p-navigation\"] ul[class=\"vector-menu-content-list\"] li")
    public ElementsCollection homeLinkList;

    @Name("Список ссылок")
    @FindBy(css = "[id=\"mw-panel\"] li")
    public ElementsCollection linkList;

    @Name("Инструменты")
    @FindBy(css = "[id=\"p-tb\"] li")
    public ElementsCollection listToolsLink;

    @Name("Нет списка")
    @FindBy(css = "[id=\"not-spisok\"] li")
    public ElementsCollection notList;
}
