package ru.at.library.core;

import io.cucumber.junit.Cucumber;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;
import org.testng.annotations.DataProvider;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        monochrome = true,
        plugin = {"io.qameta.allure.cucumber5jvm.AllureCucumber5Jvm", "pretty"},
        features = {"src/test/resources/features"},
        tags = {"@Google"},
        glue = {"ru"}
)
public class RunFeaturesTest extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
