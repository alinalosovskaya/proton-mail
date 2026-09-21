package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Decorator: wraps any ClickableElement and adds highlighting
 * before delegating the actual click to the wrapped object.
 */
public class HighlightClick implements Click {

    private final Click wrapped;
    private final WebDriver driver;
    private final WebElement element;

    public HighlightClick(Click wrapped, WebDriver driver, WebElement element) {
        this.wrapped = wrapped;
        this.driver = driver;
        this.element = element;
    }

    @Override
    public void click() {
        Highlighter.highlight(driver, element);
        wrapped.click();
    }
}