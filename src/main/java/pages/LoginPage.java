package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AbstractPage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector( "button.button-large.button-solid-norm[type='submit']");

    private static final By INBOX = By.cssSelector("[data-testid='navigation-link:inbox']" );

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Opens Proton Mail login page.
     */
    public LoginPage openLoginForm(String loginUrl) {

        driver.get(loginUrl);

        try { wait.until( ExpectedConditions.visibilityOfElementLocated( USERNAME) ); }
        catch (TimeoutException e) {

            if (!driver.findElements(INBOX).isEmpty()) { return this; }
            driver.manage().deleteAllCookies();
            driver.get(loginUrl);
            wait.until( ExpectedConditions.visibilityOfElementLocated(USERNAME ) );  }

        return this;
    }

    /**
     * Login with username and password.
     */
    public SidebarPage submitCredentials( String username, String password ) {

        WebElement usernameField =wait.until( ExpectedConditions.visibilityOfElementLocated( USERNAME ) );
        usernameField.clear();
        usernameField.sendKeys(username);

        WebElement passwordField = wait.until( ExpectedConditions.visibilityOfElementLocated( PASSWORD ) );
        passwordField.clear();
        passwordField.sendKeys(password);


        WebElement loginButton = wait.until( ExpectedConditions.visibilityOfElementLocated( LOGIN_BUTTON ) );

        wait.until( driver -> loginButton.isDisplayed() && loginButton.isEnabled());

        try { loginButton.click(); }
        catch (ElementClickInterceptedException e) {
            wait.until( driver -> {
                        try { return driver.findElement(LOGIN_BUTTON).isDisplayed();
                        } catch (Exception ignored) { return false; } } );


            WebElement freshLoginButton = driver.findElement(LOGIN_BUTTON);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", freshLoginButton );
        }


        wait.until( ExpectedConditions.visibilityOfElementLocated(INBOX));

        return new SidebarPage(driver);
    }

    /**
     * Checks whether login form is visible.
     */
    public boolean isLoginFormVisible() {
        return driver.findElement(USERNAME).isDisplayed()&& driver.findElement(PASSWORD).isDisplayed();
    } }

