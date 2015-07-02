package org.alfresco.share.util;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.GoogleDocsAuthorisation;
import org.alfresco.po.share.site.document.GoogleDocsDiscardChanges;
import org.alfresco.po.share.site.document.GoogleDocsRenamePage;
import org.alfresco.po.share.site.document.GoogleDocsUpdateFilePage;
import org.alfresco.po.share.site.document.GoogleSignUpPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ShareUserGoogleDocs extends AbstractCloudSyncTest
{
    public static String googleURL = "https://accounts.google.com";
    public static String googlePlusURL = "https://plus.google.com";

    private static Log logger = LogFactory.getLog(ShareUserGoogleDocs.class);

    public ShareUserGoogleDocs()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * This method provides the user to login into edit google docs page through
     * google authorization.
     *
     * @param drone WebDrone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signIntoEditGoogleDocFromDetailsPage(WebDrone drone) throws InterruptedException
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        GoogleDocsAuthorisation googleAuthorisationPage = detailsPage.editInGoogleDocs().render();
        return signInGoogleDocs(drone, googleAuthorisationPage).render();
    }

    /**
     * This method provides the user to login into edit google docs page through
     * google authorization.
     *
     * @param drone WebDrone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signIntoResumeEditGoogleDocFromDetailsPage(WebDrone drone) throws InterruptedException
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        GoogleDocsAuthorisation googleAuthorisationPage = detailsPage.resumeEditInGoogleDocs().render();
        return signInGoogleDocs(drone, googleAuthorisationPage);
    }

    /**
     * This method provides edit google docs page.
     *
     * @param drone WebDrone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage openEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        EditInGoogleDocsPage googleDocsPage = detailsPage.editInGoogleDocs().render();
        if (isGoogleDocsV3)
        {
            googleDocsPage = switchToGoogleWindow(drone).render();
        }
        return googleDocsPage.render();
    }

    /**
     * This method provides the user to login into create google docs with given
     * filename through google authorization. And Saves the document return to
     * the document library page. User should be already logged
     *
     * @param drone       WebDrone
     * @param fileName    String
     * @param contentType ContentType
     * @return DocumentLibraryPage
     * @throws Exception
     */
    public static DocumentLibraryPage createAndSavegoogleDocBySignIn(WebDrone drone, String fileName, ContentType contentType) throws Exception
    {
        DocumentLibraryPage docLibPage;
        String docTitle = "";
        EditInGoogleDocsPage googleDocsPage = createGoogleDocWithoutSave(drone, fileName, contentType);
        if (isGoogleDocsV3)
        {
            switch (contentType)
            {
                case GOOGLEDOCS:
                    docTitle = "Untitled Document.docx";
                    break;
                case GOOGLESPREADSHEET:
                    docTitle = "Untitled Document.xlsx";
                    break;
                case GOOGLEPRESENTATION:
                    docTitle = "Untitled Document.pptx";
                    break;
            }
            closeAndSwitchToShare(drone).render();
            Thread.sleep(10000);
            docLibPage = ShareUser.getSharePage(drone).render();
            docLibPage.render();
            try
            {
                docLibPage.getFileDirectoryInfo(docTitle).selectCheckInGoogleDoc();
            }
            catch (TimeoutException e)
            {
                throw new PageOperationException("Check-in didn't finish within the timeout");
            }
        }
        else
        {
            docLibPage = googleDocsPage.selectSaveToAlfresco().render();
        }
        return docLibPage.render();
    }

    public static EditInGoogleDocsPage createGoogleDocWithoutSave(WebDrone drone, String fileName, ContentType contentType) throws Exception
    {
        DocumentLibraryPage docLibPage = ShareUser.getSharePage(drone).render();

        GoogleDocsAuthorisation googleAuthorisationPage = docLibPage.getNavigation().selectCreateContent(contentType).render();
        googleAuthorisationPage.render();

        EditInGoogleDocsPage googleDocsPage = signInGoogleDocs(drone, googleAuthorisationPage);
        googleDocsPage.setGoogleCreate(true);

        if (isGoogleDocsV3)
        {
            googleDocsPage = switchToGoogleWindow(drone);
        }
        if (!(fileName == null))
        {
            return renameGoogleDocName(fileName, googleDocsPage);
        }
        throw new PageOperationException("Filename param must not be null : error creating GoogleDoc");
    }

    /**
     * This method provides the user to edit google docs name with the given
     * name.
     *
     * @param fileName       String
     * @param googleDocsPage EditInGoogleDocsPage
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage renameGoogleDocName(String fileName, EditInGoogleDocsPage googleDocsPage)
    {
        GoogleDocsRenamePage renameDocs = googleDocsPage.renameDocumentTitle().render();
        return renameDocs.updateDocumentName(fileName).render();
    }

    /**
     * Saving the google doc with the minor version and if isCreate boolean
     * value is true for saving the new google doc otherwise existing google
     * doc.
     *
     * @param drone       WebDrone
     * @param isCreateDoc boolean
     * @return SharPage
     */
    public static SharePage saveGoogleDoc(WebDrone drone, boolean isCreateDoc)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        googleDocsPage.setGoogleCreate(isCreateDoc);
        if (isGoogleDocsV3)
        {
            String title = googleDocsPage.getDocumentTitle();
            closeAndSwitchToShare(drone);
            SharePage currPage = ShareUser.getSharePage(drone).render();
            if (currPage instanceof DocumentDetailsPage)
            {
                return ((DocumentDetailsPage) currPage).clickCheckInGoogleDoc().submit().render();
            }
            else if (currPage instanceof DocumentLibraryPage)
            {
                return ((DocumentLibraryPage) currPage).getFileDirectoryInfo(title).selectCheckInGoogleDoc().submit().render();
            }
        }
        else
        {
            GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
            googleUpdatefile.render();
            googleUpdatefile.selectMinorVersionChange();
            return googleUpdatefile.submit().render();
        }
        throw new PageOperationException("Unable to save Google Doc");
    }

    /**
     * Saving the google doc with the minor version and if isCreate boolean
     * value is true for saving the new google doc otherwise existing google
     * doc. Methods used for edition by concurrent user's
     *
     * @param drone       WebDrone
     * @param isCreateDoc boolean
     * @return SharePage
     */
    public static SharePage saveGoogleDocOtherEditor(WebDrone drone, boolean isCreateDoc)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        googleDocsPage.setGoogleCreate(isCreateDoc);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        return googleUpdatefile.submitWithConcurrentEditors().render();
    }

    /**
     * Discarding the changes made in google doc.
     *
     * @param drone WebDrone
     * @return SharePage
     */
    public static HtmlPage discardGoogleDocsChanges(WebDrone drone)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        if (isGoogleDocsV3)
        {
            String docTitle = googleDocsPage.getDocumentTitle();
            closeAndSwitchToShare(drone);
            HtmlPage currentPage = FactorySharePage.resolvePage(drone);
            if (currentPage instanceof DocumentLibraryPage)
            {
                ((DocumentLibraryPage) currentPage).getFileDirectoryInfo(docTitle).selectCancelEditingInGoogleDocs().render();
            }
            else if (currentPage instanceof DocumentDetailsPage)
            {
                ((DocumentDetailsPage) currentPage).clickCancelEditingInGoogleDocs().render();
            }
            HtmlPage thePage = FactorySharePage.resolvePage(drone).render();
            if (thePage instanceof SharePopup)
            {
                ((SharePopup) thePage).clickYes();
                drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxWaitTime, MILLISECONDS));
            }
        }
        else
        {
            GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
            return googleDocsDiscardChanges.clickOkButton().render();
        }
        return FactorySharePage.resolvePage(drone).render();
    }

    /**
     * Discarding the changes made in google doc. Methods used for edition by concurrent user's
     *
     * @param drone WebDrone
     * @return HtmlPage
     */
    public static HtmlPage discardGoogleDocsChangesOtherEditor(WebDrone drone)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
        return googleDocsDiscardChanges.clickOkConcurrentEditorButton().render();
    }

    /**
     * This method provides the sign in page to log into google docs.
     *
     * @param googleAuth GoogleDocsAuthorisation
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signInGoogleDocs(WebDrone driver, GoogleDocsAuthorisation googleAuth) throws InterruptedException
    {
        int i = 0;
        GoogleSignUpPage signUpPage = googleAuth.submitAuth().render();
        signUpPage.signUp(googleUserName, googlePassword);
        if (isGoogleDocsV3)
        {
            switchToGoogleWindow(driver);
        }
        while (!(driver.getCurrentPage() instanceof EditInGoogleDocsPage))
        {
            webDriverWait(driver, 10000);
            i++;
            if (i == retrySearchCount)
            {
                break;
            }
        }
        return FactorySharePage.resolvePage(driver).render();
    }

    /**
     * Saves the google doc with the given comments as minor or major version.
     *
     * @param drone          WebDrone
     * @param comments       String
     * @param isMinorVersion boolean
     * @return GoogleDocsUpdateFilePage
     */
    public static GoogleDocsUpdateFilePage saveGoogleDocWithVersionAndComment(WebDrone drone, String comments, boolean isMinorVersion)
    {
        EditInGoogleDocsPage googleDocsPage = drone.getCurrentPage().render();
        GoogleDocsUpdateFilePage googleUpdateFile = googleDocsPage.selectSaveToAlfresco().render();

        if (isMinorVersion)
        {
            googleUpdateFile.selectMinorVersionChange();
        }
        else
        {
            googleUpdateFile.selectMajorVersionChange();
        }

        if (!StringUtils.isEmpty(comments))
        {
            googleUpdateFile.setComment(comments);
        }

        return googleUpdateFile;
    }

    /**
     * This method is used to delete the given user profile.
     *
     * @param testUser String
     * @param drone    WebDrone
     * @return {@link UserSearchPage}
     */
    protected UserSearchPage deleteUser(WebDrone drone, String testUser)
    {
        if (isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Delete user is available in cloud");
        }
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        page = page.searchFor(testUser).render();
        UserProfilePage userProfile = page.clickOnUser(testUser).render();
        return userProfile.deleteUser().render();
    }

    /**
     * Discarding the changes made in google doc.
     *
     * @param filename String
     * @return SharePage
     */
    public static GoogleSignUpPage openSignUpPage(WebDrone driver, String filename)
    {
        DocumentLibraryPage docLibPage = driver.getCurrentPage().render();
        GoogleDocsAuthorisation googleAuth = docLibPage.getFileDirectoryInfo(filename).selectEditInGoogleDocs().render();
        Assert.assertTrue(googleAuth.isAuthorisationDisplayed());
        return googleAuth.submitAuth().render();
    }

    /**
     * Method to close Google Drive window and switch to Alfresco
     *
     * @param drone WebDrone
     * @return SharePage
     */
    private static SharePage closeAndSwitchToShare(WebDrone drone)
    {
        Set<String> setWindowHandles = drone.getWindowHandles();
        drone.closeWindow();
        drone.switchToWindow(setWindowHandles.toArray(new String[setWindowHandles.size()])[0]);
        return FactorySharePage.resolvePage(drone).render();
    }

    /**
     * Method to switch to Google Drive window
     *
     * @param drone WebDrone
     * @return EditGoogleDocsPage
     */
    private static EditInGoogleDocsPage switchToGoogleWindow(WebDrone drone)
    {
        logger.info("Switch to GoogleDocs Edit window");
        Set<String> setWindowHandles = drone.getWindowHandles();
        int retry = 0;
        while (setWindowHandles.size() < 2)
        {
            webDriverWait(drone, 3000);
            retry++;
            setWindowHandles = drone.getWindowHandles();
            if (retry == 3)
            {
                throw new PageOperationException("Google doc isn't opened in new window for Editing");
            }
        }
        String[] windowHandles = setWindowHandles.toArray(new String[setWindowHandles.size()]);
        drone.switchToWindow(windowHandles[1]);
        return FactorySharePage.resolvePage(drone).render();
    }
}