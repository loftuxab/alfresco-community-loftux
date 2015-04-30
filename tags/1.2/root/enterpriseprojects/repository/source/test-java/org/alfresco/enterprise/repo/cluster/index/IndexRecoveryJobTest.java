/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.cluster.index;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.repo.node.index.IndexRecovery;
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
 * Tests for the {@link IndexRecoveryJob} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexRecoveryJobTest
{
    // Class under test
    private IndexRecoveryJob reindexJob;
    private @Mock JobExecutionContext jobContext;
    private @Mock IndexRecovery reindexer;
    private @Mock JobDetail jobDetail;
    private @Mock ClusterService clusterService;
    private JobDataMap jobDataMap;
    
    @Before
    public void setUp() throws Exception
    {
        reindexJob = new IndexRecoveryJob();        
        jobDataMap = new JobDataMap();
        jobDataMap.put(IndexRecoveryJob.KEY_INDEX_RECOVERY_COMPONENT, reindexer);
        jobDataMap.put(IndexRecoveryJob.KEY_CLUSTER_SERVICE, clusterService);
        when(jobContext.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
    }

    @Test
    public void testBuildFixHack() throws JobExecutionException
    {
        // See minimal-context.xml ClusterService definition.
        String clusterServiceAsString = "";
        jobDataMap.put(IndexRecoveryJob.KEY_CLUSTER_SERVICE, clusterServiceAsString);
        
        reindexJob.execute(jobContext);
        
        verify(reindexer, never()).reindex();
    }
    
    @Test
    public void testWhenClusteringDisabled() throws JobExecutionException
    {
        when(clusterService.isClusteringEnabled()).thenReturn(false);
        
        reindexJob.execute(jobContext);
        
        verify(reindexer, never()).reindex();
    }
    
    @Test
    public void testWhenClusteringNotInitialised() throws JobExecutionException
    {
        when(clusterService.isClusteringEnabled()).thenReturn(true);
        when(clusterService.isInitialised()).thenReturn(false);
        
        reindexJob.execute(jobContext);
        
        verify(reindexer, never()).reindex();
    }
    
    @Test
    public void testWhenClusterHasOnlyOneMember() throws JobExecutionException
    {
        when(clusterService.isClusteringEnabled()).thenReturn(true);
        when(clusterService.isInitialised()).thenReturn(true);
        when(clusterService.getNumActiveClusterMembers()).thenReturn(1);
        
        reindexJob.execute(jobContext);
        
        verify(reindexer, never()).reindex();
    }
    
    @Test
    public void testWhenClusterHasMultipleMembers() throws JobExecutionException
    {
        when(clusterService.isClusteringEnabled()).thenReturn(true);
        when(clusterService.isInitialised()).thenReturn(true);
        when(clusterService.getNumActiveClusterMembers()).thenReturn(2);
        
        reindexJob.execute(jobContext);
        
        verify(reindexer).reindex();
    }
}
