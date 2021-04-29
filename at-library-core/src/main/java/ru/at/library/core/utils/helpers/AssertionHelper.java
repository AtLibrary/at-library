package ru.at.library.core.utils.helpers;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.Configuration;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class AssertionHelper {
    private final boolean SOFT_ASSERT_ENABLED = Configuration.assertionMode.equals(AssertionMode.SOFT);
    private final ThreadLocal<StringList> stepErrors = new ThreadLocal<>();

    public <T> void hamcrestAssert(String reason, T actual, Matcher<? super T> matcher) throws AssertionError {
        try {
            assertThat(reason, actual, matcher);
        } catch (AssertionError e) {
            continueOrBreak(e);
        }
    }

    public void continueOrBreak(AssertionError error) throws AssertionError {
        if (SOFT_ASSERT_ENABLED) {
            addStepError(error.getMessage());
        } else throw error;
    }

    public void addStepError(String error) {
        this.getStepErrors().add(error);
    }

    public boolean isNoStepErrors() {
        return this.getStepErrors().isEmpty();
    }

    public List<String> takeStepErrors() {
        List<String> errors = this.getStepErrors().takeList();
        this.getStepErrors().clear();
        return errors;
    }

    private StringList getStepErrors() {
        if (stepErrors.get() == null) {
            stepErrors.set(new StringList());
        }
        return stepErrors.get();
    }
}
