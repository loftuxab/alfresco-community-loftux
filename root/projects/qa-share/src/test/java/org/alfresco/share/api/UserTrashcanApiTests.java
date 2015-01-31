package org.alfresco.share.api;

import org.alfresco.json.JSONUtil;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.AlfrescoHttpClient;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Class to include: Tests for trashcan apis.
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test
public class UserTrashcanApiTests extends AlfrescoHttpClient
{
    public UserTrashcanApiTests() throws Exception
    {
        super();
    }

    private static Log logger = LogFactory.getLog(UserTrashcanApiTests.class);
    private String testName;
    private String testUser;
    private String siteName;
    private String fileName;
    private String nodeRef;
    private String reqURL;
    private String suffix;
    private DocumentLibraryPage doclibPage;
    String[] headers;
    String[] authDetails;
    HttpClient client;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        testName = this.getClass().getSimpleName();
        testUser = getUserNamePremiumDomain(testName);
        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();

        if (isAlfrescoVersionCloud(drone))
        {
            suffix = "alfresco/a" + DOMAIN_PREMIUM;
        }
        else
            suffix = "alfresco/service";
        reqURL = getAPIURL(drone).replace("alfresco/api/", suffix) + "/api/archive/workspace/SpacesStore";
        headers = getRequestHeaders("application/json;charset=utf-8");
        authDetails = getAuthDetails(testUser);
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        //Several documents are uploaded by the user, e.g. 5 and deleted
        //cleaning up trashcan
        HttpDelete httpDelete = generateDeleteRequest(reqURL, headers, new String[] { "" });
        executeRequestHttpResp(client, httpDelete);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true).render();
        //creating 5 documents and deleting'em
        for (int i = 0; i < 5; i++)
        {
            doclibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName + i });
            doclibPage.getFileDirectoryInfo(fileName + i).delete();
        }
    }

    @Test
    public void AONE_14287() throws Exception
    {
        String max_items = "?maxItems=%s";
        String skip_count = "?skipCount=%s";
        String nameFilter = "?nf=%s";

        HttpGet httpGet = generateGetRequest(reqURL, headers);
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

        String result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertEquals(getNumOfDeletedNodes(result), 5, "Incorrect number of nodes returned");
        for (int i = 0; i < 5; i++)
        {
            assertTrue(isParamWithValExists(result, "name", fileName + i), "Object isn't present in the response");
        }

        httpGet = generateGetRequest(reqURL + String.format(max_items, 2), headers);
        response = executeRequestHttpResp(client, httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

        result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertEquals(getParameterValue("paging", "maxItems", result), "2");
        assertEquals(getParameterValue("paging", "totalItems", result), "2");
        assertEquals(getNumOfDeletedNodes(result), 2, "Incorrect number of nodes returned");

        httpGet = generateGetRequest(reqURL + String.format(skip_count, 2), headers);
        response = executeRequestHttpResp(client, httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

        result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertEquals(getParameterValue("paging", "skipCount", result), "2");
        assertEquals(getParameterValue("paging", "maxItems", result), "50");
        assertEquals(getParameterValue("paging", "totalItems", result), "3");
        assertEquals(getNumOfDeletedNodes(result), 3, "Incorrect number of nodes returned");

        httpGet = generateGetRequest(reqURL + String.format(nameFilter, fileName + "1"), headers);
        response = executeRequestHttpResp(client, httpGet);
        assertEquals(response.getStatusLine().getStatusCode(), 200);

        result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertTrue(isParamWithValExists(result, "name", fileName + "1"), "Object isn't present in the response");
        assertFalse(isParamWithValExists(result, "name", fileName + "2"), "Object isn't present in the response");
        assertEquals(getNumOfDeletedNodes(result), 1, "Incorrect number of nodes returned");
    }

    @Test
    public void AONE_14288() throws Exception
    {
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        nodeRef = getDeletedNodeRef(fileName + "1");
        HttpPut httpPut = generatePutRequest(reqURL.replace("workspace", "archive") + "/" + nodeRef, headers, new String[] { "" });
        HttpResponse response = executeRequestHttpResp(client, httpPut);
        assertEquals(response.getStatusLine().getStatusCode(), 200);
        String result = JSONUtil.readStream(response.getEntity()).toJSONString();
        JSONObject json = new JSONObject(result);
        assertTrue(json.getJSONObject("data").getJSONObject("restoredNode").get("restoredNodeRef").toString().equals("workspace://SpacesStore/" + nodeRef));
    }

    @Test
    public void AONE_14289() throws Exception
    {
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        nodeRef = getDeletedNodeRef(fileName + "2");
        HttpDelete httpDelete = generateDeleteRequest(reqURL.replace("workspace", "archive") + "/" + nodeRef, headers, new String[] { "" });
        HttpResponse response = executeRequestHttpResp(client, httpDelete);
        assertEquals(response.getStatusLine().getStatusCode(), 200);
        httpDelete = generateDeleteRequest(reqURL, headers, new String[] { "" });
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        response = executeRequestHttpResp(client, httpDelete);
        assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    private int getNumOfDeletedNodes(String response) throws Exception
    {
        JSONObject json = new JSONObject(response);
        return json.getJSONObject("data").getJSONArray("deletedNodes").length();
    }

    private boolean isParamWithValExists(String response, String param, String value) throws Exception
    {
        JSONObject json = new JSONObject(response);
        JSONArray jsonArray = json.getJSONObject("data").getJSONArray("deletedNodes");
        for (int i = 0; i < jsonArray.length(); i++)
        {
            if (jsonArray.getJSONObject(i).get(param).equals(value))
                return true;
        }
        return false;
    }

    private String getDeletedNodeRef(String fileName) throws Exception
    {
        client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpGet httpGet = generateGetRequest(reqURL + "?nf=" + fileName, headers);
        HttpResponse response = executeRequestHttpResp(client, httpGet);
        String result = JSONUtil.readStream(response.getEntity()).toJSONString();
        JSONObject json = new JSONObject(result);
            return json.getJSONObject("data").getJSONArray("deletedNodes").getJSONObject(0).get("nodeRef").toString().split("SpacesStore/(.*?)")[1];
    }
}
