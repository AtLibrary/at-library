package ru.at.library.core.utils.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public interface IElementCheck {
    String getName();
    SelenideElement getElement();
    Condition getCondition();
    String getMessage();
    void setStatus(boolean status);
    boolean getStatus();
    String toString();
}
