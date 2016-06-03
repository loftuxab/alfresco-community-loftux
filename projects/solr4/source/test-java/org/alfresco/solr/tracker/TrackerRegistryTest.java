package org.alfresco.solr.tracker;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class TrackerRegistryTest
{
    private static TrackerRegistry reg = new TrackerRegistry();
    private static Tracker aclTracker = new AclTracker();
    private static Tracker contentTracker = new ContentTracker();
    private static Tracker metadataTracker = new MetadataTracker();
    private static Tracker modelTracker = new ModelTracker();
    private static final String CORE_NAME = "coreName";
    private static final String CORE2_NAME = "core2Name";
    private static final String CORE3_NAME = "core3Name";
    private static final String NOT_A_CORE_NAME = "not a core name";

    public static void registerTrackers(String coreName)
    {
        reg.register(coreName, aclTracker);
        reg.register(coreName, contentTracker );
        reg.register(coreName, metadataTracker);
        reg.register(coreName, modelTracker);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        registerTrackers(CORE_NAME);
    }

    
    @Test
    public void testGetCoreNames()
    {
        Set<String> coreNames = reg.getCoreNames();
        assertNotNull(coreNames);
        assertTrue(coreNames.contains(CORE_NAME));
        assertEquals(1, coreNames.size());
        
        registerTrackers(CORE2_NAME);
        coreNames = reg.getCoreNames();
        assertNotNull(coreNames);
        assertTrue(coreNames.contains(CORE_NAME));
        assertFalse(coreNames.contains(NOT_A_CORE_NAME));
        assertEquals(2, coreNames.size());
    }

    @Test
    public void testGetTrackersForCore()
    {
        Collection<Tracker> trackersForCore = reg.getTrackersForCore(CORE_NAME);
        assertNotNull(trackersForCore);
        assertFalse(trackersForCore.isEmpty());
        assertTrue(trackersForCore.contains(aclTracker));
        assertTrue(trackersForCore.contains(contentTracker));
        assertTrue(trackersForCore.contains(modelTracker));
        assertTrue(trackersForCore.contains(metadataTracker));
        
        trackersForCore = reg.getTrackersForCore(NOT_A_CORE_NAME);
        assertNull(trackersForCore);
    }

    @Test
    public void testHasTrackersForCore()
    {
        assertTrue(reg.hasTrackersForCore(CORE_NAME));
        assertFalse(reg.hasTrackersForCore(NOT_A_CORE_NAME));
    }

    @Test
    public void testGetTrackerForCore()
    {
        assertEquals(aclTracker, reg.getTrackerForCore(CORE_NAME, AclTracker.class));
        assertEquals(contentTracker, reg.getTrackerForCore(CORE_NAME, ContentTracker.class));
        assertEquals(metadataTracker, reg.getTrackerForCore(CORE_NAME, MetadataTracker.class));
        assertEquals(modelTracker, reg.getTrackerForCore(CORE_NAME, ModelTracker.class));
    }
    
    @Test
    public void testRemoveTrackersForCore()
    {
        registerTrackers(CORE3_NAME);
        boolean thereWereTrackers = reg.removeTrackersForCore(CORE3_NAME);
        assertTrue(thereWereTrackers);
        assertNull(reg.getTrackersForCore(CORE3_NAME));
        thereWereTrackers = reg.removeTrackersForCore(NOT_A_CORE_NAME);
        assertFalse(thereWereTrackers);
    }
}
