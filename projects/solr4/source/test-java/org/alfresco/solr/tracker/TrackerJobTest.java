package org.alfresco.solr.tracker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Tests for the {@link TrackerJob} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class TrackerJobTest
{
    private TrackerJob trackerJob; // Class under test
    private @Mock Tracker tracker;
    private @Mock JobExecutionContext jec;
    private @Mock JobDetail jobDetail;
    private JobDataMap jobDataMap;
    
    @Before
    public void setUp() throws Exception
    
    {
        trackerJob = new TrackerJob();
        
        jobDataMap = new JobDataMap();
        jobDataMap.put("TRACKER", tracker);
        
        when(jec.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
    }

    @Test
    public void canExecuteTrackerJob() throws JobExecutionException
    {
        trackerJob.execute(jec);
        
        // When the TrackerJob is triggered, then the Tracker's execute() method
        // should be invoked.
        verify(tracker).track();
    }

}
