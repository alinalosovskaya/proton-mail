package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DriverFactory {

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    public static WebDriver createDriver(String browserName) {

        String browser = browserName == null ? "chrome" : browserName.toLowerCase();

        LOGGER.info("Creating WebDriver for browser: {}", browser);

        WebDriver driver;

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                LOGGER.error("Unsupported browser requested: {}", browser);
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        LOGGER.debug("WebDriver instance created: {}", driver);

        return driver;
    }
}