/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.io.Serializable;

import org.alfresco.repo.node.index.AbstractReindexComponent.InIndex;
import org.alfresco.service.cmr.repository.NodeRef.Status;

/**
 * Index node info
 *
 * @author janv
 */
public class IndexNodeInfo implements Serializable
{
    private static final long serialVersionUID = 7854749986083635678L;
    
    private Status nodeStatus;
    private InIndex inIndex;

    /* package */ IndexNodeInfo(Status nodeStatus, InIndex inIndex)
    {
        this.nodeStatus = nodeStatus;
        this.inIndex = inIndex;
    }
    
    public Status getNodeStatus()
    {
        return this.nodeStatus;
    }
    
    public InIndex getInIndex()
    {
        return this.inIndex;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        
        sb.append("IndexNodeInfo[")
          .append("nodeRef=").append(nodeStatus)
          .append(", inIndex=").append(inIndex)
          .append("]");
        return sb.toString();
    }
}
