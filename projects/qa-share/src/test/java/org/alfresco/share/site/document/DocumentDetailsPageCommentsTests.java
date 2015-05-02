package org.alfresco.share.site.document;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BOLD;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BULLET;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.ITALIC;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.NUMBER;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.UNDERLINED;

import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.document.AddCommentForm;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class DocumentDetailsPageCommentsTests extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";
    String fileName;
    String fontStyle = "style=\"color: rgb(0, 0, 255);\"";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_12352() throws Exception
    {

        String testName = getTestName() + "R1";
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        // Create User
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_12352()
    {
        DocumentLibraryPage documentLibraryPage;
        String testName = getTestName() + "R1";
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);
        String textBold = "Bold Text";
        String textItalic = "Italic Text";
        String textUnderline = "Underline Text";
        String textBullet = "Bullet Text";
        String textNumber = "Number Text";
        String colorText = "Color Text";
        String allFormats = "All in one";
        String tinyMceStr;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // open site document library page
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();

        // click add comment
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

        // ---- Step 1 ----
        // ---- Step action ---
        // Type any text and make it bold;
        // ---- Expected results ----
        // The text is marked as bold;
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(textBold);
        tinyMceEditor.clickTextFormatter(BOLD);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("<strong>") && tinyMceStr.contains(textBold));
        addCommentForm.clickAddCommentButton().render();
        String boldHTML = detailsPage.getCommentHTML(textBold);
        Assert.assertTrue(boldHTML.contains("<strong>") && boldHTML.contains(textBold));

        // ---- Step 2 ----
        // ---- Step action ---
        // Type any text and make it italic
        // ---- Expected results ----
        // The text is italic;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textItalic);
        tinyMceEditor.clickTextFormatter(ITALIC);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("<em>") && tinyMceStr.contains(textItalic));
        addCommentForm.clickAddCommentButton().render();
        String italicHtml = detailsPage.getCommentHTML(textItalic);
        Assert.assertTrue(italicHtml.contains("<em>") && italicHtml.contains(textItalic));

        // ---- Step 3 ----
        // ---- Step action ---
        // Type any text and make it underlined;
        // ---- Expected results ----
        // The text is marked as underlined;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textUnderline);
        tinyMceEditor.clickTextFormatter(UNDERLINED);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("text-decoration: underline;") && tinyMceStr.contains(textUnderline));
        addCommentForm.clickAddCommentButton().render();
        String underlineHtml = detailsPage.getCommentHTML(textUnderline);
        Assert.assertTrue(underlineHtml.contains("text-decoration: underline;") && underlineHtml.contains(textUnderline));

        // ---- Step 4 ----
        // ---- Step action ---
        // Type any text and make list
        // ---- Expected results ----
        // List is displayed;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textBullet);
        tinyMceEditor.clickTextFormatter(BULLET);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("ul") && tinyMceStr.contains("<li>") && tinyMceStr.contains(textBullet));
        addCommentForm.clickAddCommentButton().render();
        String bulletStr = detailsPage.getCommentHTML(textBullet);
        Assert.assertTrue(bulletStr.contains("ul") && bulletStr.contains(textBullet) && bulletStr.contains("<li>"));

        // ---- Step 5 ----
        // ---- Step action ---
        // Type any text and make numbered list;
        // ---- Expected results ----
        // Numbered list is displayed;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textNumber);
        tinyMceEditor.clickTextFormatter(NUMBER);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("ol") && tinyMceStr.contains("<li>") && tinyMceStr.contains(textNumber));
        addCommentForm.clickAddCommentButton().render();
        String numberStr = detailsPage.getCommentHTML(textNumber);
        Assert.assertTrue(numberStr.contains("ol") && numberStr.contains(textNumber) && numberStr.contains("<li>"));

        // ---- Step 6 ----
        // ---- Step action ---
        // Type any text and highlight it with any color;
        // ---- Expected results ----
        // The text is highlighted with any color;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(colorText);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        tinyMceStr = tinyMceEditor.getContent();
        Assert.assertTrue(tinyMceStr.contains("color: rgb(0, 0, 255)") && tinyMceStr.contains(colorText));
        addCommentForm.clickAddCommentButton().render();
        String colorHtml = detailsPage.getCommentHTML(colorText);
        Assert.assertTrue(colorHtml.contains("color: #0000ff") && colorHtml.contains(colorText));

        // ---- Step 7 ----
        // ---- Step action ---
        // Click Create button;
        // ---- Expected results ----
        /**
         * Comment is successfully created; The formatting is successfully displayed:
         * - the text is marked as bold;
         * - the text is italic;
         * - the text is marked as underlined;
         * - the list is displayed;
         * - the numbered lIst is displayed;
         * - the text is highlighted with any color;
         */
        addCommentForm = detailsPage.clickAddCommentButton();
        addCommentForm.clickAddCommentButton();
        tinyMceEditor.setText(allFormats);
        tinyMceEditor.clickTextFormatter(BOLD);
        tinyMceEditor.clickTextFormatter(ITALIC);
        tinyMceEditor.clickTextFormatter(UNDERLINED);
        tinyMceEditor.clickTextFormatter(NUMBER);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        addCommentForm.clickAddCommentButton().render();
        String allFormatsHtml = detailsPage.getCommentHTML(allFormats);
        Assert.assertTrue(allFormatsHtml.contains("text-decoration: underline") && allFormatsHtml.contains(" color: #0000ff")
                && allFormatsHtml.contains("<em>") && allFormatsHtml.contains("<strong>") && allFormatsHtml.contains("<ol>"));
    }
}
