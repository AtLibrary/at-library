package ru.bcs.at.library.core.steps;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@unit123",
        features = "src/test/resources/features",
        glue = {"ru.bcs"}
)
public class RunFeaturesTest {

}
