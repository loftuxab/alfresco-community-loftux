/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.util.Collections;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Neil Mc Erlean
 * @since TODO
 */
public class SyncSetCreationConflictException extends SyncAdminServiceException
{
    private static final long serialVersionUID = 1L;
    
    private final List<NodeRef> illegalNodes;
    private final boolean illegalNodesListIsComplete;
    
    public SyncSetCreationConflictException(String message, List<NodeRef> illegalNodes)
    {
        this(message, illegalNodes, true);
    }
    
    public SyncSetCreationConflictException(String message, List<NodeRef> illegalNodes, boolean illegalNodesListIsComplete)
    {
        super(message);
        this.illegalNodes = Collections.unmodifiableList(illegalNodes);
        this.illegalNodesListIsComplete = illegalNodesListIsComplete;
    }
    
    /**
     * Gets the List of illegal nodes.
     */
    public List<NodeRef> getIllegalNodes()
    {
        return Collections.unmodifiableList(illegalNodes);
    }
    
    /**
     * Returns <code>true</code> if the {@link #getIllegalNodes()} list is a complete list
     * and <code>false</code> if there are more illegal nodes which were unreported.
     * (This could happen, for example, if there was a large number of illegal nodes and
     * the code stopped checking beyond a limit.)
     * @return whether the {@link #getIllegalNodes()} list is complete or not.
     */
    public boolean isIllegalNodesListComplete()
    {
        return illegalNodesListIsComplete;
    }
}
