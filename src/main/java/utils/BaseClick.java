package utils;

import org.openqa.selenium.WebElement;

/**
 * Base behaviour: just clicks the element.
 */
public class BaseClick implements Click {

    private final WebElement element;

    public BaseClick(WebElement element) {
        this.element = element;
    }

    @Override
    public void click() {
        element.click();
    }
}