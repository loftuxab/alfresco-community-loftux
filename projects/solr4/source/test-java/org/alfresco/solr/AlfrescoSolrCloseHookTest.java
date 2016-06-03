
package org.alfresco.solr;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.alfresco.solr.tracker.AclTracker;
import org.alfresco.solr.tracker.ContentTracker;
import org.alfresco.solr.tracker.MetadataTracker;
import org.alfresco.solr.tracker.ModelTracker;
import org.alfresco.solr.tracker.SolrTrackerScheduler;
import org.alfresco.solr.tracker.Tracker;
import org.alfresco.solr.tracker.TrackerRegistry;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.SchedulerException;

@RunWith(MockitoJUnitRunner.class)
public class AlfrescoSolrCloseHookTest
{
    private SolrCore core;
    private AlfrescoSolrCloseHook hook;

    @Mock
    private AlfrescoCoreAdminHandler adminHandler;
    @Mock
    private SolrTrackerScheduler scheduler;
    @Mock
    private ModelTracker modelTracker;
    @Mock
    private ContentTracker contentTracker;
    @Mock
    private MetadataTracker metadataTracker;
    @Mock
    private AclTracker aclTracker;
    @Mock
    private TrackerRegistry trackerRegistry;

    private final String CORE_NAME = "coreName";
    private Collection<Tracker> coreTrackers;

    @Before
    public void setUp() throws Exception
    {
        when(trackerRegistry.getModelTracker()).thenReturn(modelTracker);
        coreTrackers = Arrays.asList(new Tracker[] { contentTracker, metadataTracker, aclTracker });
        when(trackerRegistry.getTrackersForCore(CORE_NAME)).thenReturn(coreTrackers);
        when(trackerRegistry.getCoreNames()).thenReturn(new HashSet<String>(Arrays.asList(CORE_NAME)));
        when(adminHandler.getTrackerRegistry()).thenReturn(trackerRegistry);
        when(adminHandler.getScheduler()).thenReturn(scheduler);
        
        core = new SolrCore(CORE_NAME, new CoreDescriptor(new CoreContainer(), CORE_NAME, "instanceDir"));
        hook = new AlfrescoSolrCloseHook(adminHandler);
    }

    @Test
    public void testPreCloseSolrCore() throws SchedulerException
    {
        // Runs system under test
        hook.preClose(core);
        
        // Validates behavior
        verify(modelTracker).setShutdown(true);
        verify(aclTracker).setShutdown(true);
        verify(contentTracker).setShutdown(true);
        verify(metadataTracker).setShutdown(true);
        
        verify(modelTracker).close();
        verify(aclTracker).close();
        verify(contentTracker).close();
        verify(metadataTracker).close();
        verify(trackerRegistry).removeTrackersForCore(CORE_NAME);

        verify(scheduler).pauseAll();
        verify(scheduler).shutdown();
        verify(scheduler).deleteTrackerJobs(CORE_NAME, coreTrackers);
        verify(scheduler).deleteTrackerJob(CORE_NAME, modelTracker);
    }
}
