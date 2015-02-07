/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.repository.subsystems;

import org.alfresco.json.JSONUtil;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.MailUtil;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.TelnetUtil;
import org.alfresco.share.util.api.AlfrescoHttpClient;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Olga Lokhach
 */

@Listeners(FailedTestListener.class)

public class InbordEmailSubsystemTests extends AlfrescoHttpClient
{
    public InbordEmailSubsystemTests() throws Exception
    {
        super();
    }

    private static Log logger = LogFactory.getLog(InbordEmailSubsystemTests.class);
    private static String testUser;
    private static String siteName;
    private static String folderName;
    private String group = "EMAIL_CONTRIBUTORS";
    private String emailPort = "2125";
    private String nodeAlias = "inbox";
    private String emailInboundObject = "Alfresco:Type=Configuration,Category=email,id1=inbound";
    private String emailServerEnabled = "email.server.enabled";
    private String emailServerPort = "email.server.port";
    private String emailServerAuth = "email.server.auth.enabled";
    private String emailInbound = "email.inbound.enabled";
    private String server;
    private String reqURL;
    private String folderGuid;
    private String nodeDBID;
    private String query;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        testUser = testName + "@alfresco.com";
        siteName = getSiteName(testName);
        folderName = getFolderName(testName);
        server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");

        try
        {
            // Configure email server
            configEmailServer();

            // Creating enterprise user with group
            assertTrue (ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testUser, testUser, DEFAULT_LASTNAME, DEFAULT_PASSWORD, group),
                "Can't create user with group");

