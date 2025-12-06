package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

 
public class ConfigManager {
    
    private static final String CONFIG_FILE = "pharmacy.properties";
    private static Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
     
    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
             
            setDefaults();
        }
    }
    
     
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
    
     
    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Pharmacy Management System Configuration");
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Failed to save configuration");
        }
    }
    
     
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
     
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
     
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
     
    public static String getAppName() {
        return getProperty("app.name", "Pharmacy Management System");
    }
    
     
    public static String getAppVersion() {
        return getProperty("app.version", "1.0");
    }
    
     
    public static String getReportsDirectory() {
        return getProperty("reports.directory", "reports");
    }
    
     
    public static String getLogsDirectory() {
        return getProperty("logs.directory", "logs");
    }
}
