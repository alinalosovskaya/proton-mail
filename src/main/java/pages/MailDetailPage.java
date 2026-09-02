package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MailDetailPage extends AbstractPage {

    private final By starButton = By.cssSelector( "[data-testid='item-star-false']" );

    private final By activeStarButton =  By.cssSelector("[data-testid='item-star-true']");

    public MailDetailPage(WebDriver driver) {
        super(driver);
    }

    /** Star the currently opened conversation. */
    public MailDetailPage toggleStar() {

        WebElement star = wait.until(driver -> {

                    List<WebElement> buttons = driver.findElements(starButton);
                    for (WebElement button : buttons) {
                        if (button.isDisplayed() && button.isEnabled()) { return button;   }   }
                    return null;     });

        star.click();
        wait.until(driver -> {
            List<WebElement> buttons =  driver.findElements(activeStarButton);

            for (WebElement button : buttons) {
                if (button.isDisplayed()) {  return true;  } }
            return false;
        });

        return this;
    }

    /**
     * Check whether the current conversation is starred.
     */
    public boolean isStarActive() {

        List<WebElement> buttons = driver.findElements(activeStarButton);

        for (WebElement button : buttons) {

            if (button.isDisplayed()) { return true; }

        }

        return false;
    }
}
