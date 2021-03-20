package ru.at.library.web.step.result.core;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

public interface IStepResult {
    Optional<List<WebElement>> getWebElements();
}
