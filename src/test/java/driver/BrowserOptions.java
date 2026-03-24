package driver;

import Utils.PropertiesUtils;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Tracing;

import java.nio.file.Paths;
import java.util.List;

public final class BrowserOptions {

    private BrowserOptions() {}

    /* ─── Launch Options ──────────────────────────────────────────────── */

    public static BrowserType.LaunchOptions getLaunchOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(PropertiesUtils.getBooleanProperty("headless"))
                .setSlowMo(PropertiesUtils.getIntProperty("slowMo"))
                .setArgs(getBrowserArgs());
    }

    /* ─── Context Options ─────────────────────────────────────────────── */

    public static Browser.NewContextOptions getContextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(null)          // full window via --start-maximized
                .setIgnoreHTTPSErrors(true);

        // Attach saved login state if available — avoids logging in on every test
        String storageStatePath = PropertiesUtils.getProperty("storageState");
        if (storageStatePath != null && !storageStatePath.isBlank()) {
            options.setStorageStatePath(Paths.get(storageStatePath));
        }

        // Record video for every test — useful for debugging CI failures
        if (PropertiesUtils.getBooleanProperty("recordVideo")) {
            options.setRecordVideoDir(Paths.get("test-outputs/videos/"));
        }

        return options;
    }

    /* ─── Tracing ─────────────────────────────────────────────────────── */

    // Start tracing after context is created
    public static void startTracing(com.microsoft.playwright.BrowserContext context) {
        if (PropertiesUtils.getBooleanProperty("enableTracing")) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(false));
        }
    }

    // Save trace on test failure — open with: npx playwright show-trace <file>
    public static void saveTrace(com.microsoft.playwright.BrowserContext context, String testName) {
        if (PropertiesUtils.getBooleanProperty("enableTracing")) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("test-outputs/traces/" + testName + ".zip")));
        }
    }

    /* ─── Browser Arguments ───────────────────────────────────────────── */

    private static List<String> getBrowserArgs() {
        return List.of(
                "--start-maximized",
                "--disable-notifications",
                "--disable-infobars",
                "--disable-extensions",
                "--no-sandbox",            // required in CI/Docker
                "--disable-dev-shm-usage"  // prevents memory issues in CI
        );
    }
}
