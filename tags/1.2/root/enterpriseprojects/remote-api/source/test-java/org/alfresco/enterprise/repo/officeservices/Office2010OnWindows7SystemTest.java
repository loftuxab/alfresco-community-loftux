package org.alfresco.enterprise.repo.officeservices;

import java.net.URI;

import junit.framework.TestCase;

import com.xaldon.xoservices.testclient.office.Office2010Windows7Client;
import com.xaldon.xoservices.testclient.office.OfficeClientMessageReceiver;
import com.xaldon.xoservices.testclient.office.OfficeFileHandle;

public class Office2010OnWindows7SystemTest extends TestCase implements OfficeClientMessageReceiver
{
    
    private static final String USERNAME = "admin";

    private static final String PASSWORD = "admin";

    private static final String BASE_URL = "http://localhost:8080/alfresco/aos";
    
    private static final String TEST_FOLDER = "/Sites/swsdp/documentLibrary/Meeting%20Notes";
    
    private static final String TEST_DOCUMENT = "/Sites/swsdp/documentLibrary/Meeting%20Notes/Meeting%20Notes%202011-02-10.doc";

    private Office2010Windows7Client officeClient;
    
    public void setUp() throws Exception
    {
        officeClient = new Office2010Windows7Client(USERNAME,PASSWORD);
    }

    @Override
    public void message(String msg)
    {
        System.out.println(msg);
    }

    public void testFileOpenDialog() throws Exception
    {
        URI target = new URI(BASE_URL + TEST_FOLDER);
        officeClient.fileOpenDialog(target);
    }
    
    public void testFileSaveAsDialog() throws Exception
    {
        URI target = new URI(BASE_URL + TEST_FOLDER);
        officeClient.fileOpenDialog(target);
    }
    
    public void testFileOpenRefreshClose() throws Exception
    {
        URI target = new URI(BASE_URL + TEST_DOCUMENT);
        OfficeFileHandle ofh = officeClient.openFile(target);
        Thread.sleep(1000);
        officeClient.refreshLock(ofh);
        Thread.sleep(1000);
        officeClient.closeFile(ofh);
    }
    
    public void testCheckoutCheckin() throws Exception
    {
        URI target = new URI(BASE_URL + TEST_DOCUMENT);
        OfficeFileHandle ofh = officeClient.openFile(target);
        Thread.sleep(1000);
        officeClient.checkoutFile(ofh);
        Thread.sleep(1000);
        officeClient.checkinFile(ofh, "Enterprise System Build Test", true);
        Thread.sleep(1000);
        officeClient.closeFile(ofh);
    }

}
