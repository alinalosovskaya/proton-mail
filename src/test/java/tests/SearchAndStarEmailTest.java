package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MailDetailPage;
import pages.MailListPage;
import pages.SidebarPage;

/**
 * Scenario 2:
 * search ->
 * open email ->
 * star ->
 * verify in Starred.
 */
public class SearchAndStarEmailTest extends BaseTest {

    private static final String SEARCH_TERM = "QA Automation";

    @Test
    public void searchEmailAndMarkAsStarred() {

        SidebarPage sidebar = new LoginPage(driver).openLoginForm(LOGIN_URL).submitCredentials(TEST_USER);

        MailListPage inbox = sidebar.goToInbox();

        Assert.assertTrue(inbox.hasEmails(),"Inbox should contain at least one email before searching");

        /* Search. */
        MailListPage searchResults = inbox.searchBySubject(SEARCH_TERM);

        /* Verify result. */
        Assert.assertTrue(searchResults.isMailPresent(SEARCH_TERM),
                "Search results should contain an email matching '" + SEARCH_TERM + "'");

        /* Open an unstarred mail and verify it is not starred yet. */
        MailDetailPage mailDetail = searchResults.openUnstarredMailForReading(SEARCH_TERM);

        Assert.assertFalse(mailDetail.isStarActive(), "Star icon should not be active before starring the email");

        /*  Star it. */
        mailDetail.toggleStar();

        /* Verify star. */
        Assert.assertTrue(mailDetail.isStarActive(), "Star icon should be active after clicking it");

        /* Go to Starred. */
        MailListPage starred = sidebar.goToStarred();

        Assert.assertTrue(starred.isMailPresent(SEARCH_TERM), "Starred folder should contain the starred email");
    }
}