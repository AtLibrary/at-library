package ru.at.library.core.utils.helpers;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.Configuration;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class AssertionHelper {
    private final ThreadLocal<List<String>> stepErrors = new ThreadLocal<>();

    public <T> void hamcrestAssert(String reason, T actual, Matcher<? super T> matcher) throws AssertionError {
        try {
            assertThat(reason, actual, matcher);
        } catch (AssertionError e) {
            continueOrBreak(e);
        }
    }

    public void continueOrBreak(AssertionError error) throws AssertionError {
        if (Configuration.assertionMode == AssertionMode.SOFT) {
            addStepError(error.getMessage());
        } else throw error;
    }

    public void addStepError(String error) {
        getStepErrors().add(error);
    }

    public boolean isNoStepErrors() { return getStepErrors().isEmpty(); }

    public List<String> takeStepErrors() {
        List<String> errors = getStepErrors();
        getStepErrors().clear();
        return errors;
    }

    private List<String> getStepErrors() {
        if (stepErrors.get() == null) {
            stepErrors.set(new ArrayList<String>());
        }
        return stepErrors.get();
    }
}
