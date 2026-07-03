package org.playwright.toolshop.demo.fixtures;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlaywrightFixture {

    // Tracks every Playwright/Browser instance created by any worker thread, so
    // closePlaywright() can close them all from a single call on a single thread —
    // the ThreadLocals below only expose the calling thread's own instance via .get().
    private static final Set<Playwright> allPlaywrights = ConcurrentHashMap.newKeySet();
    private static final Set<Browser> allBrowsers = ConcurrentHashMap.newKeySet();

    protected static ThreadLocal<Playwright> playwright = ThreadLocal.withInitial(()->
    {
        Playwright playwrightInstance = Playwright.create();
        playwrightInstance.selectors().setTestIdAttribute("data-test");
        allPlaywrights.add(playwrightInstance);
        return playwrightInstance;
    });
    protected static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() -> {
        String browserName = System.getProperty("browser", "chromium").toLowerCase();
        BrowserType.LaunchOptions chromiumOptions = new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"));
        Browser launchedBrowser = switch (browserName) {
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
        allBrowsers.add(launchedBrowser);
        return launchedBrowser;
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


    public static void closePlaywright() {
        allBrowsers.forEach(Browser::close);
        allPlaywrights.forEach(Playwright::close);
        allBrowsers.clear();
        allPlaywrights.clear();
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
