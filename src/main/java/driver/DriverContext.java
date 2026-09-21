package driver;

import org.openqa.selenium.WebDriver;


public class DriverContext {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void set(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void remove() {
        DRIVER.remove();
    }

}