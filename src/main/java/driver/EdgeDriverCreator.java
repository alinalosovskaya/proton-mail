package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class EdgeDriverCreator implements BrowserDriverCreator {

    @Override
    public WebDriver create(boolean headless) {
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver();
    }
}