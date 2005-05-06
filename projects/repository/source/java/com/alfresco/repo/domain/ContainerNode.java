package org.alfresco.repo.domain;

import java.util.Set;

/**
 * @author Derek Hulley
 */
public interface ContainerNode extends RealNode
{
    public Set<ChildAssoc> getChildAssocs();
}
