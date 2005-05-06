package org.alfresco.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.repo.domain.ContainerNode;

/**
 * @author Derek Hulley
 */
public class ContainerNodeImpl extends RealNodeImpl implements ContainerNode
{
    private Set<ChildAssoc> childAssocs;

    public ContainerNodeImpl()
    {
        childAssocs = new HashSet<ChildAssoc>(3, 0.75F);
    }

    public Set<ChildAssoc> getChildAssocs()
    {
        return childAssocs;
    }

    /**
     * For Hibernate use
     */
    private void setChildAssocs(Set<ChildAssoc> childAssocs)
    {
        this.childAssocs = childAssocs;
    }
}
