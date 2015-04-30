package org.alfresco.enterprise.repo.officeservices;

import java.net.URI;

import junit.framework.TestCase;

import com.xaldon.xoservices.testclient.ServiceResponseException;
import com.xaldon.xoservices.testclient.XoservicesClient;
import com.xaldon.xoservices.testclient.XoservicesClient.FPSEUrlToWebUrl;
import com.xaldon.xoservices.testclient.XoservicesClient.FPSEVersion;

public class OfficeServicesVtiBinSystemTest extends TestCase
{
    
    private static final String USERNAME = "admin";

    private static final String PASSWORD = "admin";

    private static final String SERVER_URL = "http://localhost:8080";

    private XoservicesClient servicesClient;
    
    public void setUp() throws Exception
    {
        servicesClient = new XoservicesClient(USERNAME,PASSWORD);
    }

    public void testServerVersion() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/23d66749-f03d-44c6-b5f5-c8db66709748
        // Request 1
        FPSEVersion serverVersion = servicesClient.getFPSEVersion(new URI(SERVER_URL));
        assertEquals("Expected major FPSE protocol version 14",14,serverVersion.major);
        assertEquals("Expected minor FPSE protocol version 0",0,serverVersion.minor);
        assertEquals("Expected phase FPSE protocol version 0",0,serverVersion.phase);
        assertEquals("Expected increment FPSE protocol version 4730",4730,serverVersion.increase);
    }
    
    public void testUrlToWebUrlSimpleDocument() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 1
        FPSEUrlToWebUrl split = servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/alfresco/aos/foo/bar.docx"));
        assertEquals("/alfresco/aos",split.webUrl);
        assertEquals("foo/bar.docx",split.fileUrl);
    }
    
    public void testUrlToWebUrlOutsideSite() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 4
        try
        {
            servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/something/else/foo/bar.docx"));
            fail("Requests outside /alfresco/aos site have to fail");
        }
        catch(ServiceResponseException e)
        {
            // expected is 200 status code with properly formatted FPSE error. The XoservicesClient returns the following message in that case
            assertTrue("Invalid error response", e.getMessage().startsWith("Service returned error."));
            // expected. Has to fail.
        }
    }
    
    public void testUrlToWebUrlAlfrescoOnly() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 4
        try
        {
            servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/alfresco/foo/bar.docx"));
            fail("Requests outside /alfresco/aos site have to fail");
        }
        catch(ServiceResponseException e)
        {
            // expected is 200 status code with properly formatted FPSE error. The XoservicesClient returns the following message in that case
            assertTrue("Invalid error response", e.getMessage().startsWith("Service returned error."));
            // expected. Has to fail.
        }
    }
    
    public void testUrlToWebUrlServerRoot() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 4
        try
        {
            servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL));
            fail("Requests outside /alfresco/aos site have to fail");
        }
        catch(ServiceResponseException e)
        {
            // expected is 200 status code with properly formatted FPSE error. The XoservicesClient returns the following message in that case
            assertTrue("Invalid error response", e.getMessage().startsWith("Service returned error."));
            // expected. Has to fail.
        }
        try
        {
            servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/"));
            fail("Requests outside /alfresco/aos site have to fail");
        }
        catch(ServiceResponseException e)
        {
            // expected is 200 status code with properly formatted FPSE error. The XoservicesClient returns the following message in that case
            assertTrue("Invalid error response", e.getMessage().startsWith("Service returned error."));
            // expected. Has to fail.
        }
    }
    
    public void testUrlToWebUrlSiteRoot() throws Exception
    {
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 2
        FPSEUrlToWebUrl split = servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/alfresco/aos/"));
        assertEquals("/alfresco/aos",split.webUrl);
        assertEquals("",split.fileUrl);
        // See
        // https://ts.alfresco.com/share/page/site/alfresco-one-platform-team/document-details?nodeRef=workspace://SpacesStore/f81e055f-fe9e-4f43-9e91-f5e06cf1c6a7
        // Request 3
        split = servicesClient.getFPSEUrlToWebUrl(new URI(SERVER_URL+"/alfresco/aos"));
        assertEquals("/alfresco/aos",split.webUrl);
        assertEquals("",split.fileUrl);
    }

}
