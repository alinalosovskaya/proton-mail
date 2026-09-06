package driver;

import org.openqa.selenium.WebDriver;

/**
 * Responsible only for storing and retrieving the current thread's
 * WebDriver instance. Creation logic lives elsewhere (DriverFactory),
 * so this class has a single reason to change: how driver state is
 * kept per-thread.
 */
public class DriverContext {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void set(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void remove() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}