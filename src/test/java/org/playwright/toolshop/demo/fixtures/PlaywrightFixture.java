package org.playwright.toolshop.demo.fixtures;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class PlaywrightFixture {

    protected static ThreadLocal<Playwright> playwright = ThreadLocal.withInitial(()->
    {
        Playwright playwrightInstance = Playwright.create();
        playwrightInstance.selectors().setTestIdAttribute("data-test");
        return playwrightInstance;
    });
    protected static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() -> {
        String browserName = System.getProperty("browser", "chromium").toLowerCase();
        BrowserType.LaunchOptions chromiumOptions = new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"));
        return switch (browserName) {
            case "chrome"   -> playwright.get().chromium().launch(new BrowserType.LaunchOptions()
                                    .setHeadless(false).setChannel("chrome")
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")));
            case "edge"     -> playwright.get().chromium().launch(new BrowserType.LaunchOptions()
                                    .setHeadless(false).setChannel("msedge")
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu")));
            case "firefox"  -> playwright.get().firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "webkit"   -> playwright.get().webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            default         -> playwright.get().chromium().launch(chromiumOptions);
        };
    });

    private static final ThreadLocal<Page> page = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();

    public static void initContext(String name) {
        ensureOutputDirectories();
        Path harPath = Paths.get("target/hars/" + sanitizedName(name) + ".har");
        browserContext.set(browser.get().newContext(
                new Browser.NewContextOptions().setRecordHarPath(harPath)
        ));
        page.set(getBrowserContext().newPage());
        getBrowserContext().tracing().start(
                    new Tracing.StartOptions()
                            .setScreenshots(true)
                            .setSnapshots(true)
                            .setSources(true));
    }

    public static void cleanupContext(String name) {
        try {
            takeScreenshot(getPage(), name);
        } finally {
            try {
                recordTrace(sanitizedName(name), getBrowserContext());
            } finally {
                getBrowserContext().close();
            }
        }
    }

    @AfterAll
    static void closePlaywright() {
        browser.get().close();
        browser.remove();
        playwright.get().close();
        playwright.remove();
    }

    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getBrowserContext() {
        return browserContext.get();
    }

    private static void ensureOutputDirectories() {
        try {
            Files.createDirectories(Paths.get("target/traces"));
            Files.createDirectories(Paths.get("target/hars"));
        } catch (Exception exception) {
            throw new RuntimeException("Unable to prepare output directories", exception);
        }
    }

    private static void recordTrace(String traceName, BrowserContext context) {
        String sanitizedTraceName = traceName.replaceAll("[^a-zA-Z0-9-_]", "_");
        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/trace-" + sanitizedTraceName + ".zip"))
        );
    }

    private static void takeScreenshot(Page page, String name) {
        var screenshot = page.screenshot(
                new Page.ScreenshotOptions()
                        .setFullPage(true));
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
    }

    private static String sanitizedName(String value) {
        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
