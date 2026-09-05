package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final String ENV = System.getProperty("env", "qa");
    private static final Properties PROPERTIES = new Properties();

    static {
        String fileName = "config/" + ENV + ".properties";

        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) { throw new RuntimeException("Config file not found: " + fileName);}

            PROPERTIES.load(input);

        } catch (IOException e) { throw new RuntimeException("Failed to load config file: " + fileName, e);}
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean isHeadless() {
        String value = get("headless");
        return Boolean.parseBoolean(value);
    }
}