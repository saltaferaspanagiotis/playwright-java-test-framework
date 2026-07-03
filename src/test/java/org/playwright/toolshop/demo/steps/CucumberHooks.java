package org.playwright.toolshop.demo.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.playwright.toolshop.demo.fixtures.PlaywrightFixture;
import org.playwright.toolshop.demo.fixtures.ScenarioContext;

public class CucumberHooks {

    @Before
    public void setUp(Scenario scenario) {
        ScenarioContext.clear();
        PlaywrightFixture.initContext(scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        PlaywrightFixture.cleanupContext(scenario.getName());
        ScenarioContext.clear();
    }
}