            // Admin logs in
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // Create a public site
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            // Create a folder
            ShareUser.openDocumentLibrary(drone);
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER).render();
            folderGuid = ShareUser.getGuid(drone, folderName);

            // Get node-dbid property of the folder
            reqURL = PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) +
                    "/alfresco/s/api/metadata?nodeRef=workspace://SpacesStore/" + folderGuid;
            query = "&shortQNames=true";
            nodeDBID = getNodeDBID();

            // Add Aliasable (Email) aspect
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName);
            SelectAspectsPage selectAspectsPage = fileInfo.selectManageAspects().render();
            selectAspectsPage.add(Arrays.asList(DocumentAspect.ALIASABLE_EMAIL)).render();
            selectAspectsPage.clickApplyChanges().render();
            EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
            editDocumentPropertiesPage.selectAllProperties();
            editDocumentPropertiesPage.setEmailAlias(nodeAlias);
            editDocumentPropertiesPage.selectSave();

            //Invite user to the site with Collaborator role
            webDriverWait(drone, 10000); //solr wait
            ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser, siteName, UserRole.COLLABORATOR);
        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as pre-condition step(s) fail: " + e);
        }
    }

    /**
     * AONE-7272: Verify possibility to disable email server
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7272() throws Exception
    {
        boolean emailEnabledDefault = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(shareUrl, emailInboundObject, emailServerEnabled).toString());

        try
        {
            // Email server is enabled
            assertTrue(emailEnabledDefault, "Email server isn't enabled");

            //  Set property "email.server.enabled=false"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailServerEnabled, false);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify that email server is disable
            assertFalse(TelnetUtil.connectServer(shareUrl, emailPort), "Email server is running");

        }
        finally
        {
            // Enabled email server
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailServerEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }

    }

    /**
     * AONE-7275:Verify possibility to block a sender
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7275() throws Exception
    {
        String emailBlockedSenders = "email.server.blocked.senders";
        String sender = testUser.split("[@]+")[0] + "\\@alfresco\\.com";
        String userEmail = testUser;
        List<String> listRecipients = new ArrayList<>();
        listRecipients.add(nodeAlias + "@" + server);
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String blockedSendersDefaultValue = "";

        try
        {
            // Set property "email.server.blocked.senders=.*"  to "testUser\@alfresco\.com"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailBlockedSenders, sender);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify impossibility to receive email from sender with email address testUser@alfresco.com
            assertFalse(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
                "The Alfresco server is configured to accept inbound emails. Mail was send.");

            // Verify the folder isn't contains the sent message
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
            assertFalse(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is presented");
        }
        finally
        {
            // Set default property "email.server.blocked.senders="
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailBlockedSenders, blockedSendersDefaultValue);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }

    }

    /**
     * AONE-7276:Verify possibility to block a group of senders
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7276() throws Exception
    {
        String emailBlockedSenders = "email.server.blocked.senders";
        String senders = ".*\\@alfresco\\.com";
        String userEmail = testUser;
        List<String> listRecipients = new ArrayList<>();
        listRecipients.add(nodeAlias + "@" + server);
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String blockedSendersDefaultValue = "";

        try
        {
            // Set property "email.server.blocked.senders=.*"  to "testUser\@alfresco\.com"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailBlockedSenders, senders);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify impossibility to receive email  from senders with email addresses @alfresco.com
            assertFalse(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
                "The Alfresco server is configured to accept inbound emails. Mail was send.");

            // Verify the folder isn't contains the sent message
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
            assertFalse(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is presented");
        }
        finally
        {
            // Set default property "email.server.blocked.senders="
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailBlockedSenders, blockedSendersDefaultValue);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }

    }

    /**
     * AONE-7277:Verify possibility to allow a sender to send an e-mail
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7277() throws Exception
    {
        String emailAllowedSenders = "email.server.allowed.senders";
        String sender = testUser.split("[@]+")[0] + "\\@alfresco\\.com";
        String userEmail = testUser;
        List<String> listRecipients = new ArrayList<>();
        listRecipients.add(nodeAlias + "@" + server);
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String allowedSendersDefaultValue = ".*";

        try
        {
            //  Set property "email.server.allowed.senders=testUser\@alfresco\.com"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailAllowedSenders, sender);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify possibility to receive email from sender with email address "testUser\@alfresco\.com";
            assertTrue(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
                "The Alfresco server is not configured to accept inbound emails.");

            // Verify the folder contains the sent message
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
            assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");
        }
        finally
        {
            // Set default property "email.server.blocked.senders=.*"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailAllowedSenders, allowedSendersDefaultValue);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }
    }

    /**
     * AONE-7278:Verify possibility to allow a group of senders to send e-mails
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7278() throws Exception
    {
        String emailAllowedSenders = "email.server.allowed.senders";
        String senders = ".*\\@alfresco\\.com";
        String userEmail = testUser;
        List<String> listRecipients = new ArrayList<>();
        listRecipients.add(nodeAlias + "@" + server);
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String allowedSendersDefaultValue = ".*";

        try
        {
            //  Set property "email.server.allowed.senders=.*\@alfresco\.com"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailAllowedSenders, senders);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify possibility to receive email from senders with email addresses ".*\@alfresco\.com";
            assertTrue(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
                "The Alfresco server is not configured to accept inbound emails.");

            // Verify the folder contains the sent message
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
            assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");
        }

        finally
        {
            // Set default property "email.server.blocked.senders=.*"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailAllowedSenders, allowedSendersDefaultValue);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }

    }

    /**
     * AONE-7280:Verify possibility to change email server domain from "alfresco.com" (default) to another one
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7280() throws Exception
    {
        String emailServerDomain = "email.server.domain";
        String newEmailServerName = "test.com";

        //  Set property "email.server.domain=test.com"
        JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailServerDomain, newEmailServerName);

        // click operation "Start"
        JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
        JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

        // Verify that email server name is changed
        assertTrue(TelnetUtil.readStreamString(shareUrl, emailPort).contains("220 test.com ESMTP SubEthaSMTP"));

    }

    /**
     * AONE-7279: Verify possibility to change email server domain from actual to "alfresco.com" (default)
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7279() throws Exception
    {
        String emailServerDomain = "email.server.domain";
        String emailServerName = "alfresco.com";

        //  Set property "email.server.domain=alfresco.com"
        JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailServerDomain, emailServerName);

        // click operation "Start"
        JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
        JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

        // Verify that server name is changed
        assertTrue(TelnetUtil.readStreamString(shareUrl, emailPort).contains("220 alfresco.com ESMTP SubEthaSMTP"));

    }

    /**
     * AONE-7281:Specify Unknown User
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7281() throws Exception
    {
        String emailUnknownUser = "email.inbound.unknownUser";
        List<String> listRecipients = new ArrayList<>();
        listRecipients.add(nodeAlias + "@" + server);
        String unknownUser = testUser;
        String userEmail = getRandomString(5) + "@" + DOMAIN_FREE;
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String emailUnknownUserDefaultValue = "anonymous";

        try
        {
            //  Set property "email.inbound.unknownUser=testUser"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailUnknownUser, unknownUser);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");

            // Verify possibility to receive email from non alfresco users email
            assertTrue(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
                "The Alfresco server is not configured to accept inbound emails.");

            // Verify the folder contains the sent message
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
            assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");

            // Verify message  - The creator and modifier is testUser
            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(subject).render();
            Map<String, Object> properties = documentDetailsPage.getProperties();
            assertEquals(properties.get("Creator"), testUser, "Creator isn't set to " + testUser);
            assertEquals(properties.get("Modifier"), testUser, "Modifier isn't set to " + testUser);
        }

        finally
        {
            // Set default property "email.server.blocked.senders=.*"
            JmxUtils.setAlfrescoServerProperty(shareUrl, emailInboundObject, emailUnknownUser, emailUnknownUserDefaultValue);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, emailInboundObject, "start");
        }

    }

    /**
     * AONE-7288:Send email to multiple recipient folders
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7288() throws Exception
    {
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String userEmail = testUser;

        // Log in as User
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open document library
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        for (int i = 1; i < 3; i++)
        {
            // Create folders
            ShareUser.createFolderInFolder(drone, folderName + i, folderName + i, DOCLIB_CONTAINER).render();

            // Add Aliasable (Email) aspect
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + i);
            SelectAspectsPage selectAspectsPage = fileInfo.selectManageAspects().render();
            selectAspectsPage.add(Arrays.asList(DocumentAspect.ALIASABLE_EMAIL)).render();
            selectAspectsPage.clickApplyChanges().render();

            // Specify alias to folders
            EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
            editDocumentPropertiesPage.selectAllProperties();
            editDocumentPropertiesPage.setEmailAlias(nodeAlias + i);
            editDocumentPropertiesPage.selectSave();
        }

        // Send email to multiple recipient folders (folderName1, folderName2)
        String recipients = nodeAlias + "1@" + server + ", " + nodeAlias + "2@" + server;
        List<String> listRecipients = Arrays.asList(recipients.split("[,]+"));
        assertTrue(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
            "The Alfresco server is not configured to accept inbound emails.");

        // Verify the presence of the sent message in the folders
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        for (int i = 1; i < 3; i++)
        {
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName + i).render();
            assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");
        }

    }

    /**
     * AONE-7282:Check for emailed content created in right space
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_7282() throws Exception
    {
        String subject = testName + getRandomString(5);
        String body = getRandomString(10);
        String userEmail = testUser;

        // Log in as User
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Add a email alias aspect to Document library with alias "root";
        FolderDetailsPage folderDetailsPage = documentLibraryPage.getNavigation().selectFolderInNavBar("Documents").render();
        SelectAspectsPage selectAspectsPage = folderDetailsPage.selectManageAspects();
        selectAspectsPage.add(Arrays.asList(DocumentAspect.ALIASABLE_EMAIL)).render();
        selectAspectsPage.clickApplyChanges().render();

        EditDocumentPropertiesPage editDocumentPropertiesPage = folderDetailsPage.selectEditProperties();
        editDocumentPropertiesPage.setEmailAlias("root");
        editDocumentPropertiesPage.selectSave();

        // Send an email to the folderName using the dbnode id and another mail to the Document library using the "root" alias;
        String recipients = nodeDBID + "@" + server + ", " + "root@" + server;
        List<String> listRecipients = Arrays.asList(recipients.split("[,]+"));
        assertTrue(MailUtil.sendMailData(subject, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
            "The Alfresco server is not configured to accept inbound emails.");

        // Verify the presence of the sent message in the folders
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");
        documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName).render();
        assertTrue(documentLibraryPage.isFileVisible(subject), "The sent message with subject '" + subject + " is absent");

        // Change the email account configuration and enter an unknown SMTP host;
        // Send an email to the folderName using the dbnode id and another mail to the Document library using the "root" alias;
        // Verify impossibility to send email from unknown SMTP host;
        assertFalse(MailUtil.sendMailData(testName, body, DEFAULT_PASSWORD, userEmail, listRecipients, RandomUtil.getRandomString(5) + ".local", emailPort),
            "The Alfresco server is not configured to accept inbound emails.");

        // Fix the configuration problem by setting a correct SMTP relay host and send emails;
        assertTrue(MailUtil.sendMailData(testName, body, DEFAULT_PASSWORD, userEmail, listRecipients, shareUrl, emailPort),
            "The Alfresco server is not configured to accept inbound emails.");

        // Verify the presence of the sent message in the folders
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isFileVisible(testName), "The sent message with subject '" + subject + " is absent");
        documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName).render();
        assertTrue(documentLibraryPage.isFileVisible(testName), "The sent message with subject '" + subject + " is absent");

    }

    private void configEmailServer()
    {
        JmxUtils.invokeAlfrescoServerProperty(server, emailInboundObject, "stop");
        JmxUtils.setAlfrescoServerProperty(server, emailInboundObject, emailInbound, true);
        JmxUtils.setAlfrescoServerProperty(server, emailInboundObject, emailServerEnabled, true);
        JmxUtils.setAlfrescoServerProperty(server, emailInboundObject, emailServerPort, emailPort);
        JmxUtils.setAlfrescoServerProperty(server, emailInboundObject, emailServerAuth, false);
        JmxUtils.invokeAlfrescoServerProperty(server, emailInboundObject, "start");
    }


    private String getNodeDBID() throws Exception
    {
        HttpClient client = null;
        HttpResponse response = null;
        JSONObject json = new JSONObject();

        try
        {
            String[] headers = getRequestHeaders("application/json;charset=utf-8");
            String[] authDetails = getAuthDetails(ADMIN_USERNAME);
            client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
            HttpGet httpGet = generateGetRequest(reqURL + query, headers);
            response = executeRequestHttpResp(client, httpGet);
            String result = JSONUtil.readStream(response.getEntity()).toJSONString();
            json = new JSONObject(result);
        }

        finally
        {
            releaseConnection(client, response.getEntity());
        }

        return json.getJSONObject("properties").get("sys:node-dbid").toString();
    }

}
