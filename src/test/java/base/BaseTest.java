package base;

import Utils.AllureUtils;
import Utils.JsonUtil;
import Utils.LogsUtil;
import Utils.PropertiesUtils;
import driver.BrowserOptions;
import driver.DriverManager;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class BaseTest {

    /**
     * Single source of truth for all test data.
     * Available to every test class via inheritance.
     */
    protected static final JsonUtil testData = new JsonUtil("TestData", JsonUtil.TESTDATA);

    /**************** Suite Lifecycle ****************/

    /**
     * Runs ONCE before the entire suite.
     * Cleans all test-outputs folders so every run starts fresh.
     */
    @BeforeSuite(alwaysRun = true)
    public void cleanOutputsBeforeSuite() {
        LogsUtil.info("=== Cleaning test-outputs before suite run ===");
        cleanAndRecreate("test-outputs/allure-results");
        cleanAndRecreate("test-outputs/allure-report");
        cleanAndRecreate("test-outputs/videos");
        cleanAndRecreate("test-outputs/traces");
        // Logs folder: skip deleting because log4j holds the current file open
        // Old logs are overwritten per run by log4j timestamp naming
        LogsUtil.info("=== test-outputs cleaned and ready ===");
    }

    /**
     * Runs ONCE after the entire suite.
     * Generates the Allure HTML report automatically.
     */
    @AfterSuite(alwaysRun = true)
    public void generateReportAfterSuite() {
        LogsUtil.info("=== Suite finished — generating Allure report ===");
        AllureUtils.generateReport();
    }

    /**************** Test Lifecycle ****************/

    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional String browserName) {
        LogsUtil.info("=== Starting test on browser: "
                + (browserName != null ? browserName : "default from properties") + " ===");
        DriverManager.initDriver(browserName);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LogsUtil.info("=== Closing browser — test: " + testName + " ===");

        // Attach screenshot on FAILURE — captured before browser closes
        if (result.getStatus() == ITestResult.FAILURE) {
            AllureUtils.attachScreenshot(DriverManager.getPage(), testName);
            AllureUtils.attachText(
                    "Failure URL — " + testName,
                    DriverManager.getPage() != null ? DriverManager.getPage().url() : "page unavailable"
            );
        }

        // Attach log to every test result
        AllureUtils.attachLog(testName);

        // Save video if enabled — must happen before quitDriver()
        saveVideo(result);

        // Save trace on failure — must happen before quitDriver()
        saveTrace(result);

        DriverManager.quitDriver();
    }

    public Page getPage() {
        return DriverManager.getPage();
    }

    /**************** Video ****************/

    private void saveVideo(ITestResult result) {
        if (!PropertiesUtils.getBooleanProperty("recordVideo")) return;
        try {
            Page page = DriverManager.getPage();
            if (page == null || page.video() == null) return;

            String testName  = result.getMethod().getMethodName();
            Path videoPath   = page.video().path();
            Path destination = Paths.get("test-outputs/videos/" + testName + ".webm");

            Files.createDirectories(destination.getParent());
            Files.move(videoPath, destination, StandardCopyOption.REPLACE_EXISTING);
            LogsUtil.info("Video saved: " + destination);

            // Also attach video path as text in Allure
            AllureUtils.attachText("Video Path — " + testName, destination.toString());

        } catch (Exception e) {
            LogsUtil.error("Failed to save video: " + e.getMessage());
        }
    }

    /**************** Tracing ****************/

    private void saveTrace(ITestResult result) {
        if (!PropertiesUtils.getBooleanProperty("enableTracing")) return;
        if (result.getStatus() != ITestResult.FAILURE) return;
        try {
            String testName = result.getMethod().getMethodName();
            BrowserOptions.saveTrace(DriverManager.getContext(), testName);
            LogsUtil.info("Trace saved: test-outputs/traces/" + testName + ".zip");
        } catch (Exception e) {
            LogsUtil.error("Failed to save trace: " + e.getMessage());
        }
    }

    /**************** Folder Cleanup ****************/

    private void cleanAndRecreate(String folderPath) {
        Path path = Paths.get(folderPath);
        try {
            if (Files.exists(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                            throws IOException {
                        if (!dir.equals(path)) Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            Files.createDirectories(path);
            LogsUtil.info("Cleaned: " + folderPath);
        } catch (IOException e) {
            LogsUtil.error("Failed to clean " + folderPath + ": " + e.getMessage());
        }
    }
}