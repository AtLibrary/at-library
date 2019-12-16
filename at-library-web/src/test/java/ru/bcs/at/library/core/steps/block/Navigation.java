package ru.bcs.at.library.core.steps.block;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.annotations.Optional;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("Навигация")
public class Navigation extends CorePage {

    @Name("БКС брокер")
    @FindBy(css = "[role=\"banner\"]")
    private SelenideElement logoBcsBroker;

    @Name("Трейдинг")
    @FindBy(xpath=".//div/a[text()='Трейдинг']")
    private SelenideElement trading;

    @Name("Продукты")
    @FindBy(xpath=".//div/a[text()='Продукты']")
    private SelenideElement products;

    @Name("Тарифы")
    @FindBy(xpath=".//div/a[text()='Тарифы']")
    private SelenideElement rates;

    @Name("Идеи")
    @FindBy(xpath=".//div/a[text()='Идеи']")
    private SelenideElement ideas;

    @Name("Обучение")
    @FindBy(xpath=".//div/a[text()='Обучение']")
    private SelenideElement training;

    @Optional
    @Name("О компании")
    @FindBy(xpath=".//div/a[text()='О компании']")
    private SelenideElement aboutCompany;

    @Optional
    @Name("Для бизнеса")
    @FindBy(xpath=".//div/a[text()='Для бизнеса']")
    private SelenideElement forBusiness;

    @Optional
    @Name("Блог")
    @FindBy(xpath=".//div/a[text()='Блог']")
    private SelenideElement blog;

    @Name("FAQ")
    @FindBy(xpath=".//div/a[text()='FAQ']")
    private SelenideElement faq;
}
