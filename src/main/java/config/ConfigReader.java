package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static ConfigReader instance;

    private final Properties properties;
    private final String env;


    private ConfigReader() {

        this.env = System.getProperty("env", "qa");
        this.properties = new Properties();

        String fileName = "config/" + env + ".properties";

        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new RuntimeException("Config file not found: " + fileName);
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + fileName, e);
        }
    }


    public static synchronized ConfigReader getInstance() {

        if (instance == null) {
            instance = new ConfigReader();
        }

        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String baseUrl() {
        return get("base.url");
    }

    public String browser() {
        return get("browser");
    }

    public boolean isHeadless() {
        String value = get("headless");
        return Boolean.parseBoolean(value);
    }
}