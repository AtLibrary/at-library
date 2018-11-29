package ru.bcs.at.library.core;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import io.qameta.allure.cucumber3jvm.AllureCucumber3Jvm;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/features"},
        glue = {"ru.bcs.at.library.core.steps"},
        tags = {"@instagram"}
        ,
        plugin = {"io.qameta.allure.cucumber3jvm.AllureCucumber3Jvm"}
)
public class CucumberRunnerTest {

}
