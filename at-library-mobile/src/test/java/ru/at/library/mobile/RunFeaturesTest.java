package ru.at.library.mobile;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@mobile",
        features = "src/test/resources/features",
        glue = {"ru"}
)
public class RunFeaturesTest {
}
