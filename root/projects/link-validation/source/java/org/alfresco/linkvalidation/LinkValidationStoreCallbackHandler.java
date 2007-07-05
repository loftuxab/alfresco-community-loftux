/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    LinkValidationStoreCallbackHandler.java
*----------------------------------------------------------------------------*/

package org.alfresco.linkvalidation;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.repo.avm.CreateVersionCallback;
import org.alfresco.repo.avm.PurgeStoreCallback;
import org.alfresco.repo.avm.PurgeVersionCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//-----------------------------------------------------------------------------
/**
*   Listens to AVM CreateVersionCallback and PurgeStoreCallback events,
*   and allows a list of stores that have been versioned.   This makes
*   it possible to fetch up-to-date getLatestSnapshotID(store) info without
*   actually polling the AVM (which is currently a heavy-weight operation).
*   Note that even if multiple snapshots are taken of a store before it
*   its links can be re-validated, only the highest version value is recorded
*   (which is exactly what link validation wants).  
*   <p>
*   The only corner-case to handle is when a version is purged, and the purged
*   version is equal to the current latest snapshot for that store.  In this
*   case, the value associated with the store's last version is set to the
*   bogus value -1, and the code walking the list is responsible for calling
*   getLatestSnapshotID() manually.   This saves repeated calls to 
*   getLatestSnapshotID() within this callback handler, and possibly allows
*   many such calls to be eliminated by deferring them  (e.g.: in a succession
*   of PurgeVersionCallback events, PurgeVersionCallback events followed by
*   a PurgeStoreCallback.  Thus, the onus of looking for -1  is on the client,
*   who is in a position to do so lazily.
*/
//-----------------------------------------------------------------------------
public class       LinkValidationStoreCallbackHandler 
       implements  CreateVersionCallback, 
                   PurgeVersionCallback, 
                   PurgeStoreCallback
{
    private static Log log = 
        LogFactory.getLog(LinkValidationStoreCallbackHandler.class);

    HashMap<String,Integer> latest_;

    public LinkValidationStoreCallbackHandler()
    {
        latest_ = new HashMap<String,Integer>();
    }

    //-------------------------------------------------------------------------
    /**
    * Callback function:  a version of a store has been created.
    * Note:  the create may be a no-op.
    *  
    * @param storeName The name of the store in which a new version has been created.
    * @param versionID The version id of the new version.
    */
    //-------------------------------------------------------------------------
    public void versionCreated(String storeName, int versionID)
    {
        synchronized( this )
        {
            if ( log.isDebugEnabled() )
                log.debug("versionCreated: " + storeName + "   " + versionID);

            latest_.put( storeName, versionID );
        }
    }

    //-------------------------------------------------------------------------
    /**
    * Callback function:  a version was purged from a store.
    * If the version being purged less than the "latest" version, we don't care;
    * otherwise, we declare we don't know what the latest version is by claiming
    * that it is -1 (which is an impossible version id).
    *
    * @param storeName The name of the store from which a version was purged.
    * @param versionID The id of the purged version.
    */
    //-------------------------------------------------------------------------
    public void versionPurged(String storeName, int versionID)
    {
        synchronized( this )
        {
            Integer  old =  latest_.get( storeName );

            if ( log.isDebugEnabled() )
                log.debug("versionPurged: " + storeName + "   " + versionID);

            if ((old == null) || ( versionID < old )) { return; }

            if ( log.isDebugEnabled() )
                log.debug("versionPurged set cache to -1: " + storeName);

            latest_.put( storeName, -1 );
        }
    }


    //-------------------------------------------------------------------------
    /**
    * Callback function: a store has been purged.
    * @param storeName The name of the purged store.
    */
    //-------------------------------------------------------------------------
    public void storePurged(String storeName)
    {
        synchronized( this )
        {
            if ( log.isDebugEnabled() )
                log.debug("storePurged: " + storeName);

            latest_.remove( storeName );
        }
    }


    //-------------------------------------------------------------------------
    /**
    *  Add a store's version info to the latest ID cache.
    *  This function is called by LinkValidationService
    *  to update the cache, but care is taken only allow
    *  transitions from null or -1;  this is necessary 
    *  to avoid a race condition where LinkValidationService
    *  notices there's no cached value, fetches the real value
    *  manually [race window] then updates the value.  During
    *  The "[race window]" , a snapshot might have been taken,
    *  and care must be taken not to destroy it.   Because
    *  the callback only uses defined ID values > 0, filtering
    *  LinkValidationService updates is sufficient.
    *  <p>
    *  Note: The caller must take the value returned by this function
    *  as the latest version of the store, not the 'id' value passed in
    *  (this is necessary to avoid a race condition). 
    */
    //-------------------------------------------------------------------------
    public synchronized Integer putLatestSnapshotID(String  storeName, 
                                                    Integer id)
    {
        Integer current =  latest_.get( storeName );

        if ( (current == null) || (current == -1) )
        {
            current = id;
            latest_.put( storeName, current );
        }
        return current;
    }

    //-------------------------------------------------------------------------
    /**
    *   Returns a individual value within the cache or null.
    *   This information may be incomplete because a snapshot 
    *   of a store might not have been taken since server startup.  
    *   Some stores in the map returned might also have a latest 
    *   version of "-1", which means it's unknown, and must be 
    *   fetched maually via the AVM API getLatestSnapshotID().
    *   The situation where this arrises is when a purge of the 
    *   latest version of a store was done.
    *   <p>
    *   To use this cache, LinkValidation first gets a list of all staging 
    *   areas manually.  For every staging area in the cache it can avoid
    *   a call to getLatestSnapshotID() when the associated value isn't -1.
    *   LinkValidation also updates the cache manually to avoid calling
    *   getLatestSnapshotID() again on inactive stores. 
    *   <p>
    *   See also:  getLatestSnapshotIdCache()
    */
    //-------------------------------------------------------------------------
    public synchronized Integer getLatestSnapshotID( String storeName )
    {
        return latest_.get(storeName);
    }

    //-------------------------------------------------------------------------
    /**
    *   Returns a copy of the latest snapshot info cache.
    *   This function just a debugging aid; typically, just use get(). 
    */
    //-------------------------------------------------------------------------
    synchronized Map<String,Integer> getLatestSnapshotIdCache()
    {
        Map<String,Integer> cache = 
            new HashMap<String,Integer>( latest_.size() );

        for ( String key : latest_.keySet() )
        {
            cache.put( key, latest_.get( key ) );
        }
        return cache;
    }
}

