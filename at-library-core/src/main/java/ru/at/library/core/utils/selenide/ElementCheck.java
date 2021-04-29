package ru.at.library.core.utils.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ElementCheck implements IElementCheck {
    private final String name;
    private final SelenideElement element;
    private final Condition condition;
    private final String message;
    @Setter
    private boolean status;

    public ElementCheck(String name, SelenideElement element, Condition condition, String message) {
        this.name = name;
        this.element = element;
        this.condition = condition;
        this.message = message;
        this.status = false;
    }

    @Override
    public boolean getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return  "Элемент: " + this.name + "\n" +
                "Локатор: " + this.element.getSearchCriteria() + "\n" +
                "Проверка: " + this.message + "\n" +
                "Результат проверки: " + (this.status ? "Успешно пройдена" : "Не пройдена");
    }

}
