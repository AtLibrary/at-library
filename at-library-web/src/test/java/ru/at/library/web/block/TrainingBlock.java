package ru.at.library.web.block;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.at.library.core.cucumber.annotations.Name;
import ru.at.library.core.cucumber.api.CorePage;

@Name("Обучение")
public class TrainingBlock extends CorePage {

    @Name("Инвестиции 101")
    @FindBy(css = "[href='https://investments101.ru/']")
    public SelenideElement professionals;

    @Name("Вебинары/Семинары")
    @FindBy(css = "[href='/studing']")
    public SelenideElement onlinePartners;

}
