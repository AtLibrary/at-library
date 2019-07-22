package ru.bcs.at.library.core.steps;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@all",
        features = "src/test/resources/features",
        glue = {"ru.bcs.at.library"}
)
public class RunCukesTest {
}
