# playwright-java-test-framework

An end-to-end test automation framework for [practicesoftwaretesting.com](https://practicesoftwaretesting.com), a demo e-commerce store. It's built with **Java 21**, **Playwright**, **Cucumber (BDD)**, **JUnit 5**, and **Allure** reporting, and is intended as a template/reference for a Page-Object-Model + Gherkin test suite.

This README is written for someone new to the project — it explains what's here, how to run it, and how to extend it.

## Tech stack

| Purpose | Library | Version |
|---|---|---|
| Browser automation | Playwright | 1.58.0 |
| BDD / Gherkin | Cucumber | 7.20.1 |
| Test runner | JUnit 5 (Jupiter + Platform Suite) | 5.11.3 |
| Assertions | AssertJ | 3.27.6 |
| Test data generation | JavaFaker | 1.0.2 |
| Reporting | Allure | 2.29.0 |
| Language / build | Java 21 / Maven | — |

## Prerequisites

- **JDK 21** (`JAVA_HOME` pointing at it)
- **Maven 3.6+**
- Internet access on first run — Playwright downloads its browser binaries (Chromium/Firefox/WebKit) automatically the first time a test executes, which can take a few minutes.

No `.env` file or secrets are required — the suite runs entirely against the public demo site and its public API.

## Getting started

```bash
git clone <repo-url>
cd playwright-java-test-framework
mvn clean verify
```

This compiles the project, runs the default (`@smoke`-tagged) test suite, and generates an Allure report. Tests launch a **visible** (non-headless) Chromium window by default — that's expected, don't be alarmed when a browser pops up on screen.

To view the report afterwards:

```bash
mvn allure:serve
```

`allure:serve` starts a local server and opens the report in your browser — use this over double-clicking `index.html`, since the static HTML report relies on fetching JSON files that browsers block when opened directly via `file://`.

## Project layout

The project is test-only — everything lives under `src/test`, there is no `src/main`.

```
src/test/java/org/playwright/toolshop/demo/
├── api/            REST API helpers (ToolShopAPI) — seed data or a session without going through the UI
├── config/         BaseUrl / Endpoint enums — single source of truth for hosts and paths
├── domain/         Immutable records for test data (User, ProductSummary, CartLineItem)
├── fixtures/       PlaywrightFixture (browser/page lifecycle), ScenarioContext (cross-step state)
├── pageobjects/    Page Object Model — one class per UI section/page
├── runners/        TestRunner — JUnit Platform Suite entry point that executes the Cucumber scenarios
└── steps/          Cucumber step definitions — glue between Gherkin text and page objects

src/test/resources/
├── features/               Gherkin .feature files (the actual test scenarios, in plain English)
├── junit-platform.properties  Cucumber parallel-execution settings
└── allure.properties          Allure results directory
```

## How the tests are structured (BDD with Cucumber)

Tests are written as **Gherkin scenarios** in `.feature` files — human-readable specifications that a non-programmer (e.g. a product owner) could read and understand. Example, from `product_catalog.feature`:

```gherkin
@PC_1 @scenario-priority:High
Scenario: Sally searches for an Adjustable Wrench
  Given Sally is on the home page
  When she searches for "Adjustable Wrench"
  Then the "Adjustable Wrench" product should be displayed
```

Each line (`Given`/`When`/`Then`/`And`) is matched by text to a Java method annotated `@Given`/`@When`/`@Then`/`@And` in a class under `steps/` (called a **step definition**). Cucumber matches by the *text*, not by feature file or class name, so step text must match exactly (including punctuation) between the `.feature` file and the `@Given("...")`/etc. annotation.

**Step definitions never call Playwright directly.** They instantiate page objects (in an `@Before` hook, via `PlaywrightFixture.getPage()`) and delegate all UI interaction to them:

```java
public class ProductCatalogStepDef {
    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;

    @Before
    public void setupPageObjects() {
        navBar = new NavBar(PlaywrightFixture.getPage());
        searchComponent = new SearchComponent(PlaywrightFixture.getPage());
        productList = new ProductList(PlaywrightFixture.getPage());
    }

    @Given("Sally is on the home page")
    public void sally_is_on_the_home_page() {
        navBar.openHomePage();
    }
    // ...
}
```

`CucumberHooks` (`steps/CucumberHooks.java`) runs before/after every scenario to set up and tear down the browser context (see [Test lifecycle](#test-lifecycle) below) and to clear shared scenario state.

### Existing feature files

| Feature file | Tag | Scenario IDs | Covers |
|---|---|---|---|
| `product_catalog.feature` | `@product_catalog` | `@PC_1`–`@PC_6` | Search, filter, sort |
| `user_authentication.feature` | `@user_authentication` | `@UA_1`–`@UA_4` | Register, login, invalid login, sign out |
| `shopping_cart.feature` | `@shopping_cart` | `@SC_1` | Add a product to the cart, verify cart contents |
| `customer_service.feature` | `@customer_service` | `@CS_1` | API login + session carried into the browser, submit a contact form |

## Running tests

```bash
# Run the default @smoke suite + generate an Allure report
mvn clean verify

# Run one specific scenario by its ID tag (overrides the default @smoke filter)
mvn clean verify -Dcucumber.filter.tags="@PC_1"

# Run a whole feature, or combine tags with Cucumber's tag expressions
mvn clean verify -Dcucumber.filter.tags="@user_authentication"
mvn clean verify -Dcucumber.filter.tags="@user_authentication and not @UA_3"

# Run against a different browser (chromium is the default)
mvn clean verify -Dbrowser=firefox   # also supports: chrome, edge, webkit

# Regenerate the report without re-running tests
mvn allure:serve

# Compile only, no tests
mvn clean compile
```

Notes on how this works under the hood:
- Tests run through **maven-failsafe-plugin** (integration-test phase), not surefire — surefire's unit-test phase is disabled (`skipTests=true` in `pom.xml`).
- `TestRunner` (the only class failsafe picks up, via its `**/*Runner.java` include pattern) hardcodes `@smoke` as the default Cucumber tag filter. Passing `-Dcucumber.filter.tags=...` on the command line overrides that default — this is the normal way to run a single scenario or feature during development instead of the whole suite.
- Cucumber scenarios execute **in parallel**, 2 at a time (`src/test/resources/junit-platform.properties`). Any new step definition code must be safe to run concurrently — this is why page objects and fixtures are always created fresh per scenario rather than shared/static.

### Tag reference

| Tag pattern | Meaning |
|---|---|
| `@smoke` | Included in the default suite run by `TestRunner` |
| `@<feature_name>` (e.g. `@shopping_cart`) | All scenarios belonging to one feature |
| `@XX_n` (e.g. `@PC_1`, `@UA_2`, `@SC_1`, `@CS_1`) | A unique ID for one specific scenario — use this to run just that scenario |
| `@feature-priority:High\|Medium\|Low` | Business priority of the whole feature (documentation only) |
| `@scenario-priority:High\|Medium\|Low` | Business priority of one scenario (documentation only) |

## Page Object Model

Each class in `pageobjects/` represents one UI section or page, is constructed with a Playwright `Page`, and exposes intention-revealing methods (`login.loginWithCredentials(...)`, not raw locator calls) to step definitions.

```java
public class Login {
    private final Page page;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator loginSubmit;

    public Login(Page page) {
        this.page = page;
        this.emailInput = page.getByTestId("email");
        this.passwordInput = page.getByTestId("password");
        this.loginSubmit = page.getByTestId("login-submit");
    }

    public void loginWithCredentials(String email, String password) {
        emailInput.fill(email);
        passwordInput.fill(password);
        loginSubmit.click();
    }
}
```

Conventions to follow when adding to or extending a page object:
- **Locate by `data-test`.** The app exposes `data-test="..."` attributes for most interactive elements; the framework configures Playwright's test-id attribute to `data-test` once (in `PlaywrightFixture`), so use `page.getByTestId("...")` over CSS/XPath wherever the app provides one. Open the app in a browser and inspect the element to find its `data-test` value. Fall back to a CSS locator (e.g. `page.locator(".card")`) only when no `data-test` exists.
- **One field per locator, built once in the constructor** — `private final Locator someButton = page.getByTestId("...")` — rather than calling `page.getByTestId(...)` inline inside every method. This avoids duplicating the same selector string across methods. The only exception is a locator scoped to a per-row/per-card element inside a loop (e.g. iterating a table's rows), since each iteration needs a different root element and there's nothing fixed to cache.
- **Avoid `Thread.sleep`/arbitrary waits.** Wrap the triggering action in `page.waitForResponse("**/matching/route**", () -> { ...click/fill... })` when an action triggers a network call you need to wait on, or use `page.waitForLoadState(LoadState.NETWORKIDLE)` after a navigation.

## Fixtures and cross-cutting infrastructure

### Test lifecycle

`fixtures/PlaywrightFixture.java` owns the Playwright/Browser/BrowserContext/Page objects, one set per thread (`ThreadLocal`, required since scenarios run in parallel). `CucumberHooks` (`steps/CucumberHooks.java`) drives the lifecycle from three Cucumber hooks:

- `@Before(order = 0) setUp` — clears `ScenarioContext` and calls `initContext(...)`, which creates a fresh `BrowserContext` (with HAR recording) and `Page`, and starts Playwright tracing. `Playwright`/`Browser` themselves are only launched the *first* time a given worker thread needs them — every later scenario on that thread reuses the already-running browser process, which is what keeps the suite fast. The explicit `order = 0` guarantees this runs before any step-def class's own `@Before` (e.g. `setupPageObjects()`), which reads `PlaywrightFixture.getPage()` — without it, hook order between different glue classes is undefined and that read could return `null`.
- `@After(order = 0) tearDown` — calls `cleanupContext(...)`, which takes a full-page screenshot (attached to the Allure report), stops the trace, and closes the `BrowserContext`. Runs once per scenario, mirroring `setUp`.
- `@AfterAll tearDownSuite` (static, per Cucumber's contract) — calls `closePlaywright()` once, after every scenario on every thread has finished, to close the underlying `Browser`/`Playwright` processes. This can't live in `tearDown` above: since a thread's `Browser` is reused across scenarios, closing it per-scenario would force a full relaunch on that thread's next scenario. It also can't be `browser.get().close()` naively, since `@AfterAll` runs on one thread that isn't necessarily one of the worker threads that launched a browser — `PlaywrightFixture` tracks every created `Playwright`/`Browser` in a static registry at creation time and `closePlaywright()` closes everything in that registry, regardless of which thread calls it.

(Cucumber scenarios don't go through JUnit's `@BeforeEach`/`@AfterEach`, which is why this lifecycle lives in Cucumber-native hooks rather than JUnit ones.)

Artifacts land in:
- `target/traces/trace-<scenario-name>.zip` — a full Playwright trace (DOM snapshots, network, console, screenshots per step)
- `target/hars/<scenario-name>.har` — the network HAR for the scenario
- Screenshots are attached directly inside the Allure report, no separate file to hunt for

**Opening a trace file:** the easiest way is dragging the `.zip` onto [trace.playwright.dev](https://trace.playwright.dev) (no install needed). If you have Node.js available, `npx playwright show-trace target/traces/trace-<name>.zip` works too.

### `ScenarioContext` — sharing data between step definition classes

Cucumber doesn't wire any dependency injection between glue classes by default in this project, so `ProductCatalogStepDef`, `UserStepDef`, etc. don't share instances. When one step (e.g. "a new user account has been created via the API") needs to hand data to a later step in a *different* step definition class (e.g. "she logs in with the registered credentials"), it goes through `fixtures/ScenarioContext.java` — a small `ThreadLocal`-backed holder:

```java
ScenarioContext.setUser(user);   // in the step that creates the user
...
User user = ScenarioContext.getUser();   // in a later step, possibly a different class
```

`CucumberHooks` clears it before and after every scenario so state never leaks between scenarios sharing a pooled thread. If you need to share a new kind of value, add another typed field + getter/setter pair to `ScenarioContext` — avoid a generic `Map<String, Object>`, since typed fields keep call sites cast-free.

### `ToolShopAPI` — bypassing the UI for test setup

`api/ToolShopAPI.java` wraps Playwright's `page.request()` (an `APIRequestContext`) to call the backend REST API directly, for things that would be slow or irrelevant to repeat through the UI:

- `registerUser(User user)` — creates an account via `POST /users/register`, used to get a valid account without driving the registration form every time.
- `loginAsUser(String email, String password)` — logs in via `POST /users/login`, then seeds the returned JWT into the browser's `localStorage` (via `BrowserContext.addInitScript`) so the *next* page navigation renders as already logged in, without ever touching the login form.

### `config` — base URLs and paths

`config/BaseUrl.java` (`UI`/`API` hosts) and `config/Endpoint.java` (each known path paired with a `BaseUrl`) are the single source of truth for URLs — e.g. `Endpoint.CONTACT.url()` or `Endpoint.LOGIN.path()` (path only, useful for substring checks like "is the browser currently on the login page?"). Add a new destination as one more enum constant rather than hardcoding a URL string in a page object.

### Test data

`domain/User.randomValidUser()` (backed by JavaFaker) generates a random, valid registration payload, post-processing the fake password so it always satisfies the app's complexity rules (uppercase, lowercase, digit, symbol, min length). **Always prefer this over hardcoded test users** — it avoids collisions between parallel scenarios and across repeated runs.

## Adding a new test — walkthrough

1. **Write the Gherkin.** Add a `Scenario` to an existing `.feature` file, or create a new file under `src/test/resources/features/`. Give it a unique ID tag (e.g. `@SC_2`) and, if it belongs to the default run, `@smoke`.
2. **Run it once** with `mvn clean verify -Dcucumber.filter.tags="@YOUR_ID"`. Cucumber will fail with "undefined step" and print ready-to-paste Java method stubs for any step text that has no matching definition yet.
3. **Implement the steps** in the matching class under `steps/` (or create a new `...StepDef.java` if the scenario doesn't fit an existing one). Each method should just call page object methods — no raw Playwright calls in step definitions.
4. **Extend a page object** if the step needs a UI interaction that doesn't exist yet: add a `private final Locator` field (built from `page.getByTestId("...")` — find the `data-test` value via your browser's DevTools) and a method using it.
5. **Re-run your scenario by tag** until it's green, then run the surrounding feature/suite to make sure nothing else regressed.
6. Check `target/traces/` or the Allure report if something fails and the reason isn't obvious from the console output.

## Troubleshooting

- **A visible Chrome/Chromium window pops up during `mvn verify`.** Expected — the framework runs non-headless by default (see `PlaywrightFixture`). Don't close it manually; let the test finish.
- **First run is slow.** Playwright downloads browser binaries on first use; subsequent runs are fast.
- **`mvn allure:report` output looks broken/blank when opened directly.** Use `mvn allure:serve` instead (see above) — the static report needs to be served over HTTP, not opened via `file://`.
- **A locator isn't found.** Open the target page in a real browser, inspect the element, and confirm its `data-test` attribute — the app occasionally doesn't expose one, in which case a CSS fallback (matching the pattern already used in `CheckoutCart`/`ProductList`) is acceptable.
