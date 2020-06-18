package ru.appavlov.at.library.core.block;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.appavlov.at.library.core.cucumber.annotations.Name;
import ru.appavlov.at.library.core.cucumber.annotations.Optional;
import ru.appavlov.at.library.core.cucumber.api.CorePage;

@Name("Навигация")
public class NavigationBlock extends CorePage {

    @Name("БКС брокер")
    @FindBy(css = "[role=\"banner\"]")
    public SelenideElement logoappavlovBroker;

    @Name("Трейдинг")
    @FindBy(xpath = ".//div/a[text()='Трейдинг']")
    public SelenideElement trading;

    @Name("Продукты")
    @FindBy(xpath = ".//div/a[text()='Продукты']")
    public SelenideElement products;

    @Name("Тарифы")
    @FindBy(xpath = ".//div/a[text()='Тарифы']")
    public SelenideElement rates;

    @Name("Идеи")
    @FindBy(xpath = ".//div/a[text()='Идеи']")
    public SelenideElement ideas;

    @Name("Обучение")
    @FindBy(xpath = ".//div/a[text()='Обучение']")
    public SelenideElement training;

    @Optional
    @Name("О компании")
    @FindBy(xpath = ".//div/a[text()='О компании']")
    public SelenideElement aboutCompany;

    @Optional
    @Name("Для бизнеса")
    @FindBy(xpath = ".//*[@href=\"/professionals\"]//../../../div/a[text()='Для бизнеса']")
    public SelenideElement forBusiness;

    @Optional
    @Name("Блог")
    @FindBy(xpath = ".//div/a[text()='Блог']")
    public SelenideElement blog;

    @Name("FAQ")
    @FindBy(xpath = ".//div/a[text()='FAQ']")
    public SelenideElement faq;
}
