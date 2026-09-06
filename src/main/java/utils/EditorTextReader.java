package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Responsible only for reading text out of a contenteditable editor
 * (like Proton Mail's Rooster editor), trying multiple strategies
 * because getText() alone is unreliable for contenteditable elements.
 */
public class EditorTextReader {

    private final WebDriver driver;

    public EditorTextReader(WebDriver driver) {
        this.driver = driver;
    }

    public String readText(WebElement editor) {

        String text = editor.getText();
        if (text != null && !text.trim().isEmpty()) {
            return text;
        }

        String innerText = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].innerText || '';", editor);
        if (innerText != null && !innerText.trim().isEmpty()) {
            return innerText;
        }

        String textContent = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].textContent || '';", editor);

        return textContent == null ? "" : textContent;
    }
}