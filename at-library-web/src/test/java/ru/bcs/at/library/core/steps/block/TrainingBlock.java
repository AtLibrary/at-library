package ru.bcs.at.library.core.steps.block;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.bcs.at.library.core.cucumber.annotations.Name;
import ru.bcs.at.library.core.cucumber.api.CorePage;

@Name("Обучение")
public class TrainingBlock extends CorePage {

    @Name("Инвестиции 101")
    @FindBy(css = "[href='https://investments101.ru/']")
    private SelenideElement professionals;

    @Name("Вебинары/Семинары")
    @FindBy(css = "[href='/studing']")
    private SelenideElement onlinePartners;

}
