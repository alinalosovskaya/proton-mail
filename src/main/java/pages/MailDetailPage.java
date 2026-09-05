package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MailDetailPage extends AbstractPage {
    private final By conversationContainer = By.cssSelector("article.message-container.is-opened");

    private final By starButton = By.cssSelector("[data-testid='item-star-false']");
    private final By activeStarButton = By.cssSelector("[data-testid='item-star-true']");

    public MailDetailPage(WebDriver driver) {
        super(driver);
    }

    /** Star the currently opened message in the conversation. */
    public MailDetailPage toggleStar() {
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(conversationContainer));
        WebElement star = wait.until(d -> container.findElement(starButton));
        star.click();

        wait.until(d -> container.findElements(activeStarButton).stream().anyMatch(WebElement::isDisplayed));
        return this;
    }

    /** Check whether the currently opened message is starred */
    public boolean isStarActive() {
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(conversationContainer));
        return container.findElements(activeStarButton).stream().anyMatch(WebElement::isDisplayed);
    }
}