package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;
import com.activiti.repo.domain.ContainerNode;

/**
 * @author derekh
 */
public class ContainerNodeImpl extends RealNodeImpl implements ContainerNode {
    private Set childAssocs;

    public ContainerNodeImpl() {
        childAssocs = new HashSet(3, 0.75F);
    }

    public Set getChildAssocs() {
        return childAssocs;
    }

    public void setChildAssocs(Set childAssocs) {
        this.childAssocs = childAssocs;
    }
}
