package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class SidebarPage extends AbstractPage {

    private static final Logger LOGGER = LogManager.getLogger(SidebarPage.class);

    private final By inboxLink = By.cssSelector("[data-testid='navigation-link:inbox']");

    private final By draftsLink = By.cssSelector("[data-testid='navigation-link:all-drafts']");

    private final By sentLink = By.cssSelector("[data-testid='navigation-link:all-sent']");

    private final By starredLink = By.cssSelector("[data-testid='navigation-link:starred']");

    private final By composeButton = By.cssSelector("[data-testid='sidebar:compose']");

    private final By createFolderButton = By.cssSelector("[data-testid='navigation-link:add-folder']");

    private final By folderNameInput = By.cssSelector( "[data-testid='label/folder-modal:name']");

    private final By saveFolderButton = By.cssSelector("[data-testid='label-modal:save']" );

    private final By accountMenuButton = By.cssSelector("[data-testid='heading:userdropdown']" );

    private final By signOutButton = By.cssSelector("[data-testid='userdropdown:button:logout']" );

    private final By usernameInput = By.id("username");


    public SidebarPage(WebDriver driver) {
        super(driver);
    }


    /** Open Inbox. */
    public MailListPage goToInbox() {

        LOGGER.info("Navigating to Inbox");
        clickInSidebar(inboxLink);
        MailListPage page = new MailListPage(driver);
        page.waitUntilLoaded();
        return page;
    }


    /** Open Drafts. */
    public MailListPage goToDrafts() {

        LOGGER.info("Navigating to Drafts");
        clickInSidebar(draftsLink);
        MailListPage page = new MailListPage(driver);
        page.waitUntilLoaded();
        return page;
    }


    /** Open Sent. */
    public MailListPage goToSent() {

        LOGGER.info("Navigating to Sent");
        clickInSidebar(sentLink);
        MailListPage page = new MailListPage(driver);
        page.waitUntilLoaded();
        return page;
    }


    /** Open Starred. */
    public MailListPage goToStarred() {

        LOGGER.info("Navigating to Starred");
        clickInSidebar(starredLink);
        MailListPage page = new MailListPage(driver);
        page.waitUntilLoaded();
        return page;
    }


    /** Open new message composer. */
    public ComposePage startNewMessage() {

        LOGGER.info("Opening new message composer");
        clickInSidebar(composeButton);
        ComposePage page = new ComposePage(driver);
        page.waitUntilReady();
        return page;
    }


    /** Create a new folder. */
    public SidebarPage createFolder(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            LOGGER.error("Attempted to create a folder with an empty name");
            throw new IllegalArgumentException("Folder name must not be empty." );}

        LOGGER.info("Creating folder: {}", folderName);

        clickInSidebar(createFolderButton);
        longWait.until( ExpectedConditions.visibilityOfElementLocated( folderNameInput )).sendKeys(folderName);
        clickInSidebar(saveFolderButton);
        longWait.until( ExpectedConditions.visibilityOfElementLocated(folderLocator(folderName)));

        LOGGER.info("Folder created successfully: {}", folderName);

        return this;
    }


    /** Exact locator for a folder. */
    private By folderLocator(String folderName) {
        return By.cssSelector("a.navigation-link[title='" + folderName + "']" );  }



    /** Check whether the folder is listed in sidebar. */
    public boolean isFolderListed(String folderName) {

        if (folderName == null || folderName.isBlank()) { return false; }

        return longWait.until(driver ->
                driver.findElements(folderLocator(folderName)).stream().anyMatch(WebElement::isDisplayed));
    }


    /** Open a folder. */
    public MailListPage goToFolder(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            LOGGER.error("Attempted to open a folder with an empty name");
            throw new IllegalArgumentException("Folder name must not be empty." );}

        LOGGER.info("Opening folder: {}", folderName);
        clickInSidebar(folderLocator(folderName));
        MailListPage page = new MailListPage(driver);
        page.waitUntilPageAvailable();
        return page;
    }


    /** Sign out. */
    public LoginPage signOut() {

        LOGGER.info("Signing out");
        clickInSidebar(accountMenuButton);
        clickInSidebar(signOutButton);
        longWait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput ) );
        LOGGER.info("Signed out successfully");
        return new LoginPage(driver);
    }


    /** Click an element after it becomes clickable. */
    private void clickInSidebar(By locator) {

        LOGGER.debug("Clicking element: {}", locator);
        WebElement element = longWait.until(ExpectedConditions.elementToBeClickable(locator));
        utils.Highlighter.highlight(driver, element);
        element.click();
    }

}