package ru.at.library.core.cucumber.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public class ElementCheck implements IElementCheck {
    private final String name;
    private final SelenideElement element;
    private final Condition condition;
    private final String message;
    private boolean status;

    public ElementCheck(String name, SelenideElement element, Condition condition, String message) {
        this.name = name;
        this.element = element;
        this.condition = condition;
        this.message = message;
        this.status = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SelenideElement getElement() {
        return element;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return  "Элемент: " + this.name + "\n" +
                "Локатор: " + this.element.getSearchCriteria() + "\n" +
                "Проверка: " + this.message + "\n" +
                "Результат проверки: " + (this.status ? "Успешно пройдена" : "Не пройдена");
    }
}
