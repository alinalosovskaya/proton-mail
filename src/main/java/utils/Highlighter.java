package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Highlighter {

    private static final String HIGHLIGHT_STYLE = "border: 3px solid red; background-color: yellow;";

    public static void highlight(WebDriver driver, WebElement element) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String originalStyle = element.getAttribute("style");

        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, HIGHLIGHT_STYLE);

        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                element, originalStyle == null ? "" : originalStyle);
    }
}