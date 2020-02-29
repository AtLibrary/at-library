package ru.appavlov.at.library.core;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@unit",
        features = "src/test/resources/features",
        glue = {"ru.appavlov"}
)
public class RunFeaturesTest {
}
