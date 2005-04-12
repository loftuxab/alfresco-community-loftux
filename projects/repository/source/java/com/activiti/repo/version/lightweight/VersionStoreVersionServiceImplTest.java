package com.activiti.repo.version.lightweight;

import java.util.Collection;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionHistory;
import com.activiti.repo.version.VersionService;

/**
 * LightWeightVersionService test class.
 * 
 * @author Roy Wetherall
 */
public class VersionStoreVersionServiceImplTest extends VersionStoreBaseImplTest
{
	/**
     * Tests the creation of the initial version of a versionable node
     */
    public void testCreateIntialVersion()
    {
        NodeRef versionableNode = createNewVersionableNode();
        createVersion(versionableNode);
    }     
    
    /**
     * 
     */
    public void testCreateManyVersionsSameWorkspace()
    {
        NodeRef versionableNode = createNewVersionableNode();
        createVersion(versionableNode);
        // TODO mess with some of the properties and stuff as you version
        createVersion(versionableNode);
        // TODO mess with some of the properties and stuff as you version
        createVersion(versionableNode);
    }
    
    // TODO test versioning a non versionable node
    
    // TODO test versioning numberious times with branchs implies by different workspaces
    
    public void testNoVersionHistory()
    {
        NodeRef nodeRef = createNewVersionableNode();
        
        VersionHistory vh = this.lightWeightVersionStoreVersionService.getVersionHistory(nodeRef);
        assertNull(vh);
    }
    
    /**
     */
    public void testGetVersionHistorySameWorkspace()
    {
        NodeRef versionableNode = createNewVersionableNode();
        
        Version version1 = addToVersionHistory(versionableNode, null);
        Version version2 = addToVersionHistory(versionableNode, version1);
        Version version3 = addToVersionHistory(versionableNode, version2);
        Version version4 = addToVersionHistory(versionableNode, version3);
        addToVersionHistory(versionableNode, version4);        
    }
    
    /**
     * Adds another version to the version history then checks that getVersionHistory is returning
     * the correct data.
     * 
     * @param versionableNode  the versionable node reference
     * @param parentVersion    the parent version
     */
    private Version addToVersionHistory(NodeRef versionableNode, Version parentVersion)
    {
        //int initialVersionNumber = peekNextVersionNumber();
        Version createdVersion = createVersion(versionableNode);
        
        VersionHistory vh = this.lightWeightVersionStoreVersionService.getVersionHistory(versionableNode);
        assertNotNull(vh);
        
        if (parentVersion == null)
        {
            // Check the root is the newly created version
            Version root = vh.getRootVersion();
            assertNotNull(root);
            assertEquals(createdVersion.getVersionLabel(), root.getVersionLabel());
        }
        
        // Get the version from the version history
        Version version = vh.getVersion(createdVersion.getVersionLabel());
        assertNotNull(version);
        assertEquals(createdVersion.getVersionLabel(), version.getVersionLabel());
        
        // Check that the version is a leaf node of the version history (since it is newly created)
        Collection<Version> suc = vh.getSuccessors(version);
        assertNotNull(suc);
        assertEquals(0, suc.size());
        
        // Check that the predessor is the passed parent version (if root version should be null)
        Version pre = vh.getPredecessor(version);
        if (parentVersion == null)
        {
            assertNull(pre);
        }
        else
        {
            assertNotNull(pre);
            assertEquals(parentVersion.getVersionLabel(), pre.getVersionLabel());
        }
        
        if (parentVersion != null)
        {
            // Check that the successors of the parent are the created version
            Collection<Version> parentSuc = vh.getSuccessors(parentVersion);
            assertNotNull(parentSuc);
            assertEquals(1, parentSuc.size());
            Version tempVersion = (Version)parentSuc.toArray()[0];
            assertEquals(version.getVersionLabel(), tempVersion.getVersionLabel());
        }
        
        return createdVersion;
    }
}
