package pages;

import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AbstractPage {

    private static final Logger LOGGER = LogManager.getLogger(LoginPage.class);

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector( "button.button-large.button-solid-norm[type='submit']");

    private static final By INBOX = By.cssSelector("[data-testid='navigation-link:inbox']" );

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Opens Proton Mail login page */
    public LoginPage openLoginForm(String loginUrl) {

        LOGGER.info("Opening login page: {}", loginUrl);

        driver.get(loginUrl);

        try { wait.until( ExpectedConditions.visibilityOfElementLocated( USERNAME) ); }
        catch (TimeoutException e) {

            LOGGER.debug("Username field not visible yet, checking if already logged in");

            if (!driver.findElements(INBOX).isEmpty()) {
                LOGGER.info("Already logged in, inbox detected");
                return this;
            }

            LOGGER.debug("Clearing cookies and retrying login page load");
            driver.manage().deleteAllCookies();
            driver.get(loginUrl);
            wait.until( ExpectedConditions.visibilityOfElementLocated(USERNAME ) );  }

        return this;
    }

    /** Login with username and password. */
    public SidebarPage submitCredentials(String username, String password) {

        LOGGER.info("Submitting login credentials for user: {}", username);

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        usernameField.clear();
        usernameField.sendKeys(username);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD));
        passwordField.clear();
        passwordField.sendKeys(password);

        LOGGER.debug("Clicking login button");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(INBOX));
            LOGGER.info("Login successful for user: {}", username);
        } catch (TimeoutException e) {
            LOGGER.error("Login failed for user: {}", username);
            throw e;
        }

        return new SidebarPage(driver);
    }

    /** Login using a User business object. */
    public SidebarPage submitCredentials(User user) {
        return submitCredentials(user.getEmail(), user.getPassword());
    }

    /** Checks whether login form is visible( to ensure signing out after test) */
    public boolean isLoginFormVisible() {
        return driver.findElement(USERNAME).isDisplayed()&& driver.findElement(PASSWORD).isDisplayed();
    } }