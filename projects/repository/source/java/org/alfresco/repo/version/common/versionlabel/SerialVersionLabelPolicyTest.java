package org.alfresco.repo.version.common.versionlabel;

import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionType;
import org.alfresco.repo.version.common.VersionImpl;

import junit.framework.TestCase;

/**
 * Unit test class for SerialVersionLabelPolicy class
 * 
 * @author Roy Wetherall
 */
public class SerialVersionLabelPolicyTest extends TestCase
{
    /**
     * Test getVersionLabelValue
     */
    public void testGetVersionLabelValue()
    {
        SerialVersionLabelPolicy policy = new SerialVersionLabelPolicy();
        
        NodeRef dummyNodeRef = new NodeRef(new StoreRef("", ""), "");
        
        HashMap<String, Serializable> versionProp1 = new HashMap<String, Serializable>();
        versionProp1.put(Version.PROP_VERSION_TYPE, VersionType.MINOR);
        
        String initialVersion = policy.calculateVersionLabel(
				DictionaryBootstrap.TYPE_BASE,
                null,
                0,
                versionProp1);
        assertEquals("1.0", initialVersion);
        
        HashMap<String, Serializable> versionProp2 = new HashMap<String, Serializable>();
        versionProp2.put(Version.PROP_VERSION_LABEL, "1.0");
        Version version1 = new VersionImpl(versionProp2, dummyNodeRef);
        
        String verisonLabel1 = policy.calculateVersionLabel(
				DictionaryBootstrap.TYPE_BASE,                
                version1,
                1,
                versionProp1);
        assertEquals("1.1", verisonLabel1);
        
        HashMap<String, Serializable> versionProp3 = new HashMap<String, Serializable>();
        versionProp3.put(Version.PROP_VERSION_LABEL, "1.1");
        Version version2 = new VersionImpl(versionProp3, dummyNodeRef);
        
        HashMap<String, Serializable> versionProp4 = new HashMap<String, Serializable>();
        versionProp4.put(Version.PROP_VERSION_TYPE, VersionType.MAJOR);
        
        String verisonLabel2 = policy.calculateVersionLabel(
				DictionaryBootstrap.TYPE_BASE,
                version2,
                1,
                versionProp4);
        assertEquals("2.0", verisonLabel2);
    }

}
