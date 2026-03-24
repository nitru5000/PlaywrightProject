package Utils;

import io.qameta.allure.Allure;
import com.microsoft.playwright.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for attaching test artifacts to the Allure report.
 */
public class AllureUtils {

    private static final String ALLURE_RESULTS = "test-outputs/allure-results";
    private static final String ALLURE_REPORT  = "test-outputs/allure-report";
    private static final String LOGS_DIR       = "test-outputs/Logs";

    private AllureUtils() {}

    /**************** Screenshot ****************/

    public static void attachScreenshot(Page page, String testName) {
        if (page == null) {
            LogsUtil.warn("attachScreenshot: page is null — skipping screenshot for " + testName);
            return;
        }
        try {
            byte[] screenshot = page.screenshot(
                    new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(
                    "Screenshot — " + testName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    "png");
            LogsUtil.info("Screenshot attached to Allure for: " + testName);
        } catch (Exception e) {
            LogsUtil.error("Failed to attach screenshot: " + e.getMessage());
        }
    }

    /**************** Log File ****************/

    public static void attachLog(String testName) {
        try {
            Path logsDir = Paths.get(LOGS_DIR);
            if (!Files.exists(logsDir)) {
                LogsUtil.warn("attachLog: Logs directory not found");
                return;
            }

            Path latestLog = Files.list(logsDir)
                    .filter(p -> p.toString().endsWith(".log"))
                    .max((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(a)
                                    .compareTo(Files.getLastModifiedTime(b));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .orElse(null);

            if (latestLog == null) {
                LogsUtil.warn("attachLog: No log file found");
                return;
            }

            byte[] logBytes = Files.readAllBytes(latestLog);
            Allure.addAttachment(
                    "Log — " + testName,
                    "text/plain",
                    new ByteArrayInputStream(logBytes),
                    "log");
            LogsUtil.info("Log attached to Allure for: " + testName);

        } catch (Exception e) {
            LogsUtil.error("Failed to attach log: " + e.getMessage());
        }
    }

    /**************** Plain Text ****************/

    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(
                    name,
                    "text/plain",
                    new ByteArrayInputStream(content.getBytes()),
                    "txt");
        } catch (Exception e) {
            LogsUtil.error("Failed to attach text '" + name + "': " + e.getMessage());
        }
    }

    /**************** Report Generation ****************/

    /**
     * Generate Allure HTML report
     */
    public static void generateReport() {
        LogsUtil.info("=== Generating Allure HTML report ===");

        // First check if results exist
        Path resultsPath = Paths.get(ALLURE_RESULTS);
        if (!Files.exists(resultsPath)) {
            LogsUtil.warn("No Allure results found at: " + ALLURE_RESULTS);
            return;
        }

        try {
            // Try to find Allure executable
            String allureCmd = findAllureCommand();

            if (allureCmd == null) {
                // Allure not found - provide manual instructions
                LogsUtil.warn("Allure CLI not found. To generate report manually:");
                LogsUtil.warn("  Open Command Prompt and run:");
                LogsUtil.warn("  cd " + System.getProperty("user.dir"));
                LogsUtil.warn("  allure generate " + ALLURE_RESULTS + " --output " + ALLURE_REPORT + " --clean");
                LogsUtil.warn("  then open: " + ALLURE_REPORT + "/index.html");
                return;
            }

            LogsUtil.info("Using Allure: " + allureCmd);

            // Create report directory if it doesn't exist
            Path reportPath = Paths.get(ALLURE_REPORT);
            if (!Files.exists(reportPath)) {
                Files.createDirectories(reportPath);
            }

            // Build command
            ProcessBuilder pb = new ProcessBuilder(
                    allureCmd, "generate",
                    ALLURE_RESULTS,
                    "--output", ALLURE_REPORT,
                    "--clean"
            );
            pb.redirectErrorStream(true);

            // Set environment with PATH
            String[] env = System.getenv().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toArray(String[]::new);
            pb.environment().putAll(System.getenv());

            // Add scoop to PATH if not already there
            String userHome = System.getProperty("user.home");
            String scoopPath = userHome + "\\scoop\\shims";
            String currentPath = pb.environment().get("PATH");
            if (currentPath != null && !currentPath.contains(scoopPath)) {
                pb.environment().put("PATH", scoopPath + ";" + currentPath);
            }

            LogsUtil.info("Running: " + allureCmd + " generate " + ALLURE_RESULTS + " --output " + ALLURE_REPORT + " --clean");

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                LogsUtil.info("✅ Allure report generated successfully!");
                LogsUtil.info("📊 Report location: " + ALLURE_REPORT + "/index.html");
            } else {
                LogsUtil.warn("Allure CLI exited with code " + exitCode);
                LogsUtil.warn("Try running manually in Command Prompt:");
                LogsUtil.warn("  allure generate " + ALLURE_RESULTS + " --output " + ALLURE_REPORT + " --clean");
            }
        } catch (IOException e) {
            LogsUtil.warn("Could not run Allure CLI. Manual command:");
            LogsUtil.warn("  allure generate " + ALLURE_RESULTS + " --output " + ALLURE_REPORT + " --clean");
            LogsUtil.debug("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogsUtil.error("Report generation interrupted: " + e.getMessage());
        }
    }

    /**
     * Find Allure executable in common locations
     */
    private static String findAllureCommand() {
        // Try to find in PATH first
        try {
            ProcessBuilder pb = new ProcessBuilder("allure", "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LogsUtil.debug("Found Allure in PATH");
                return "allure";
            }
        } catch (Exception e) {
            LogsUtil.debug("Allure not found in PATH: " + e.getMessage());
        }

        // Check common installation paths
        String userHome = System.getProperty("user.home");
        String[] possiblePaths = {
                userHome + "\\scoop\\shims\\allure.exe",
                userHome + "\\scoop\\apps\\allure\\current\\bin\\allure.bat",
                "C:\\allure\\bin\\allure.bat",
                "C:\\Program Files\\allure\\bin\\allure.bat"
        };

        for (String path : possiblePaths) {
            if (Files.exists(Paths.get(path))) {
                LogsUtil.debug("Found Allure at: " + path);
                return path;
            }
        }

        LogsUtil.debug("Allure not found in common locations");
        return null;
    }
}