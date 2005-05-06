package org.alfresco.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.domain.NodeAssoc;
import org.alfresco.repo.domain.RealNode;

/**
 * @author Derek Hulley
 */
public class RealNodeImpl extends NodeImpl implements RealNode
{
    private Set<NodeAssoc> targetNodeAssocs;

    public RealNodeImpl()
    {
        targetNodeAssocs = new HashSet<NodeAssoc>(3);
    }

    public Set<NodeAssoc> getTargetNodeAssocs()
    {
        return targetNodeAssocs;
    }
    
    /**
     * @return Returns all the regular associations for which this node is a source 
     */
    private void setTargetNodeAssocs(Set<NodeAssoc> targetNodeAssocs)
    {
        this.targetNodeAssocs = targetNodeAssocs;
    }
}
