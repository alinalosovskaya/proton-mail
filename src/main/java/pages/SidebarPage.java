package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebElement;


public class SidebarPage extends AbstractPage {

    private final By inboxLink =
            By.cssSelector("[data-testid='navigation-link:inbox']");

    private final By draftsLink =
            By.cssSelector("[data-testid='navigation-link:all-drafts']");

    private final By sentLink =
            By.cssSelector("[data-testid='navigation-link:all-sent']");

    private final By starredLink =
            By.cssSelector("[data-testid='navigation-link:starred']");

    private final By composeButton =
            By.cssSelector("[data-testid='sidebar:compose']");

    private final By createFolderButton =
            By.cssSelector("[data-testid='navigation-link:add-folder']");

    private final By folderNameInput =
            By.cssSelector(
                    "[data-testid='label/folder-modal:name']"
            );

    private final By saveFolderButton =
            By.cssSelector(
                    "[data-testid='label-modal:save']"
            );

    private final By accountMenuButton =
            By.cssSelector(
                    "[data-testid='heading:userdropdown']"
            );

    private final By signOutButton =
            By.cssSelector(
                    "[data-testid='userdropdown:button:logout']"
            );

    private final By usernameInput =
            By.id("username");


    public SidebarPage(WebDriver driver) {
        super(driver);
    }


    /**
     * Open Inbox.
     */
    public MailListPage goToInbox() {

        clickInSidebar(inboxLink);

        MailListPage page =
                new MailListPage(driver);

        page.waitUntilLoaded();

        return page;
    }


    /**
     * Open Drafts.
     */
    public MailListPage goToDrafts() {

        clickInSidebar(draftsLink);

        MailListPage page =
                new MailListPage(driver);

        page.waitUntilLoaded();

        return page;
    }


    /**
     * Open Sent.
     */
    public MailListPage goToSent() {

        clickInSidebar(sentLink);

        MailListPage page =
                new MailListPage(driver);

        page.waitUntilLoaded();

        return page;
    }


    /**
     * Open Starred.
     */
    public MailListPage goToStarred() {

        clickInSidebar(starredLink);

        MailListPage page =
                new MailListPage(driver);

        page.waitUntilLoaded();

        return page;
    }


    /**
     * Open new message composer.
     */
    public ComposePage startNewMessage() {

        clickInSidebar(composeButton);

        ComposePage page =
                new ComposePage(driver);

        page.waitUntilReady();

        return page;
    }


    /**
     * Create a new folder.
     */
    public SidebarPage createFolder(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Folder name must not be empty."
            );
        }

        clickInSidebar(createFolderButton);

        longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        folderNameInput
                )
        ).sendKeys(folderName);

        clickInSidebar(saveFolderButton);

        waitForFolder(folderName);

        return this;
    }


    /**
     * Exact locator for a folder.
     */
    private By folderLocator(String folderName) {

        return By.cssSelector(
                "a.navigation-link[title='" +
                        cssEscape(folderName) +
                        "']"
        );
    }


    /**
     * Wait until the folder appears in sidebar.
     */
    private void waitForFolder(String folderName) {

        longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        folderLocator(folderName)
                )
        );
    }


    /**
     * Check whether the folder is listed in sidebar.
     */
    public boolean isFolderListed(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            return false;
        }

        try {
            waitForFolder(folderName);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Open a folder.
     */
    public MailListPage goToFolder(String folderName) {

        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Folder name must not be empty."
            );
        }

        clickInSidebar(folderLocator(folderName));

        MailListPage page =
                new MailListPage(driver);

        page.waitUntilPageAvailable();

        return page;
    }


    /**
     * Sign out.
     */
    public LoginPage signOut() {

        clickInSidebar(accountMenuButton);

        clickInSidebar(signOutButton);

        longWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        usernameInput
                )
        );

        return new LoginPage(driver);
    }


    /**
     * Click an element after it becomes clickable.
     */
    private void clickInSidebar(By locator) {

        WebElement element = longWait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        utils.Highlighter.highlight(driver, element);

        element.click();
    }


    /**
     * Escape a value used inside a CSS attribute selector.
     */
    private static String cssEscape(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }
}
