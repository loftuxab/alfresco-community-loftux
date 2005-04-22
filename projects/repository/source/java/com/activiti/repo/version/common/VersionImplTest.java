/*
 * Created on Mar 29, 2005
 *
 */
package com.activiti.repo.version.common;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.exception.VersionServiceException;

import junit.framework.TestCase;

/**
 * VersionImpl Unit Test
 * 
 * @author Roy Wetherall
 */
public class VersionImplTest extends TestCase
{
    /**
     * Property names and values
     */
    private final static String PROP_1 = "prop1";
    private final static String PROP_2 = "prop2";
    private final static String PROP_3 = "prop3";
    private final static String VALUE_1 = "value1";
    private final static String VALUE_2 = "value2";
    private final static String VALUE_3 = "value3";  
    
    /**
     * Version labels
     */
    private final static String VERSION_1 = "1";
    
    /**
     * Data used during tests
     */
    private VersionImpl version = null;
    private NodeRef nodeRef = null;
    private Map<String, Serializable> versionProperties = null;
    private Date createdDate = new Date();

    /**
     * Test case set up
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Create the node reference
        this.nodeRef = new NodeRef(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "testWS"), "testID");
        assertNotNull(this.nodeRef);
        
        // Create the version property map
        this.versionProperties = new HashMap<String, Serializable>();
        this.versionProperties.put(Version.PROP_VERSION_LABEL, VersionImplTest.VERSION_1);
        this.versionProperties.put(Version.PROP_CREATED_DATE, this.createdDate);
        this.versionProperties.put(VersionImplTest.PROP_1, VersionImplTest.VALUE_1);
        this.versionProperties.put(VersionImplTest.PROP_2, VersionImplTest.VALUE_2);
        this.versionProperties.put(VersionImplTest.PROP_3, VersionImplTest.VALUE_3);
        
        // Create the root version
        this.version = new VersionImpl(this.versionProperties, this.nodeRef);
        assertNotNull(this.version);
    }
    

    /**
     * Test getCreatedDate()
     */
    public void testGetCreatedDate()
    {
        Date createdDate1 = this.version.getCreatedDate();
        assertEquals(this.createdDate, createdDate1);
    }

    /**
     * Test getVersionLabel()
     */
    public void testGetVersionLabel()
    {
        String versionLabel1 = this.version.getVersionLabel();
        assertEquals(VersionImplTest.VERSION_1, versionLabel1);
    }
    
    /**
     * Test getVersionProperties
     *
     */
    public void testGetVersionProperties()
    {
        Map<String, Serializable> versionProperties = version.getVersionProperties();
        assertNotNull(versionProperties);
        assertEquals(this.versionProperties.size(), versionProperties.size());
    }

    /**
     * Test getVersionProperty
     */
    public void testGetVersionProperty()
    {
        String value1 = (String)version.getVersionProperty(VersionImplTest.PROP_1);
        assertEquals(value1, VersionImplTest.VALUE_1);
        
        String value2 = (String)version.getVersionProperty(VersionImplTest.PROP_2);
        assertEquals(value2, VersionImplTest.VALUE_2);
        
        String value3 = (String)version.getVersionProperty(VersionImplTest.PROP_3);
        assertEquals(value3, VersionImplTest.VALUE_3);
    }

    /**
     * Test getNodeRef()
     */
    public void testGetNodeRef()
    {
        NodeRef nodeRef = this.version.getNodeRef();
        assertNotNull(nodeRef);
        assertEquals(nodeRef.toString(), this.nodeRef.toString());
    }
    
    /**
     * Exception case - no node ref supplied when creating a verison
     */
    public void testNoNodeRefOnVersionCreate()
    {
        try
        {
            VersionImpl badVersion = new VersionImpl(this.versionProperties, null);
            fail("It is invalid to create a version object without a node ref specified.");
        }
        catch (VersionServiceException exception)
        {
        }
    }    
}
