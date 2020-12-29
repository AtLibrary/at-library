package ru.at.library.api;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@unit",
        features = "src/test/resources/features",
        glue = {"ru"}
)
public class RunFeaturesTest {
}
