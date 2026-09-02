package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ComposePage;
import pages.LoginPage;
import pages.MailListPage;
import pages.SidebarPage;

/**
 * Scenario 1:
 *
 * Login
 * -> create draft
 * -> verify draft
 * -> reopen
 * -> verify content
 * -> send
 * -> verify Sent
 * -> logout.
 */
public class EmailDraftAndSendTest extends BaseTest {

    private static final String ADDRESSEE =
            "recipient@example.com";

    private static final String SUBJECT =
            "Draft Test "
                    + System.currentTimeMillis();

    private static final String BODY =
            "This is an automated test message body.";

    @Test
    public void composeSaveDraftAndSendEmail() {

        /*
         * LOGIN
         */
        SidebarPage sidebar =
                new LoginPage(driver)
                        .openLoginForm(LOGIN_URL)
                        .submitCredentials(
                                TEST_EMAIL,
                                TEST_PASSWORD
                        );


        /*
         * OPEN COMPOSER
         */
        ComposePage composer =
                sidebar.startNewMessage();

        /*
         * RECIPIENT
         */
        composer.addressTo(ADDRESSEE);

        /*
         * SUBJECT
         */
        composer.giveSubject(SUBJECT);

        /*
         * BODY
         */
        composer.writeBody(BODY);

        /*
         * VERIFY COMPOSED CONTENT
         */
        composer.waitUntilContentEntered(
                ADDRESSEE,
                SUBJECT,
                BODY
        );

        /*
         * SAVE AS DRAFT
         */
        composer.closeAndKeepAsDraft();

        /*
         * OPEN DRAFTS
         */
        sidebar.goToDrafts();

        MailListPage mailList =
                new MailListPage(driver);

        Assert.assertTrue(
                mailList.isMailPresent(SUBJECT),
                "Draft with subject '" + SUBJECT
                        + "' should be present in Drafts"
        );

        /*
         * REOPEN DRAFT
         */
        ComposePage reopenedDraft =
                mailList.reopenDraft(SUBJECT);

        /*
         * WAIT FOR REOPENED DRAFT
         */
        reopenedDraft.waitUntilContentEntered(
                ADDRESSEE,
                SUBJECT,
                BODY
        );

        /*
         * VERIFY RECIPIENT
         */
        Assert.assertEquals(
                reopenedDraft.recipientValue(ADDRESSEE),
                ADDRESSEE,
                "Addressee should match"
        );

        /*
         * VERIFY SUBJECT
         */
        Assert.assertEquals(
                reopenedDraft.subjectValue(),
                SUBJECT,
                "Subject should match"
        );

        /*
         * VERIFY BODY
         */
        Assert.assertTrue(
                reopenedDraft.bodyValue().contains(BODY),
                "Body should match"
        );

        /*
         * SEND
         */
        reopenedDraft.dispatchMail();

        /*
         * GO BACK TO DRAFTS
         */
        sidebar.goToDrafts();

        mailList =
                new MailListPage(driver);

        /*
         * VERIFY DRAFT DISAPPEARED
         */
        Assert.assertTrue(
                mailList.mailEventuallyDisappears(SUBJECT),
                "Mail should no longer be present in Drafts "
                        + "after sending"
        );

        /*
         * GO TO SENT
         */
        sidebar.goToSent();

        mailList =
                new MailListPage(driver);

        /*
         * VERIFY SENT
         */
        Assert.assertTrue(
                mailList.isMailPresent(SUBJECT),
                "Mail should be present in Sent after sending"
        );

        /*
         * LOGOUT
         */
        LoginPage loginPage =
                sidebar.signOut();

        Assert.assertTrue(
                loginPage.isLoginFormVisible(),
                "Login form should be visible after signing out"
        );
    }
}
