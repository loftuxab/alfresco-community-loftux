package org.alfresco.enterprise.repo.sync;

import java.util.Date;

/** 
 * When a sync conflict has ocurred, how was it processed 
 * by the system?
 *
 */
public interface ConflictResponse
{
    /**
     * Has the conflict been resolved by the system?
     */
    public Resolution getResolution();
    
    /**
     * Has the conflict been resolved,
     */
    public enum Resolution
    {
        /**
         * The conflict was automatically resolved.  The content 
         * from the remote system has replaced the local content. 
         */
        RESOLVED_WON,
        
        /**
         * The conflict was automatically resolved.  The local content
         * has not been updated.  The remote content becomes the conflict.
         */
        RESOLVED_LOST,
        
        /**
         * The conflict has not been resolved.  There is no
         * automatic solution. 
         */
        NOT_RESOLVED
    };
    
    /**
     * date/time of conflict
     */
    public Date getTimeOfConflict();
    
    /**
     * Version label of failed sync.   This is the remote version label.
     */
    public String getVersionLabelOfConflict();
    
    /**
     * time of last successful sync.   Null if not available.
     */
    public Date getTimeOfLastSuccessfulSync();  
    
    /**
     * Version label of the last sync. Null if not available.
     */
    public String getVersionLabelOfLastSuccessfulSync();

}
