package org.alfresco.share.sharepoint.office2011;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.alfresco.mac.application.MicrosoftExcel2011;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.DocumentLibraryUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author p3700454
 */
@Listeners(FailedTestListener.class)
public class MSExcel2011Tests extends AbstractUtils
{

    private String testName;
    private String testUser;
    private String testSiteName;

    private File xls9760TestFile;
    private File xls9761TestFile;
    private File xls9761DownloadTestFile;
    private File xls9763TestFile;
    private ArrayList<File> testFiles = new ArrayList<File>();

    private DocumentLibraryPage documentLibraryPage;
    private MicrosoftExcel2011 appExcel2011;

    private static final String SHAREPOINT = "sharepoint";

    private static final Logger logger = Logger.getLogger(MSExcel2011Tests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName() + "10";
        testUser = getUserNameFreeDomain(testName);
        testSiteName = getSiteName(testName);

        appExcel2011 = new MicrosoftExcel2011();

        // used from testdata folder
        xls9760TestFile = getTestDataFile(SHAREPOINT, "AONE-9760.xlsx");
        xls9761TestFile = getTestDataFile(SHAREPOINT, "AONE-9761.xlsx");
        xls9763TestFile = getTestDataFile(SHAREPOINT, "AONE-9763.xlsx");
        xls9761DownloadTestFile = new File("tmpxls9761TestFile.xlsx");

        // this files will be uploaded on dataprep
        testFiles.add(xls9761TestFile);
        testFiles.add(xls9763TestFile);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();

        xls9761DownloadTestFile.delete();

        try
        {
            appExcel2011.exitApplication();
        }
        catch (IOException e)
        {
            logger.error("Error closing Office App: " + e.getMessage());
        }
    }

    @Test(groups = { "DataPrepExcelMac" })
    public void dataPrep_AONE() throws Exception
    {
        // Create normal User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create public site
        try
        {
            ShareUser.createSite(drone, testSiteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        }
        catch (SkipException e)
        {
            logger.error("Data Prep on Create Site: " + e.getMessage());
        }

        ShareUser.openSiteDocumentLibraryFromSearch(drone, testSiteName);

        // upload all test files
        for (Iterator<File> iterator = testFiles.iterator(); iterator.hasNext();)
        {
            File file = iterator.next();
            logger.info("Setup Data Prep using: " + file.getName());
            ShareUserSitePage.uploadFile(drone, file).render();
        }
    }

    protected void openDocumentLibraryForTest()
    {
        // open site document libary
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, testSiteName);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Upload the document - Add file action")
    public void AONE_9760() throws Exception
    {

        // opening document library
        openDocumentLibraryForTest();

        // S1 -Click on Add File button.
        // S2 -Choose the created in the pre-condition document and click on Upload button.
        // Expect
        // Upload New Files window is opened.
        // The document is uploaded
        ShareUserSitePage.uploadFile(drone, xls9760TestFile).render();

        // --- Step 3 ---
        // Verify the document library of the site in the Share.
        // --- Expected results ---
        // The document was uploaded correctly
        Assert.assertTrue(documentLibraryPage.isFileVisible(xls9760TestFile.getName()), "The document was uploaded corectly.");
        // No data was lost (check the size of the document uploaded)
        Assert.assertEquals(DocumentLibraryUtil.getDocumentProperties(documentLibraryPage, xls9760TestFile.getName()).size(), 10);

    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document")
    public void AONE_9761() throws Exception
    {
        // opening document library
        openDocumentLibraryForTest();

        // Choose the document and choose Save As action from the context menu.
        // Choose any location, e.g. Desktop, specify any name and click on Save button.
        // Verify the chosen location, e.g. Desktop.
        // Expect - The document was downloaded correctly. No data was lost. The document was downloaded with the specified name.
        DocumentLibraryUtil.downloadFile(documentLibraryPage, xls9761TestFile.getName(), xls9761DownloadTestFile);

        Assert.assertEquals(xls9761DownloadTestFile.length(), 8679, "Data was lost from the file downloaded");
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection§ to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document")
    public void AONE_9763() throws Exception
    {
        // open document in Excel
        appExcel2011.openApplication();
        appExcel2011.focus();
        appExcel2011.openURL(getVTIDocumentLibraryFilePath(testSiteName, xls9763TestFile.getName()));
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);

        // now searching on Document Library that this file is locked
        openDocumentLibraryForTest();

        Boolean isLocked = DocumentLibraryUtil.fileIsLocked(documentLibraryPage, xls9763TestFile.getName());
        Assert.assertTrue(isLocked, "File is locked after is was opened localy from Excel");

    }
}
