package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Decorator: wraps any ClickableElement and adds highlighting
 * before delegating the actual click to the wrapped object.
 */
public class HighlightingClickableElement implements ClickableElement {

    private final ClickableElement wrapped;
    private final WebDriver driver;
    private final WebElement element;

    public HighlightingClickableElement(ClickableElement wrapped, WebDriver driver, WebElement element) {
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