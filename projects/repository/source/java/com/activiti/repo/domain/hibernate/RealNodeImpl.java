package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;

import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.RealNode;

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
