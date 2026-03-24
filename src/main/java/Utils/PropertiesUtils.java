package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    private static final Properties properties = new Properties();

    static {
        // Load from classpath — works in IntelliJ, Maven, and CI environments
        try (InputStream is = PropertiesUtils.class
                .getClassLoader().getResourceAsStream("Config.properties")) {
            if (is == null) {
                System.err.println("ERROR: Config.properties not found on classpath");
            } else {
                properties.load(is);
                System.out.println("INFO: Config.properties loaded successfully.");
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load Config.properties: " + e.getMessage());
        }
    }

    private PropertiesUtils() {}

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key) {
        return getIntProperty(key, 0);
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, false);
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
