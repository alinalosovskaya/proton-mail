package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import model.Email;

public class ComposePage extends AbstractPage {

    private static final Logger LOGGER = LogManager.getLogger(ComposePage.class);

    private final By roosterIframe = By.cssSelector("iframe[data-testid='rooster-iframe']");
    private final By bodyEditor = By.cssSelector("#rooster-editor[contenteditable='true']");
    private final By toField = By.cssSelector(
            "[data-testid='composer:to'], input[placeholder*='To' i], input[aria-label*='To' i]");
    private final By subjectField = By.cssSelector(
            "[data-testid='composer:subject'], input[placeholder*='Subject' i], input[aria-label*='Subject' i]");
    private final By sendButton = By.cssSelector("[data-testid='composer:send-button'], button[aria-label*='Send' i]");
    private final By closeButton = By.cssSelector("[data-testid='composer:close-button'], button[aria-label*='Close' i]");
    private final By recipientChipLabel = By.cssSelector("[data-testid='composer-addresses-item-label']");

    private final By recipientSummaryField = By.cssSelector("[data-testid='composer:address']");

    public ComposePage(WebDriver driver) { super(driver); }

    public ComposePage waitUntilReady() {
        LOGGER.debug("Waiting for composer to be ready");
        switchToMainDocument();
        longWait.until(ExpectedConditions.presenceOfElementLocated(roosterIframe));
        switchToRoosterIframe();
        longWait.until(ExpectedConditions.visibilityOfElementLocated(bodyEditor));
        switchToMainDocument();
        return this;
    }

    public ComposePage fill(Email email) {

        LOGGER.info("Filling composer with email: recipient={}, subject={}", email.getRecipient(), email.getSubject());

        addressTo(email.getRecipient());
        giveSubject(email.getSubject());
        writeBody(email.getBody());

        return this;
    }

    private void switchToMainDocument() {
        driver.switchTo().defaultContent();
    }

    private void switchToRoosterIframe() {
        switchToMainDocument();
        WebElement iframe = longWait.until(ExpectedConditions.presenceOfElementLocated(roosterIframe));
        driver.switchTo().frame(iframe);
    }

    public ComposePage addressTo(String recipient) {
        if (recipient == null || recipient.trim().isEmpty()) {
            LOGGER.error("Attempted to set an empty recipient");
            throw new IllegalArgumentException("Recipient must not be null or empty.");
        }

        LOGGER.info("Setting recipient: {}", recipient);

        switchToMainDocument();
        WebElement field = longWait.until(ExpectedConditions.visibilityOfElementLocated(toField));
        field.click();
        field.clear();
        field.sendKeys(recipient);
        field.sendKeys(Keys.ENTER);
        LOGGER.debug("Recipient chips after ENTER: {}", driver.findElements(recipientChipLabel).size());

        return this;
    }

    public ComposePage giveSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            LOGGER.error("Attempted to set an empty subject");
            throw new IllegalArgumentException("Subject must not be null or empty.");
        }

        LOGGER.info("Setting subject: {}", subject);

        switchToMainDocument();
        WebElement field = longWait.until(ExpectedConditions.visibilityOfElementLocated(subjectField));
        field.click();
        field.clear();
        field.sendKeys(subject);

        return this;
    }

    /**
     * Put cursor immediately before Proton's automatic signature.
     */
    public ComposePage writeBody(String body) {

        LOGGER.info("Writing email body ({} characters)", body.length());

        switchToRoosterIframe();
        WebElement editor = longWait.until(ExpectedConditions.visibilityOfElementLocated(bodyEditor));

        editor.click();
        editor.sendKeys(Keys.chord(Keys.CONTROL, Keys.HOME));
        editor.sendKeys(body);

        WebDriverWait bodyWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        bodyWait.until(driver -> {
            try {
                switchToRoosterIframe();
                return getEditorText(driver.findElement(bodyEditor)).contains(body);
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return false;
            } finally {
                switchToMainDocument();
            }
        });

        switchToMainDocument();
        return this;
    }

    /** Reads text from a contenteditable editor. */
    private String getEditorText(WebElement editor) {
        String text = editor.getText();
        if (text != null && !text.trim().isEmpty()) return text;

        String innerText = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].innerText || '';", editor);
        if (innerText != null && !innerText.trim().isEmpty()) return innerText;

        String textContent = (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].textContent || '';", editor);
        return textContent == null ? "" : textContent;
    }

    /** Checks whether the expected recipient is present as a confirmed */
    private boolean isRecipientPresent(String expectedRecipient) {
        switchToMainDocument();

        List<WebElement> summaries = driver.findElements(recipientSummaryField);
        if (!((List<?>) summaries).isEmpty()) {
            WebElement summary = summaries.get(0);
            String title = summary.findElements(By.cssSelector("[title]")).stream()
                    .map(el -> el.getAttribute("title"))
                    .filter(t -> t != null && !t.isBlank())
                    .findFirst()
                    .orElse(null);

            if (title != null && (expectedRecipient.equals(title) || title.contains(expectedRecipient))) {
                return true;
            }

            String text = summary.getText();
            if (text != null && text.contains(expectedRecipient)) { return true; }
        }


        for (WebElement chip : driver.findElements(recipientChipLabel)) {
            try {
                String text = chip.getText();
                if (expectedRecipient.equals(text) || (text != null && text.contains(expectedRecipient))) {
                    return true; }
            } catch (StaleElementReferenceException ignored) { }}

        return false;
    }

    public ComposePage waitUntilContentEntered(String recipient, String subject, String body) {

        LOGGER.debug("Waiting until composer content is fully entered");

        WebDriverWait contentWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        contentWait.until(driver -> {
            try {
                switchToMainDocument();
                String actualSubject = driver.findElement(subjectField).getAttribute("value");
                boolean subjectOk = actualSubject != null && actualSubject.contains(subject);
                boolean recipientOk = isRecipientPresent(recipient);

                switchToRoosterIframe();
                boolean bodyOk = getEditorText(driver.findElement(bodyEditor)).contains(body);

                return recipientOk && subjectOk && bodyOk;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return false;
            } finally {
                switchToMainDocument();
            }
        });

        return this;
    }

    public MailListPage closeAndKeepAsDraft() {
        LOGGER.info("Closing composer and keeping as draft");
        switchToMainDocument();
        longWait.until(ExpectedConditions.elementToBeClickable(closeButton)).click();
        return new MailListPage(driver);
    }

    public MailListPage dispatchMail() {
        LOGGER.info("Sending email");
        switchToMainDocument();
        longWait.until(ExpectedConditions.elementToBeClickable(sendButton)).click();
        return new MailListPage(driver);
    }

    /** Returns the expected recipient if it is present. */
    public String recipientValue(String expectedRecipient) {
        return isRecipientPresent(expectedRecipient) ? expectedRecipient : "";
    }

    public String subjectValue() {
        switchToMainDocument();
        return longWait.until(ExpectedConditions.visibilityOfElementLocated(subjectField)).getAttribute("value");
    }

    public String bodyValue() {
        switchToRoosterIframe();
        WebElement editor = longWait.until(ExpectedConditions.visibilityOfElementLocated(bodyEditor));
        String text = getEditorText(editor);
        switchToMainDocument();
        return text;
    }
}