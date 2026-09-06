package driver;

import config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class DriverFactory {

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private static final Map<String, BrowserDriverCreator> CREATORS = Map.of(
            "chrome", new ChromeDriverCreator(),
            "firefox", new FirefoxDriverCreator(),
            "edge", new EdgeDriverCreator()
    );

    public static WebDriver createDriver(String browserName) {

        String browser = browserName == null ? "chrome" : browserName.toLowerCase();
        boolean headless = ConfigReader.getInstance().isHeadless();

        LOGGER.info("Creating WebDriver for browser: {}, headless: {}", browser, headless);

        BrowserDriverCreator creator = CREATORS.get(browser);

        if (creator == null) {
            LOGGER.error("Unsupported browser requested: {}", browser);
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        WebDriver driver = creator.create(headless);

        LOGGER.debug("WebDriver instance created: {}", driver);

        DriverContext.set(driver);

        return driver;
    }

    public static WebDriver getDriver() {
        return DriverContext.get();
    }

    public static void quitDriver() {
        DriverContext.remove();
    }
}