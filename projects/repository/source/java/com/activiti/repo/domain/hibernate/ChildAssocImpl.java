package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;

/**
 * @author derekh
 */
public class ChildAssocImpl implements ChildAssoc {
    private long id;

    private ContainerNode parentNode;

    private Node child;

    private String name;

    private boolean isPrimary;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void buildAssociation(ContainerNode parentNode, Node childNode) {
        // add the forward associations
        this.setParent(parentNode);
        this.setChild(childNode);
        // add the inverse associations
        parentNode.getChildAssocs().add(this);
        childNode.getParentAssocs().add(this);
    }

    public ContainerNode getParent() {
        return parentNode;
    }

    public void setParent(ContainerNode parentNode) {
        this.parentNode = parentNode;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node node) {
        child = node;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
