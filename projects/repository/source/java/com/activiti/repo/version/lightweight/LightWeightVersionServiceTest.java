package com.activiti.repo.version.lightweight;

import java.util.HashMap;
import java.util.Map;

import com.activiti.repo.domain.Node;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.StoreService;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionService;
import com.activiti.util.BaseSpringTest;

/**
 * LightWeightVersionService test class.
 * 
 * @author Roy Wetherall
 */
public class LightWeightVersionServiceTest extends BaseSpringTest
{
    /**
     * Services used by the tests
     */
    private StoreService storeService = null;
    private NodeService nodeService = null;
    private VersionService versionService = null;

    /**
     * Data used by tests
     */
    private NodeRef rootNodeRef = null;
    private Map<String,String> versionProperties = null;
    
    /**
     * Proprety names and values
     */
    private final static String PROP_1 = "prop1";
    private final static String PROP_2 = "prop2";
    private final static String PROP_3 = "prop3";
    private final static String VALUE_1 = "value1";
    private final static String VALUE_2 = "value2";
    private final static String VALUE_3 = "value3";    

    /**
     * Set the store service
     * 
     * @param storeService store service
     */
    public void setStoreService(StoreService storeService)
    {
        this.storeService = storeService;
    }

    /**
     * Set the node service
     * 
     * @param nodeService node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the version service
     * 
     * @param versionService version service
     */
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;        
    }    

    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        // Create a bag of properties for later use
        this.versionProperties = new HashMap<String, String>();
        versionProperties.put(LightWeightVersionServiceTest.PROP_1, LightWeightVersionServiceTest.VALUE_1);
        versionProperties.put(LightWeightVersionServiceTest.PROP_2, LightWeightVersionServiceTest.VALUE_2);
        versionProperties.put(LightWeightVersionServiceTest.PROP_3, LightWeightVersionServiceTest.VALUE_3);
        
        // Create a workspace that contains the 'live' nodes
        StoreRef storeRef = this.storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        this.rootNodeRef = this.storeService.getRootNode(storeRef);
    }

    /**
     * Tests creating a version that has no existing version history.
     * 
     * Should create a new version history in the version sore with a
     * root version that relates to the current state of the versioned
     * node.
     */
    public void testCreateVersionWithNoExistingVersionHistory()
    {
        NodeRef versionableNode = nodeService.createNode(rootNodeRef, null, "MyVersionableNode", Node.TYPE_REAL);
        assertNotNull(versionableNode);
        
        // Now lets create a new version for this node
        Version newVersion = versionService.createVersion(versionableNode, this.versionProperties);
        assertNotNull(newVersion);
        
        // Check the version label
        //assertEquals("0", newVersion.getVersionLabel());
        
        // Check the created date
        
        // TODO check that the properties have been set correctly
        
        // TODO check that the node ref returned for the frozen state is correct
        
        // TODO need to revert the version counter some how !!!
    }
      
}
