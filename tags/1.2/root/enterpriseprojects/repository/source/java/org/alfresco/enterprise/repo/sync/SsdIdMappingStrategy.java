/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

/**
 * This interface defines the mapping between an On Premise {@link SyncModel#PROP_SYNC_GUID SSD ID} and the Cloud SSD ID.
 * In the production system, these IDs will be identical, but for development test purposes we will adopt a simple non-identical mapping.
 * 
 * @author Neil Mc Erlean
 * @since TODO
 */
public interface SsdIdMappingStrategy
{
    /** Given an On Premise SSD ID, returns the Cloud equivalent. */
    String getCloudGUID(String onPremiseGuid);
    /** Given an Cloud SSD ID, returns the On Premise equivalent. */
    String getOnPremiseGUID(String cloudGuid);
    
    /**
     * The production strategy for SsdId Mapping.
     */
    public static class IdentitySsdIdMapping implements SsdIdMappingStrategy
    {
        @Override public String getCloudGUID(String onPremiseGuid) { return onPremiseGuid; }
        
        @Override public String getOnPremiseGUID(String cloudGuid) { return cloudGuid; }
    }
}
