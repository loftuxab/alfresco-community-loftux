package com.activiti.repo.domain;

import java.util.Set;

/**
 * @author Derek Hulley
 */
public interface RealNode extends Node
{
    public Set<NodeAssoc> getTargetNodeAssocs();

    /**
     * @return Returns all the regular associations for which this node is a source 
     */
    public void setTargetNodeAssocs(Set<NodeAssoc> nodeAssocs);
}
