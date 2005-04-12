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

public class VersionStoreBaseImplTest extends BaseSpringTest 
{
	/*
     * Services used by the tests
     */
	protected NodeService dbNodeService = null;
	protected StoreService dbStoreService = null;
    protected VersionService lightWeightVersionStoreVersionService = null;
    protected VersionCounterDaoService versionCounterDaoService = null;
	
    /*
     * Data used by tests
     */
    protected NodeRef rootNodeRef = null;
    protected Map<String,String> versionProperties = null;
    protected HashMap<QName, Serializable> nodeProperties = null;
    
    /*
     * Proprety names and values
     */
    protected final static QName PROP_1 = QName.createQName("{test}prop1");
	protected final static QName PROP_2 = QName.createQName("{test}prop2");
	protected final static QName PROP_3 = QName.createQName("{test}prop3");
	protected final static String VERSION_PROP_1 = "versionProp1";
	protected final static String VERSION_PROP_2 = "versionProp2";
	protected final static String VERSION_PROP_3 = "versionProp3";
	protected final static String VALUE_1 = "value1";
	protected final static String VALUE_2 = "value2";
	protected final static String VALUE_3 = "value3";  
	
	/**
	 * Constructor.  Auto-wire by name is required due to type conflicts
	 */
	public VersionStoreBaseImplTest()
	{
		setPopulateProtectedVariables(true);
	}
    
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        // Create a bag of properties for later use
        this.versionProperties = new HashMap<String, String>();
        versionProperties.put(VERSION_PROP_1, VALUE_1);
        versionProperties.put(VERSION_PROP_2, VALUE_2);
        versionProperties.put(VERSION_PROP_3, VALUE_3);
        
        // Create the node properties
        this.nodeProperties = new HashMap<QName, Serializable>();
        this.nodeProperties.put(VersionStoreVersionServiceImplTest.PROP_1, VersionStoreVersionServiceImplTest.VALUE_1);
        this.nodeProperties.put(VersionStoreVersionServiceImplTest.PROP_2, VersionStoreVersionServiceImplTest.VALUE_2);
        this.nodeProperties.put(VersionStoreVersionServiceImplTest.PROP_3, VersionStoreVersionServiceImplTest.VALUE_3);
        
        // Create a workspace that contains the 'live' nodes
        StoreRef storeRef = this.dbStoreService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        this.rootNodeRef = this.dbStoreService.getRootNode(storeRef);
    }
	
	public void testGetVersionStoreReference() 
	{
	}

	public void testGetVersionHistoryNodeRef() 
	{
	}

	public void testGetCurrentVersionNodeRef() 
	{
	}
    
    /**
     * Creates a new versionable node
     * 
     * @return  the node reference
     */
    protected NodeRef createNewVersionableNode()
    {
        NodeRef nodeRef = this.dbNodeService.createNode(
                rootNodeRef, 
                QName.createQName("{}MyVersionableNode"), 
                Node.TYPE_CONTAINER,
                this.nodeProperties).getChildRef();
        assertNotNull(nodeRef);
        
        return nodeRef;
    }
    
    /**
     * Creates a new version, checking the properties of the version.
     * <p>
     * The default test propreties are assigned to the version.
     * 
     * @param versionableNode    the versionable node
     * @return                   the created (and checked) new version
     */
    protected Version createVersion(NodeRef versionableNode)
    {
        return createVersion(versionableNode, this.versionProperties);
    }
    
    /**
     * Creates a new version, checking the properties of the version.
     * 
     * @param versionableNode    the versionable node
     * @param versionProperties  the version properties
     * @return                   the created (and checked) new version
     */
    protected Version createVersion(NodeRef versionableNode, Map<String, String> versionProperties)
    {
        // Get the next version number
        // TODO this check of the version number presumes the default version label policy
        int nextVersion = peekNextVersionNumber(); 
        
        // Snap-shot the date-time
        long beforeVersionTime = System.currentTimeMillis();
        
        // Now lets create a new version for this node
        Version newVersion = lightWeightVersionStoreVersionService.createVersion(versionableNode, this.versionProperties);
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
        // TODO sort this out
        //assertEquals(versionProperties.size(), props.size());
        for (String key : versionProperties.keySet())
        {
            assertEquals(
                    versionProperties.get(key), 
                    newVersion.getVersionProperty(key));
        }
        
        // Check that the node reference is correct
        NodeRef nodeRef = newVersion.getNodeRef();
        assertNotNull(nodeRef);
        assertEquals(this.lightWeightVersionStoreVersionService.getVersionStoreReference(), nodeRef.getStoreRef());
        assertNotNull(nodeRef.getId());        
        
        // TODO How do we check the frozen attributes ??
        
        // Check the node ref for the current version
        String currentVersionLabel = (String)this.dbNodeService.getProperty(
                versionableNode,
                VersionService.ATTR_CURRENT_VERSION_LABEL);
        assertEquals(newVersion.getVersionLabel(), currentVersionLabel);
        
        // Return the new version
        return newVersion;
    }
    
    /**
     * Returns the next version number without affecting the version counter.
     * 
     * @return  the next version number to be allocated
     */
    protected int peekNextVersionNumber()
    {
        StoreRef lwVersionStoreRef = this.lightWeightVersionStoreVersionService.getVersionStoreReference();
        return this.versionCounterDaoService.currentVersionNumber(lwVersionStoreRef) + 1; 
    }

}
