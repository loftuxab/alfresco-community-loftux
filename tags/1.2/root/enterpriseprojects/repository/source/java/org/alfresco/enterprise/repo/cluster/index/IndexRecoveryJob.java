/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.index;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.repo.node.index.IndexRecovery;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Forces a index recovery using the {@link IndexRecovery recovery component} passed
 * in via the job detail.
 * <p>
 * Nothing is done if the cluster is not available or has fewer than two members.
 * 
 * @author Derek Hulley
 * @author Matt Ward
 */
public class IndexRecoveryJob implements Job
{
    public static final String KEY_INDEX_RECOVERY_COMPONENT = "indexRecoveryComponent";
    public static final String KEY_CLUSTER_SERVICE = "clusterService";
    
    /**
     * Forces a full index recovery using the {@link IndexRecovery recovery component} passed
     * in via the job detail.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        IndexRecovery indexRecoveryComponent = (IndexRecovery) map.get(KEY_INDEX_RECOVERY_COMPONENT);
        if (indexRecoveryComponent == null)
        {
            throw new JobExecutionException("Missing job data: " + KEY_INDEX_RECOVERY_COMPONENT);
        }
        
        // Nasty hack that overcomes problems with Enterprise classes/config being present
        // on the Repository project's testing classpath (continuous integration).
        // See minimal-context.xml for ClusterService override.
        if (map.get(KEY_CLUSTER_SERVICE) instanceof ClusterService)
        {
            ClusterService clusterService = (ClusterService) map.get(KEY_CLUSTER_SERVICE);
            if (activeClusterWithMultipleMembers(clusterService))
            {
                // reindex
                indexRecoveryComponent.reindex();
            }
        }
    }

    private boolean activeClusterWithMultipleMembers(ClusterService clusterService)
    {
        return clusterService.isClusteringEnabled() && clusterService.isInitialised() &&
                    clusterService.getNumActiveClusterMembers() > 1;
    }
}