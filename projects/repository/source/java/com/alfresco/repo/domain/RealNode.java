package org.alfresco.repo.domain;

import java.util.Set;

/**
 * @author Derek Hulley
 */
public interface RealNode extends Node
{
    /**
     * @return Returns all the regular associations for which this node is a source 
     */
    public Set<NodeAssoc> getTargetNodeAssocs();
}
