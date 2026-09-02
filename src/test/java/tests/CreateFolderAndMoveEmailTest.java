package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ComposePage;
import pages.LoginPage;
import pages.MailListPage;
import pages.SidebarPage;

/**
 * Scenario 3:
 *
 * Login
 * -> send a new email to yourself
 * -> open Inbox
 * -> verify email
 * -> create folder
 * -> verify folder
 * -> select email
 * -> move email to folder
 * -> open folder
 * -> verify folder contains exactly one email
 * -> logout
 */
public class CreateFolderAndMoveEmailTest extends BaseTest {

    /**
     * Unique folder name for every test run.
     */
    private static final String FOLDER_NAME =
            "TestFolder_" +
                    System.currentTimeMillis();

    /**
     * Unique email subject for every test run.
     */
    private static final String EMAIL_SUBJECT =
            "QA Automation " +
                    System.currentTimeMillis();


    private static final String EMAIL_BODY =
            "This email was created by the automation test.";

    @Test
    public void createFolderAndMoveEmailIntoIt() {

        /*
         * 1. Login
         */
        SidebarPage sidebar =
                new LoginPage(driver)
                        .openLoginForm(LOGIN_URL)
                        .submitCredentials(
                                TEST_EMAIL,
                                TEST_PASSWORD
                        );

        /*
         * 2. Create email
         */
        ComposePage compose =
                sidebar.startNewMessage();

        compose
                .addressTo(TEST_EMAIL)
                .giveSubject(EMAIL_SUBJECT)
                .writeBody(EMAIL_BODY)
                .waitUntilContentEntered(
                        TEST_EMAIL,
                        EMAIL_SUBJECT,
                        EMAIL_BODY
                );

        /*
         * Send email to ourselves.
         */
        compose.dispatchMail();

        /*
         * 3. Open Inbox
         */
        MailListPage inbox =
                sidebar.goToInbox();

        /*
         * 4. Verify newly sent email exists
         */
        Assert.assertTrue(
                inbox.waitForMail(EMAIL_SUBJECT),
                "Inbox should contain the newly sent email: '" +
                        EMAIL_SUBJECT +
                        "'"
        );

        /*
         * 5. Create folder
         */
        sidebar =
                sidebar.createFolder(FOLDER_NAME);

        /*
         * 6. Verify folder was created
         */
        Assert.assertTrue(
                sidebar.isFolderListed(FOLDER_NAME),
                "New folder '" +
                        FOLDER_NAME +
                        "' should appear in the sidebar"
        );

        /*
         * 7. Return to Inbox
         */
        inbox =
                sidebar.goToInbox();



        /*
         * 8. Select email
         */
        inbox.selectMailByCheckbox(
                EMAIL_SUBJECT
        );

        /*
         * 9. Move email to folder
         */
        inbox.moveSelectedMailTo(
                FOLDER_NAME
        );

        /*
         * 10. Open destination folder
         */
        MailListPage folder =
                sidebar.goToFolder(FOLDER_NAME);

        /*
         * 11. Verify folder contains a message
         */
        Assert.assertTrue(
                folder.waitForAtLeastOneMail(),
                "Folder '" +
                        FOLDER_NAME +
                        "' should contain the moved email"
        );

        /*
         * 12. Verify exactly one message
         */
        Assert.assertEquals(
                folder.visibleMailCount(),
                1,
                "Folder '" +
                        FOLDER_NAME +
                        "' should contain exactly one email"
        );

        /*
         * 13. Logout
         */
        LoginPage loginPage =
                sidebar.signOut();

        /*
         * 14. Verify logout
         */
        Assert.assertTrue(
                loginPage.isLoginFormVisible(),
                "Login form should be visible after signing out"
        );
    }
}
