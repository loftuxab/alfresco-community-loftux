package org.alfresco.enterprise.repo.officeservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.impl.client.DefaultHttpClient;

import com.xaldon.xoservices.testclient.HttpPropfind;

public class OfficeServicesRootSystemTest extends TestCase
{
    
	private static final String USERNAME = "admin";

	private static final String PASSWORD = "admin";

	private static final String SERVER_URL = "http://localhost:8080";

	protected DefaultHttpClient unauthenticatedHttpClient = null;
    
	protected DefaultHttpClient authenticatedHttpClient = null;
    
	public void setUp() throws Exception
	{
        // create the HTTP client
		unauthenticatedHttpClient = new DefaultHttpClient();
		authenticatedHttpClient = new DefaultHttpClient();
        // set credentials
		authenticatedHttpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(USERNAME,PASSWORD));
	}
	
	public void testOptionsUnauthenticated() throws Exception
	{
	    // See
	    // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/669b1506-4ffa-4016-8597-c6df4aa29eed
	    // Request 1
        HttpOptions options = new HttpOptions(new java.net.URI(SERVER_URL));
        HttpResponse serviceResponse = unauthenticatedHttpClient.execute(options);
        serviceResponse.getEntity().getContent().close();
        System.out.println("testOptionsUnauthenticated() OPTIONS "+SERVER_URL);
        System.out.println(serviceResponse.toString());
        assertEquals(401, serviceResponse.getStatusLine().getStatusCode());
        assertTrue(serviceResponse.containsHeader("WWW-Authenticate"));
        // DIFFERENCE:
        // We do not send this header for unauthenticated requests as SharePoint does.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("MicrosoftSharePointTeamServices"));
        assertFalse(serviceResponse.containsHeader("MS-Author-Via"));
        assertFalse(serviceResponse.containsHeader("MicrosoftOfficeWebServer"));
        assertFalse(serviceResponse.containsHeader("DocumentManagementServer"));
        assertFalse(serviceResponse.containsHeader("DAV"));
        // DIFFERENCE:
        // We do not send this header for unauthenticated requests as SharePoint does.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.getFirstHeader("MicrosoftSharePointTeamServices").getValue().startsWith("14.0.0."));
	}
	
	public void testPropfindUnauthenticated() throws Exception
	{
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/669b1506-4ffa-4016-8597-c6df4aa29eed
        // Request 5
        HttpPropfind propfind = new HttpPropfind(new java.net.URI(SERVER_URL));
        HttpResponse serviceResponse = unauthenticatedHttpClient.execute(propfind);
        serviceResponse.getEntity().getContent().close();
        System.out.println("testPropfindUnauthenticated() PROPFIND "+SERVER_URL);
        System.out.println(serviceResponse.toString());
		assertEquals(401, serviceResponse.getStatusLine().getStatusCode());
        assertTrue(serviceResponse.containsHeader("WWW-Authenticate"));
        // DIFFERENCE:
        // We do not send this header for unauthenticated requests as SharePoint does.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("MicrosoftSharePointTeamServices"));
        assertFalse(serviceResponse.containsHeader("MS-Author-Via"));
        assertFalse(serviceResponse.containsHeader("MicrosoftOfficeWebServer"));
        assertFalse(serviceResponse.containsHeader("DocumentManagementServer"));
        assertFalse(serviceResponse.containsHeader("DAV"));
        // DIFFERENCE:
        // We do not send this header for unauthenticated requests as SharePoint does.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.getFirstHeader("MicrosoftSharePointTeamServices").getValue().startsWith("14.0.0."));
	}
	
	public void testOptionsAuthenticated() throws Exception
	{
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/669b1506-4ffa-4016-8597-c6df4aa29eed
        // Request 3
        HttpOptions options = new HttpOptions(new java.net.URI(SERVER_URL));
        HttpResponse serviceResponse = authenticatedHttpClient.execute(options);
        serviceResponse.getEntity().getContent().close();
		assertEquals(200,serviceResponse.getStatusLine().getStatusCode());
		assertFalse(serviceResponse.containsHeader("WWW-Authenticate"));
        assertTrue(serviceResponse.containsHeader("MicrosoftSharePointTeamServices"));
        assertTrue(serviceResponse.containsHeader("MS-Author-Via"));
        // this is not required for OPTIONS requests to the server root: assertTrue(serviceResponse.containsHeader("MicrosoftOfficeWebServer"));
        assertTrue(serviceResponse.containsHeader("DocumentManagementServer"));
        assertTrue(serviceResponse.containsHeader("DAV"));
        // DIFFERENCE:
        // We do not announce support for MS-DAVEXT
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("X-MSDAVEXT"));
        assertFalse(serviceResponse.containsHeader("X-MSFSSHTTP")); // we do *NOT* support this protocol. It is patent protected.
        // DIFFERENCE:
        // We do not announce the additional DAV namespace. This header is optional according to MS documentation.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("Public-Extension"));
        assertTrue(serviceResponse.getFirstHeader("MicrosoftSharePointTeamServices").getValue().startsWith("14.0.0."));
        assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Properties Schema;"));
        assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Source Control;"));
        assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Version History;"));
        assertTrue(serviceResponse.getFirstHeader("DAV").getValue().equals("1,2"));
        // DIFFERENCE:
        // We do not announce support for MS-DAVEXT
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.getFirstHeader("X-MSDAVEXT").getValue().equals("1"));
        // DIFFERENCE:
        // We do not announce the additional DAV namespace. This header is optional according to MS documentation.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.getFirstHeader("Public-Extension").getValue().equals("http://schemas.microsoft.com/repl-2"));
	}
	
	public void testPropfindAuthenticated() throws Exception
	{
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/669b1506-4ffa-4016-8597-c6df4aa29eed
        // Request 4
        HttpPropfind propfind = new HttpPropfind(new java.net.URI(SERVER_URL));
        HttpResponse serviceResponse = authenticatedHttpClient.execute(propfind);
        serviceResponse.getEntity().getContent().close();
		assertEquals(207, serviceResponse.getStatusLine().getStatusCode());
        assertFalse(serviceResponse.containsHeader("WWW-Authenticate"));
        assertTrue(serviceResponse.containsHeader("MicrosoftSharePointTeamServices"));
        // DIFFERENCE:
        // The following headers are not required for PROPFIND requests.
        // Although these headers are not send by a SharePoint, we do not explicitly check for their absence.
        // This can be changed in the future if we observe applications relying on this.
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.containsHeader("MS-Author-Via"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.containsHeader("MicrosoftOfficeWebServer"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.containsHeader("DocumentManagementServer"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.containsHeader("DAV"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.containsHeader("X-MSDAVEXT"));
        assertFalse(serviceResponse.containsHeader("X-MSFSSHTTP")); // we do *NOT* support this protocol. It is patent protected.
        // DIFFERENCE:
        // We do not announce the additional DAV namespace. This header is optional according to MS documentation.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("Public-Extension"));
        assertTrue(serviceResponse.getFirstHeader("MicrosoftSharePointTeamServices").getValue().startsWith("14.0.0."));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Properties Schema;"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Source Control;"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.getFirstHeader("DocumentManagementServer").getValue().contains("Version History;"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.getFirstHeader("DAV").getValue().equals("1,2"));
        // this is not required for PROPFIND requests to the server root: assertTrue(serviceResponse.getFirstHeader("X-MSDAVEXT").getValue().equals("1"));
        // DIFFERENCE:
        // We do not announce the additional DAV namespace. This header is optional according to MS documentation.
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.getFirstHeader("Public-Extension").getValue().equals("http://schemas.microsoft.com/repl-2"));
	}
    

    // DIFFERENCE:
    // We do not require authentication for the _vti_inf.html file in the server root. This behaviour simplifies deployment in
	// environments where this file is not served by our application server (e.g. directly from a remote proxy)
    // This can be changed in the future if we observe applications relying on this.
    // public void testVtiInfUnauthenticated() throws Exception
    // {
    // }
    
    public void testVtiInfAuthenticated() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/91141ce9-d151-4fff-b618-39cf289fe440
        // Request 4
        HttpGet get = new HttpGet(new java.net.URI(SERVER_URL+"/_vti_inf.html"));
        HttpResponse serviceResponse = authenticatedHttpClient.execute(get);
        assertEquals(200, serviceResponse.getStatusLine().getStatusCode());
        assertFalse(serviceResponse.containsHeader("WWW-Authenticate"));
        // DIFFERENCE:
        // We do not require any additional headers to be set on this response. This behaviour simplifies deployment in
        // environments where this file is not served by our application server (e.g. directly from a remote proxy)
        // This can be changed in the future if we observe applications relying on this.
        // assertTrue(serviceResponse.containsHeader("MicrosoftSharePointTeamServices"));
        // assertTrue(serviceResponse.containsHeader("Public-Extension"));
        // assertTrue(serviceResponse.getFirstHeader("MicrosoftSharePointTeamServices").getValue().startsWith("14.0.0."));
        // assertTrue(serviceResponse.getFirstHeader("Public-Extension").getValue().equals("http://schemas.microsoft.com/repl-2"));
        HttpEntity vtiInfEntity = serviceResponse.getEntity();
        byte[] vtiInf = streamToByteArray(vtiInfEntity.getContent());
        byte[] referenceSharePoint2010 = getResourceAsByteArray("reference/sharepoint2010/serverroot/vtiinf_response_body.bin");
        assertTrue(arrayCompare(vtiInf,referenceSharePoint2010));
    }

    private static byte[] streamToByteArray(InputStream in) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = -1;
        while( (read = in.read(buffer)) >= 0)
        {
            bos.write(buffer, 0, read);
        }
        return bos.toByteArray();
    }
    
    private static byte[] getResourceAsByteArray(String resource)
    {
        InputStream in = getResourceAsStream(resource);
        try
        {
            try
            {
                return streamToByteArray(in);
            }
            finally
            {
                in.close();
            }
        }
        catch(IOException ioe)
        {
            throw new RuntimeException("Error reading resource",ioe);
        }
    }

    private static InputStream getResourceAsStream(String resource)
    {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader!=null)
        {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if(stream == null)
        {
            stream = OfficeServicesRootSystemTest.class.getResourceAsStream(resource);
        }
        if(stream == null)
        {
            stream = OfficeServicesRootSystemTest.class.getClassLoader().getResourceAsStream(stripped);
        }
        if(stream == null)
        {
            throw new RuntimeException("Can not read resource " + resource);
        }
        return stream;
    }

    private static boolean arrayCompare(byte[] a1, byte a2[])
    {
        if(a1.length != a2.length)
        {
            return false;
        }
        for(int i = 0; i < a1.length; i++)
        {
            if(a1[i] != a2[i])
            {
                return false;
            }
        }
        return true;
    }
	
}
