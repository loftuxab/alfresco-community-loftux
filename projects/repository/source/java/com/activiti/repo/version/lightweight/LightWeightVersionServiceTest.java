package com.activiti.repo.version.lightweight;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.activiti.repo.domain.Node;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.StoreService;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionService;
import com.activiti.repo.version.common.counter.VersionCounterDaoService;
import com.activiti.util.BaseSpringTest;

/**
 * LightWeightVersionService test class.
 * 
 * @author Roy Wetherall
 */
public class LightWeightVersionServiceTest extends BaseSpringTest
{
    /*
     * Services used by the tests
     */
    private StoreService storeService = null;
    private NodeService nodeService = null;
    private VersionService versionService = null;
    private VersionCounterDaoService counter = null;

    /*
     * Data used by tests
     */
    private NodeRef rootNodeRef = null;
    private Map<String,String> versionProperties = null;
    
    /*
     * Proprety names and values
     */
    private final static QName PROP_1 = QName.createQName("{test}prop1");
    private final static QName PROP_2 = QName.createQName("{test}prop2");
    private final static QName PROP_3 = QName.createQName("{test}prop3");
    private final static String VERSION_PROP_1 = "versionProp1";
    private final static String VERSION_PROP_2 = "versionProp2";
    private final static String VERSION_PROP_3 = "versionProp3";
    private final static String VALUE_1 = "value1";
    private final static String VALUE_2 = "value2";
    private final static String VALUE_3 = "value3";    

    /**
     * Set the store service
     * 
     * @param storeService  store service
     */
    public void setStoreService(StoreService storeService)
    {
        this.storeService = storeService;
    }

    /**
     * Set the node service
     * 
     * @param nodeService  node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the version service
     * 
     * @param versionService  version service
     */
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;        
    }    

    /**
     * Set the version counter dao service
     * 
     * @param counter  the version counter service
     */
    public void setVersionCounterDaoService(VersionCounterDaoService counter)
    {
        this.counter = counter;
    }
    
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        // Create a bag of properties for later use
        this.versionProperties = new HashMap<String, String>();
        versionProperties.put(LightWeightVersionServiceTest.VERSION_PROP_1, LightWeightVersionServiceTest.VALUE_1);
        versionProperties.put(LightWeightVersionServiceTest.VERSION_PROP_2, LightWeightVersionServiceTest.VALUE_2);
        versionProperties.put(LightWeightVersionServiceTest.VERSION_PROP_3, LightWeightVersionServiceTest.VALUE_3);
        
        // Create a workspace that contains the 'live' nodes
        StoreRef storeRef = this.storeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        this.rootNodeRef = this.storeService.getRootNode(storeRef);
    }

    /**
     * Tests creating a version that has no existing version history.
     * <p>
     * Should create a new version history in the version sore with a
     * root version that relates to the current state of the versioned
     * node.
     */
    public void testCreateVersionWithNoExistingVersionHistory()
    {
        // Create the node properties
        HashMap<QName, Serializable> nodeProperties = new HashMap<QName, Serializable>();
        nodeProperties.put(LightWeightVersionServiceTest.PROP_1, LightWeightVersionServiceTest.VALUE_1);
        nodeProperties.put(LightWeightVersionServiceTest.PROP_2, LightWeightVersionServiceTest.VALUE_2);
        nodeProperties.put(LightWeightVersionServiceTest.PROP_3, LightWeightVersionServiceTest.VALUE_3);
            
        // Create the versionable node
        NodeRef versionableNode = nodeService.createNode(
                rootNodeRef, 
                QName.createQName("{}MyVersionableNode"), 
                Node.TYPE_REAL,
                nodeProperties);
        assertNotNull(versionableNode);
        
        // Version the node for the first time
        Version version = createNewVersion(versionableNode, versionProperties);
        
        // Check the node ref for the current version
        String currentVersionLabel = (String)this.nodeService.getProperty(
                versionableNode,
                VersionService.ATTR_CURRENT_VERSION_LABEL);
        assertEquals(version.getVersionLabel(), currentVersionLabel);
                
        // Version it again
        //Version version2 = createNewVersion(versionableNode, versionProperties);
    }
    
    /**
     * Creates a new version, checking the properties of the version.
     * 
     * @param versionableNode    the versionable node
     * @param versionProperties  the version properties
     * @return                   the created (and checked) new version
     */
    private Version createNewVersion(NodeRef versionableNode, Map<String, String> versionProperties)
    {
        // Get the next version number
        // TODO this check of the version number presumes the default version label policy
        StoreRef lwVersionStoreRef = this.versionService.getVersionStoreReference();
        int nextVersion = this.counter.currentVersionNumber(lwVersionStoreRef) + 1; 
        
        // Snap-shot the date-time
        long beforeVersionTime = System.currentTimeMillis();
        
        // Now lets create a new version for this node
        Version newVersion = versionService.createVersion(versionableNode, this.versionProperties);
        assertNotNull(newVersion);
        
        // Check the version label
        assertEquals(Integer.toString(nextVersion), newVersion.getVersionLabel());
        
        // Check the created date
        long afterVersionTime = System.currentTimeMillis();
        long createdDate = newVersion.getCreatedDate().getTime();
        if (createdDate < beforeVersionTime || createdDate > afterVersionTime)
        {
            fail("The created date of the version is incorrect.");
        }
        
        // Check the properties of the verison
        Map<String, String> props = newVersion.getVersionProperties();
        assertNotNull(props);
        assertEquals(versionProperties.size(), props.size());
        for (String key : versionProperties.keySet())
        {
            assertEquals(
                    versionProperties.get(key), 
                    newVersion.getVersionProperty(key));
        }
        
        // Check that the node reference is correct
        NodeRef nodeRef = newVersion.getNodeRef();
        assertNotNull(nodeRef);
        assertEquals(this.versionService.getVersionStoreReference(), nodeRef.getStoreRef());
        assertNotNull(nodeRef.getId());        
        
        // Return the new version
        return newVersion;
    }
      
}
