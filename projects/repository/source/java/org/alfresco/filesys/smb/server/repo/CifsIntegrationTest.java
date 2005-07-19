package org.alfresco.filesys.smb.server.repo;

import junit.framework.TestCase;

import org.alfresco.filesys.CIFSServer;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Checks that the required configuration details are obtainable from the CIFS components.
 * 
 * @author Derek Hulley
 */
public class CifsIntegrationTest extends TestCase
{
    /** the context to keep between tests */
    public static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    public void testGetServerName()
    {
        CIFSServer cifsServer = (CIFSServer) ctx.getBean("cifsServer");
        assertNotNull("No CIFS server available", cifsServer);
        
        // get the server name
        String serverName = cifsServer.getConfiguration().getServerName();
        assertNotNull("No server name available", serverName);
        assertTrue("No server name available (zero length)", serverName.length() > 0);
    }
    
    public void testGetRootPath()
    {
        ContentDiskInterface diskInterface = (ContentDiskInterface) ctx.getBean("contentDiskDriver");
        assertNotNull("No content disk interface found", diskInterface);
        
        // get the root share name
        String shareName = diskInterface.getShareName();
        assertNotNull("No share name available", shareName);
        assertTrue("No share name available (zero length)", shareName.length() > 0);
        
        NodeService nodeService = (NodeService) ctx.getBean("nodeService");
        // get the share root node and check that it exists
        NodeRef shareNodeRef = diskInterface.getContextRootNodeRef();
        assertNotNull("No share root node available", shareNodeRef);
        assertTrue("Share root node doesn't exist", nodeService.exists(shareNodeRef));
    }
}
