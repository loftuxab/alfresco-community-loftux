package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;

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

    public void setChildAssocs(Set<ChildAssoc> childAssocs)
    {
        this.childAssocs = childAssocs;
    }
}
