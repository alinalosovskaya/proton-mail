package driver;

import org.openqa.selenium.WebDriver;


public class DriverFactory {

    private final BrowserDriverRegistry registry;

    public DriverFactory(BrowserDriverRegistry registry) {
        this.registry = registry;
    }

    public WebDriver createDriver(String browser, boolean headless) {
        return registry.get(browser).create(headless);
    }

}