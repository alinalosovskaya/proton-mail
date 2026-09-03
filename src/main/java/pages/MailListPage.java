package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MailListPage extends AbstractPage {

    private final By searchToolbarInput =
            By.cssSelector("input[data-testid='searсh-keyword'][readonly]");

    private final By searchDialogInput =
            By.cssSelector("input[data-testid='input-input-element']");

    private final By searchSubmitButton =
            By.cssSelector("[data-testid='advanced-search:submit']");

    private final By moveToToolbarButton =
            By.cssSelector("[data-testid='toolbar:moveto']");

    private final By moveDropdownList =
            By.cssSelector("[data-testid='move-dropdown-list']");

    private final By messageRows =
            By.cssSelector("[data-testid^='message-item:']");

    public MailListPage(WebDriver driver) {
        super(driver);
    }

    public MailListPage waitUntilPageAvailable() {

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        searchToolbarInput
                )
        );

        return this;
    }

    public MailListPage waitUntilLoaded() {
        return waitUntilPageAvailable();
    }

    public MailListPage waitForMailList() {
        return waitUntilPageAvailable();
    }

    public boolean hasEmails() {
        return !driver.findElements(messageRows).isEmpty();
    }

    public MailListPage searchBySubject(String subject) {

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException(
                    "Search subject must not be empty."
            );
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchToolbarInput
                )
        ).click();

        WebElement searchInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                searchDialogInput
                        )
                );

        searchInput.clear();
        searchInput.sendKeys(subject);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchSubmitButton
                )
        ).click();

        waitForMail(subject);

        return this;
    }

    public boolean isMailPresent(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }

        return longWait.until(
                driver -> findMail(subject) != null
        );
    }

    private WebElement findMail(String subject) {

        List<WebElement> mails =
                driver.findElements(messageRows);

        for (WebElement mail : mails) {

            if (mail.isDisplayed()
                    && mail.getText().contains(subject)) {

                return mail;
            }
        }

        return null;
    }

    public boolean waitForMail(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }

        return longWait.until(
                driver -> findMail(subject) != null
        );
    }

    public boolean mailEventuallyDisappears(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }

        return longWait.until(
                driver -> findMail(subject) == null
        );
    }

    private WebElement waitForMailItem(String subject) {

        return longWait.until(
                driver -> findMail(subject)
        );
    }

    public MailListPage selectMailByCheckbox(String subject) {

        WebElement mail =
                waitForMailItem(subject);

        By checkbox =
                By.cssSelector("label.item-checkbox-label");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        mail.findElement(checkbox)
                )
        ).click();

        return this;
    }

    public MailDetailPage openMailForReading(String subject) {

        waitForMailItem(subject).click();

        return new MailDetailPage(driver);
    }

    public ComposePage reopenDraft(String subject) {

        waitForMailItem(subject).click();

        return new ComposePage(driver);
    }

    /**
     * Move selected email to the specified folder.
     */
    public MailListPage moveSelectedMailTo(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Folder name must not be empty."
            );
        }

        driver.findElement(moveToToolbarButton).click();

        WebElement dropdown =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                moveDropdownList
                        )
                );

        dropdown.findElement(
                By.xpath(
                        ".//li[.//*[normalize-space()=" +
                                xpathLiteral(folderName) +
                                "]]"
                )
        ).click();

        return this;
    }


    public boolean hasAtLeastOneMail() {
        return !driver.findElements(messageRows).isEmpty();
    }

    public boolean waitForAtLeastOneMail() {

        return longWait.until(
                driver ->
                        !driver.findElements(messageRows).isEmpty()
        );
    }

    public int visibleMailCount() {

        int count = 0;

        for (WebElement mail :
                driver.findElements(messageRows)) {

            if (mail.isDisplayed()) {
                count++;
            }
        }

        return count;
    }

    private String xpathLiteral(String value) {

        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        String[] parts =
                value.split("'", -1);

        StringBuilder result =
                new StringBuilder("concat(");

        for (int i = 0; i < parts.length; i++) {

            if (i > 0) {
                result.append(", \"'\", ");
            }

            result.append("'")
                    .append(parts[i])
                    .append("'");
        }

        result.append(")");

        return result.toString();
    }
}
