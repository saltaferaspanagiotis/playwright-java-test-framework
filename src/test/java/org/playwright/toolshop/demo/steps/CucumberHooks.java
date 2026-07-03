package org.playwright.toolshop.demo.steps;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.playwright.toolshop.demo.fixtures.PlaywrightFixture;
import org.playwright.toolshop.demo.fixtures.ScenarioContext;

public class CucumberHooks {

    // Lower order runs first for @Before: guarantees the Page/BrowserContext exist
    // before any step-def class's own @Before (e.g. setupPageObjects()) reads them.
    @Before(order = 0)
    public void setUp(Scenario scenario) {
        ScenarioContext.clear();
        PlaywrightFixture.initContext(scenario.getName());
    }

    // Lower order runs last for @After (higher runs first): guarantees the context
    // stays open until every other teardown hook has had a chance to use the page.
    @After(order = 0)
    public void tearDown(Scenario scenario) {
        PlaywrightFixture.cleanupContext(scenario.getName());
    }

    // Runs once, after every scenario on every worker thread has finished — not per
    // scenario, since Browser/Playwright are deliberately reused across a thread's
    // scenarios (see PlaywrightFixture). Must be static per Cucumber's @AfterAll contract.
    @AfterAll
    public static void tearDownSuite() {
        PlaywrightFixture.closePlaywright();
    }
}
