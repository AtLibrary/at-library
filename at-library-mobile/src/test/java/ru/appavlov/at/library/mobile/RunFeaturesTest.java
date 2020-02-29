package ru.appavlov.at.library.mobile;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@mobile",
        features = "src/test/resources/features",
        glue = {"ru.appavlov"}
)
public class RunFeaturesTest {
}
