package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MailListPage extends AbstractPage {

    private static final Logger LOGGER = LogManager.getLogger(MailListPage.class);

    private final By searchToolbarInput = By.cssSelector("input[data-testid='search-keyword'][readonly]");

    private final By searchDialogInput = By.cssSelector("input[data-testid='input-input-element']");

    private final By searchSubmitButton = By.cssSelector("[data-testid='advanced-search:submit']");

    private final By moveToToolbarButton = By.cssSelector("[data-testid='toolbar:moveto']");

    private final By moveDropdownList = By.cssSelector("[data-testid='move-dropdown-list']");

    private final By messageRows = By.cssSelector("[data-testid^='message-item:']");

    private final By starInactiveInRow = By.cssSelector("[data-testid='item-star-false']");

    public MailListPage(WebDriver driver) {
        super(driver);
    }

    public MailListPage waitUntilPageAvailable() {

        wait.until(ExpectedConditions.presenceOfElementLocated(searchToolbarInput));
        return this;
    }

    public MailListPage waitUntilLoaded() {
        return waitUntilPageAvailable();
    }

    public boolean hasEmails() {
        return !driver.findElements(messageRows).isEmpty();
    }

    public MailListPage searchBySubject(String subject) {

        if (subject == null || subject.isBlank()) {
            LOGGER.error("Attempted to search with an empty subject");
            throw new IllegalArgumentException(
                    "Search subject must not be empty.");
        }

        LOGGER.info("Searching for email with subject: {}", subject);

        wait.until(ExpectedConditions.elementToBeClickable(searchToolbarInput)).click();

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchDialogInput));
        searchInput.clear();
        searchInput.sendKeys(subject);

        wait.until(ExpectedConditions.elementToBeClickable(searchSubmitButton)).click();
        waitForMail(subject);

        LOGGER.info("Search completed for subject: {}", subject);

        return this;
    }

    public boolean isMailPresent(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }
        return longWait.until(driver -> findMail(subject) != null);
    }

    private WebElement findMail(String subject) {

        List<WebElement> mails = driver.findElements(messageRows);

        for (WebElement mail : mails) {
            if (mail.isDisplayed() && mail.getText().contains(subject)) {
                return mail;
            }
        }
        return null;
    }

    public boolean waitForMail(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }
        return longWait.until(driver -> findMail(subject) != null);
    }

    //in draft test mail should disappear from drafts
    public boolean mailEventuallyDisappears(String subject) {

        if (subject == null || subject.isBlank()) {
            return false;
        }
        return longWait.until(driver -> findMail(subject) == null);
    }

    private WebElement waitForMailItem(String subject) {

        return longWait.until(driver -> findMail(subject));
    }

    public MailListPage selectMailByCheckbox(String subject) {

        LOGGER.debug("Selecting email checkbox for subject: {}", subject);

        WebElement mail = waitForMailItem(subject);
        By checkbox = By.cssSelector("label.item-checkbox-label");
        wait.until(ExpectedConditions.elementToBeClickable(mail.findElement(checkbox))).click();
        return this;
    }

    public MailDetailPage openMailForReading(String subject) {

        LOGGER.info("Opening email for reading: {}", subject);

        waitForMailItem(subject).click();
        return new MailDetailPage(driver);
    }

    public ComposePage reopenDraft(String subject) {

        LOGGER.info("Reopening draft: {}", subject);

        waitForMailItem(subject).click();
        return new ComposePage(driver);
    }

    /**
     * Move selected email to the specified folder.
     */
    public MailListPage moveSelectedMailTo(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            LOGGER.error("Attempted to move email to an empty folder name");
            throw new IllegalArgumentException("Folder name must not be empty.");
        }

        LOGGER.info("Moving selected email to folder: {}", folderName);

        wait.until(ExpectedConditions.elementToBeClickable(moveToToolbarButton)).click();

        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(moveDropdownList));

        By folderOption = By.xpath(".//li[.//*[normalize-space()=" + xpathLiteral(folderName) + "]]");
        WebElement option = wait.until(d -> {
            List<WebElement> found = dropdown.findElements(folderOption);
            return found.isEmpty() ? null : found.get(0);
        });
        option.click();

        LOGGER.info("Email moved to folder: {}", folderName);

        return this;
    }

    public boolean waitForAtLeastOneMail() {
        return longWait.until(driver -> !driver.findElements(messageRows).isEmpty());
    }

    private WebElement findUnstarredMail(String subject) {

        List<WebElement> mails = driver.findElements(messageRows);
        for (WebElement mail : mails) {
            if (mail.isDisplayed() && mail.getText().contains(subject)
                    && !mail.findElements(starInactiveInRow).isEmpty()) {  return mail; }}
        return null;
    }

    public MailDetailPage openUnstarredMailForReading(String subject) {

        if (subject == null || subject.isBlank()) {
            LOGGER.error("Attempted to open unstarred email with an empty subject");
            throw new IllegalArgumentException("Subject must not be empty."); }

        LOGGER.info("Opening unstarred email for reading: {}", subject);

        WebElement mail = longWait.until(driver -> findUnstarredMail(subject));
        mail.click();
        return new MailDetailPage(driver);
    }

    private String xpathLiteral(String value) {

        if (!value.contains("'")) { return "'" + value + "'";}
        if (!value.contains("\"")) { return "\"" + value + "\"";}
        String[] parts = value.split("'", -1);
        StringBuilder result = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {result.append(", \"'\", ");}
            result.append("'").append(parts[i]).append("'"); }
        result.append(")");
        return result.toString();}}