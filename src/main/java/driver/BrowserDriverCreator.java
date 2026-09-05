package driver;

import org.openqa.selenium.WebDriver;

/**
 * Factory Method contract: each browser gets its own "creator" class
 * that knows how to build a WebDriver instance for that specific browser.
 */
public interface BrowserDriverCreator {

    WebDriver create(boolean headless);
}