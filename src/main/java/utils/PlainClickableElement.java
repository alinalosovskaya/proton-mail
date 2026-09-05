package utils;

import org.openqa.selenium.WebElement;

/**
 * Base (undecorated) behaviour: just clicks the element.
 */
public class PlainClickableElement implements ClickableElement {

    private final WebElement element;

    public PlainClickableElement(WebElement element) {
        this.element = element;
    }

    @Override
    public void click() {
        element.click();
    }
}