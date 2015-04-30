/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

/**
 * Implementing classes can react to members joining or leaving the cluster.
 * 
 * @author Matt Ward
 */
public interface ClusterMembershipListener
{
    void memberJoined(String member, String[] cluster);
    void memberLeft(String member, String[] cluster);
}
