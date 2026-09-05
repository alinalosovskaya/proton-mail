package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Highlighter;

import java.time.Duration;

/** Base class for all Page Objects */
public abstract class AbstractPage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait longWait;

    protected AbstractPage(WebDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(90));

        this.wait.ignoring(StaleElementReferenceException.class);
        this.longWait.ignoring(StaleElementReferenceException.class);
    }


}