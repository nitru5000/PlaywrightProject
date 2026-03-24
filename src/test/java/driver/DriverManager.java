package driver;

import Utils.LogsUtil;
import Utils.PropertiesUtils;
import com.microsoft.playwright.*;

public class DriverManager {

    private static final ThreadLocal<Page>           pageThread    = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThread = new ThreadLocal<>();
    private static final ThreadLocal<Playwright>     playwrightThread = new ThreadLocal<>();
    private static final ThreadLocal<Browser>        browserThread = new ThreadLocal<>();

    public static void initDriver(String browserName) {
        Playwright playwright = Playwright.create();
        playwrightThread.set(playwright);

        // null or blank → read from Config.properties
        String browserType = (browserName != null && !browserName.isBlank())
                ? browserName
                : PropertiesUtils.getProperty("browser");

        LogsUtil.info("Initializing Playwright browser: " + browserType);

        BrowserType.LaunchOptions launchOptions = BrowserOptions.getLaunchOptions();
        Browser browser;

        switch (browserType.toLowerCase()) {
            case "chrome":
                browser = playwright.chromium().launch(launchOptions.setChannel("chrome"));
                break;
            case "chromium":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "edge":
                browser = playwright.chromium().launch(launchOptions.setChannel("msedge"));
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                LogsUtil.info("Unknown browser '" + browserType + "' — defaulting to chromium");
                browser = playwright.chromium().launch(launchOptions);
        }

        browserThread.set(browser);

        BrowserContext context = browser.newContext(BrowserOptions.getContextOptions());
        contextThread.set(context);

        // Start tracing if enabled in Config.properties
        BrowserOptions.startTracing(context);

        Page page = context.newPage();
        page.setDefaultTimeout(PropertiesUtils.getIntProperty("timeout"));
        page.setDefaultNavigationTimeout(PropertiesUtils.getIntProperty("timeout"));
        pageThread.set(page);

        LogsUtil.info("Browser initialized successfully");
    }

    public static Page getPage() {
        return pageThread.get();
    }

    public static BrowserContext getContext() {
        return contextThread.get();
    }

    public static void quitDriver() {
        try {
            if (contextThread.get() != null) contextThread.get().close();
            if (browserThread.get()  != null) browserThread.get().close();
            if (playwrightThread.get() != null) playwrightThread.get().close();
            LogsUtil.info("Browser closed successfully.");
        } finally {
            pageThread.remove();
            contextThread.remove();
            browserThread.remove();
            playwrightThread.remove();
        }
    }
}
