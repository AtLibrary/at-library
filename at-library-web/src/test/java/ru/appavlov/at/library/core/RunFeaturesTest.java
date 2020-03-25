package ru.appavlov.at.library.core;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        plugin = {"io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm", "pretty"},
        tags = "@vtb",
        features = "src/test/resources/features",
        glue = {"ru.appavlov"}
)
public class RunFeaturesTest {

}
