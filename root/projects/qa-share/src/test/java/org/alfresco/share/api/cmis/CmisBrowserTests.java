/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.api.cmis;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.ContentData;
import org.alfresco.rest.api.tests.client.data.FolderNode;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.tck.impl.JUnitHelper;
import org.apache.chemistry.opencmis.tck.impl.TestParameters;
import org.apache.chemistry.opencmis.tck.tests.basics.BasicsTestGroup;
import org.apache.chemistry.opencmis.tck.tests.control.ControlTestGroup;
import org.apache.chemistry.opencmis.tck.tests.crud.CRUDTestGroup;
import org.apache.chemistry.opencmis.tck.tests.filing.FilingTestGroup;
import org.apache.chemistry.opencmis.tck.tests.query.QueryTestGroup;
import org.apache.chemistry.opencmis.tck.tests.types.TypesTestGroup;
import org.apache.chemistry.opencmis.tck.tests.versioning.VersioningTestGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Class to include: Tests for cmis tests with various bindings as poc
 *
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
public class CmisBrowserTests extends CmisUtils
{
    private String testName;
    private String testUser;
    private String siteName;
    private String fileName;
    private String folderName;
    private String testUser2;
    private static Log logger = LogFactory.getLog(CmisBrowserTests.class);
    private Map<String, String> cmisParameters;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        testUser = getUserNameFreeDomain(testName);
        testUser2 = getUserNameFreeDomain(testName + "_1");
        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();
        folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        ShareUser.login(drone, testUser2);
        ShareUser.createSite(drone, getSiteShortname(siteName), SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone);
        cmisParameters = new HashMap<>();
    }

    @Test
    public void AONE_14227() throws Exception
    {
        String fileName = "important document";
        String docContent = "MS4gR2l0YSAKIDIuIEthcm1heW9nYSBieSBWaXZla2FuYW5k";
        String folderName = "folder-" + System.currentTimeMillis();

        // Create Folder
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);
        Folder f = createFolder(CMISBinding.ATOMPUB10, testUser2, testUser2, DOMAIN, siteName, properties);
        String folderId = f.getId();

        // Create Document
        Map<String, Serializable> docProperties = new HashMap<>();
        docProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        docProperties.put(PropertyIds.NAME, fileName);
        docProperties.put(PropertyIds.DESCRIPTION, "important document");
        ContentStream cs = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);

        Document d1 = createDocumentInFolder(CMISBinding.ATOMPUB10, testUser2, fileName, DOMAIN, folderId, docProperties, VersioningState.MAJOR, cs);
        assertTrue(d1.getName().equals(fileName) && d1.getParents().get(0).getId().equals(folderId) && d1.getVersionLabel().equals("1.0")
            && d1.getContentStreamMimeType().equals("text/plain"), "The document wasn't created");
    }

    @Test
    public void AONE_14224() throws Exception
    {
        String testName = getTestName();
        String thisFolderName = getFolderName(testName);
        String thisFileName = getFileName(testName);

        ShareUser.login(drone, testUser2);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);
        String thisFolderNameGuid = ShareUser.getGuid(drone, thisFolderName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
        ContentData document = cmisSession.getContent(thisFileNameGuid);
        assertTrue(document.getBytes().length > 0, "Document wasn't retrieved");
        String invalidId = thisFileNameGuid + "inv";
        try
        {
            cmisSession.getContent(invalidId);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisObjectNotFoundException, "Document should be null as the id does not exist:" + e);
        }
        try
        {
            cmisSession.getContent(thisFolderNameGuid);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof IllegalArgumentException, "Object is not a document!:" + e);
        }
    }

    @Test
    public void AONE_14225() throws Exception
    {
        String testName = getTestName();
        String thisFolderName = getFolderName(testName);
        String thisFileName = getFileName(testName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName, thisFolderName });
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);
        ShareUser.openDocumentLibrary(drone);
        String thisFolderNameGuid = ShareUser.getGuid(drone, thisFolderName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
        FolderNode children = cmisSession.getChildren(thisFolderNameGuid, 0, 100);
        assertTrue(children.getDocumentNodes().toString().contains(thisFileNameGuid), "The children are not retrieved");
        String invalidId = thisFolderName + "inv";
        try
        {
            cmisSession.getChildren(invalidId, 0, 100);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisObjectNotFoundException, "Document should be null as the id does not exist:" + e);
        }
        try
        {
            cmisSession.getChildren(thisFileNameGuid, 0, 100);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof IllegalArgumentException, "Object is not a document!:" + e);
        }
    }

    @Test
    public void AONE_14226() throws Exception
    {
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse httpResponse = publicApiClient.get(DOMAIN + "/public/cmis/versions/1.0/atom", null);
        assertEquals(httpResponse.getStatusCode(), 200, "The operation wasn't successful");
        assertTrue(httpResponse.getResponse().contains("cmis:repositoryId") && httpResponse.getResponse().contains(DOMAIN), "The operation wasn't successful");
    }

    @Test
    public void AONE_14228() throws Exception
    {
        String testName = getTestName();
        String thisFileName = getFileName(testName);

        ShareUser.login(drone, testUser2);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName });
        String thisFileNameGuid = ShareUser.getGuid(drone, thisFileName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(CMISBinding.ATOMPUB10, testUser2, DOMAIN);
        List<Document> allVersions = cmisSession.getAllVersions(thisFileNameGuid);
        assertEquals(allVersions.size(), 1, "Incorrect version size");
        String docContent = "Lorem Ipsum";
        ContentStream fileContent = streamContent(docContent, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        Document doc1 = (Document) cmisSession.getObject(thisFileNameGuid);
        String versionLabel = doc1.getVersionLabel();

        // Update file with new one.
        doc1.setContentStream(fileContent, true);
        allVersions = cmisSession.getAllVersions(thisFileNameGuid);
        assertEquals(allVersions.size(), 2, "Incorrect version size");
        Document doc2 = doc1.getObjectOfLatestVersion(false);
        String versionLabel2 = doc2.getVersionLabel();
        assertTrue(Double.parseDouble(versionLabel2) > Double.parseDouble(versionLabel), "Incorrect version size");
        assertEquals(doc2.getVersionLabel(), "1.1", "Incorrect version label");
    }
}