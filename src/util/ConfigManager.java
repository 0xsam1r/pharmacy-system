package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for application settings
 */
public class ConfigManager {
    
    private static final String CONFIG_FILE = "pharmacy.properties";
    private static Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
    /**
     * Loads configuration from file
     */
    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Config file doesn't exist, use defaults
            setDefaults();
        }
    }
    
    /**
     * Sets default configuration values
     */
    private static void setDefaults() {
        properties.setProperty("app.name", "Pharmacy Management System");
        properties.setProperty("app.version", "1.0");
        properties.setProperty("app.theme", "default");
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "3306");
        properties.setProperty("db.name", "PMS");
        properties.setProperty("reports.directory", "reports");
        properties.setProperty("logs.directory", "logs");
    }
    
    /**
     * Saves configuration to file
     */
    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Pharmacy Management System Configuration");
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Failed to save configuration");
        }
    }
    
    /**
     * Gets a property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Gets a property value with default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Sets a property value
     */
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Gets application name
     */
    public static String getAppName() {
        return getProperty("app.name", "Pharmacy Management System");
    }
    
    /**
     * Gets application version
     */
    public static String getAppVersion() {
        return getProperty("app.version", "1.0");
    }
    
    /**
     * Gets reports directory
     */
    public static String getReportsDirectory() {
        return getProperty("reports.directory", "reports");
    }
    
    /**
     * Gets logs directory
     */
    public static String getLogsDirectory() {
        return getProperty("logs.directory", "logs");
    }
}
