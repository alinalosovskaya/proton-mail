package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ComposePage extends AbstractPage {

    private final By roosterIframe =
            By.cssSelector(
                    "iframe[data-testid='rooster-iframe']"
            );

    private final By bodyEditor =
            By.cssSelector(
                    "#rooster-editor[contenteditable='true']"
            );

    private final By toField =
            By.cssSelector(
                    "[data-testid='composer:to'], " +
                            "input[placeholder*='To' i], " +
                            "input[aria-label*='To' i]"
            );

    private final By subjectField =
            By.cssSelector(
                    "[data-testid='composer:subject'], " +
                            "input[placeholder*='Subject' i], " +
                            "input[aria-label*='Subject' i]"
            );

    private final By sendButton =
            By.cssSelector(
                    "[data-testid='composer:send-button'], " +
                            "button[aria-label*='Send' i]"
            );

    private final By closeButton =
            By.cssSelector(
                    "[data-testid='composer:close-button'], " +
                            "button[aria-label*='Close' i]"
            );

    public ComposePage(WebDriver driver) {
        super(driver);
    }

    public ComposePage waitUntilReady() {

        switchToMainDocument();

        longWait.until(
                ExpectedConditions.presenceOfElementLocated(
                        roosterIframe
                )
        );

        switchToRoosterIframe();

        longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        bodyEditor
                )
        );

        switchToMainDocument();

        return this;
    }

    private void switchToMainDocument() {
        driver.switchTo().defaultContent();
    }

    private void switchToRoosterIframe() {

        switchToMainDocument();

        WebElement iframe =
                longWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                roosterIframe
                        )
                );

        driver.switchTo().frame(iframe);
    }

    private WebElement waitForToField() {

        switchToMainDocument();

        return longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        toField
                )
        );
    }

    private WebElement waitForSubjectField() {

        switchToMainDocument();

        return longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        subjectField
                )
        );
    }

    public ComposePage addressTo(String recipient) {

        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Recipient must not be null or empty."
            );
        }

        waitUntilReady();

        WebElement field =
                waitForToField();

        try {
            field.click();

        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].focus();",
                            field
                    );
        }

        field.clear();
        field.sendKeys(recipient);
        field.sendKeys(Keys.TAB);

        switchToMainDocument();

        return this;
    }

    public ComposePage giveSubject(String subject) {

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Subject must not be null or empty."
            );
        }

        waitUntilReady();

        WebElement field =
                waitForSubjectField();

        try {
            field.click();

        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].focus();",
                            field
                    );
        }

        field.clear();
        field.sendKeys(subject);

        switchToMainDocument();

        return this;
    }

    /**
     * Put cursor immediately before Proton's automatic signature.
     */
    private void focusBeforeSignature() {

        switchToRoosterIframe();

        WebElement editor =
                longWait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                bodyEditor
                        )
                );

        ((JavascriptExecutor) driver)
                .executeScript(
                        """
                        const editor = arguments[0];

                        editor.focus();

                        const signature =
                                editor.querySelector(
                                        '.protonmail_signature_block'
                                );

                        const selection =
                                window.getSelection();

                        const range =
                                document.createRange();

                        if (signature) {
                            range.setStartBefore(signature);
                            range.collapse(true);
                        } else {
                            range.selectNodeContents(editor);
                            range.collapse(true);
                        }

                        selection.removeAllRanges();
                        selection.addRange(range);
                        """,
                        editor
                );
    }

    public ComposePage writeBody(String body) {

        if (body == null) {
            throw new IllegalArgumentException(
                    "Body must not be null."
            );
        }

        waitUntilReady();
        focusBeforeSignature();

        switchToRoosterIframe();

        WebElement editor =
                longWait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                bodyEditor
                        )
                );

        ((JavascriptExecutor) driver)
                .executeScript(
                        """
                        arguments[0].scrollIntoView({
                            block: 'center',
                            inline: 'nearest'
                        });
                        """,
                        editor
                );

        try {
            editor.sendKeys(body);

        } catch (ElementNotInteractableException e) {

            focusBeforeSignature();
            switchToRoosterIframe();

            editor =
                    longWait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    bodyEditor
                            )
                    );

            editor.sendKeys(body);
        }

        WebDriverWait bodyWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(15)
                );

        bodyWait.until(driver -> {

            try {

                switchToRoosterIframe();

                WebElement current =
                        driver.findElement(bodyEditor);

                return getEditorText(current)
                        .contains(body);

            } catch (
                    StaleElementReferenceException |
                    NoSuchElementException e
            ) {

                return false;

            } finally {

                switchToMainDocument();
            }
        });

        switchToMainDocument();

        return this;
    }

    private String getEditorText(WebElement editor) {

        String text =
                editor.getText();

        if (text != null && !text.trim().isEmpty()) {
            return text;
        }

        String innerText =
                (String) ((JavascriptExecutor) driver)
                        .executeScript(
                                "return arguments[0].innerText || '';",
                                editor
                        );

        if (innerText != null
                && !innerText.trim().isEmpty()) {
            return innerText;
        }

        String textContent =
                (String) ((JavascriptExecutor) driver)
                        .executeScript(
                                "return arguments[0].textContent || '';",
                                editor
                        );

        return textContent == null
                ? ""
                : textContent;
    }

    /**
     * Checks whether the exact expected recipient is present.
     */
    private boolean isRecipientPresent(
            String expectedRecipient
    ) {

        switchToMainDocument();

        for (WebElement input :
                driver.findElements(toField)) {

            try {

                String value =
                        input.getAttribute("value");

                if (expectedRecipient.equals(value)
                        || (value != null
                        && value.contains(expectedRecipient))) {

                    return true;
                }

            } catch (StaleElementReferenceException ignored) {
            }
        }

        return (Boolean) ((JavascriptExecutor) driver)
                .executeScript(
                        """
                        const expected = arguments[0];

                        for (const element of document.querySelectorAll('*')) {

                            if (element.offsetParent === null) {
                                continue;
                            }

                            const value =
                                    element.getAttribute('value');

                            const aria =
                                    element.getAttribute('aria-label');

                            const title =
                                    element.getAttribute('title');

                            const text =
                                    (element.innerText || '').trim();

                            if (value === expected ||
                                (value && value.includes(expected)) ||
                                (aria && aria.includes(expected)) ||
                                (title && title.includes(expected)) ||
                                text === expected) {

                                return true;
                            }
                        }

                        return false;
                        """,
                        expectedRecipient
                );
    }

    public ComposePage waitUntilContentEntered(
            String recipient,
            String subject,
            String body
    ) {

        WebDriverWait contentWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(15)
                );

        contentWait.until(driver -> {

            try {

                switchToMainDocument();

                String actualSubject =
                        driver.findElement(subjectField)
                                .getAttribute("value");

                boolean subjectOk =
                        actualSubject != null
                                && actualSubject.contains(subject);

                boolean recipientOk =
                        isRecipientPresent(recipient);

                switchToRoosterIframe();

                String actualBody =
                        getEditorText(
                                driver.findElement(bodyEditor)
                        );

                boolean bodyOk =
                        actualBody.contains(body);

                return recipientOk
                        && subjectOk
                        && bodyOk;

            } catch (
                    StaleElementReferenceException |
                    NoSuchElementException e
            ) {

                return false;

            } finally {

                switchToMainDocument();
            }
        });

        return this;
    }

    public MailListPage closeAndKeepAsDraft() {

        switchToMainDocument();

        WebElement button =
                longWait.until(
                        ExpectedConditions.elementToBeClickable(
                                closeButton
                        )
                );

        try {
            button.click();

        } catch (ElementNotInteractableException e) {

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].click();",
                            button
                    );
        }

        return new MailListPage(driver);
    }

    public MailListPage dispatchMail() {

        switchToMainDocument();

        WebElement button =
                longWait.until(
                        ExpectedConditions.elementToBeClickable(
                                sendButton
                        )
                );

        try {
            button.click();

        } catch (ElementNotInteractableException e) {

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].click();",
                            button
                    );
        }

        return new MailListPage(driver);
    }

    /**
     * Returns the expected recipient if it is present.
     */
    public String recipientValue(
            String expectedRecipient
    ) {

        if (expectedRecipient == null
                || expectedRecipient.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Expected recipient must not be null or empty."
            );
        }

        return isRecipientPresent(expectedRecipient)
                ? expectedRecipient
                : "";
    }

    public String subjectValue() {

        switchToMainDocument();

        return longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        subjectField
                )
        ).getAttribute("value");
    }

    public String bodyValue() {

        switchToRoosterIframe();

        WebElement editor =
                longWait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                bodyEditor
                        )
                );

        String text =
                getEditorText(editor);

        switchToMainDocument();

        return text;
    }
}
