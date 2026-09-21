package driver;

import org.openqa.selenium.WebDriver;


public interface BrowserDriverCreator {

    WebDriver create(boolean headless);
}