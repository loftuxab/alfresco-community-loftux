package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;

/**
 * @author Derek Hulley
 */
public class ChildAssocImpl implements ChildAssoc
{
    private long id;
    private ContainerNode parent;
    private Node child;
    private String namespaceUri;
    private String name;
    private boolean isPrimary;

    public void buildAssociation(ContainerNode parentNode, Node childNode)
    {
        // add the forward associations
        this.setParent(parentNode);
        this.setChild(childNode);
        // add the inverse associations
        parentNode.getChildAssocs().add(this);
        childNode.getParentAssocs().add(this);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer(32);
        sb.append("ChildAssoc")
          .append("[ parent=").append(parent)
          .append(", child=").append(child)
          .append(", name=").append(name)
          .append(", isPrimary=").append(isPrimary)
          .append("]");
        return sb.toString();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public ContainerNode getParent()
    {
        return parent;
    }

    public void setParent(ContainerNode parentNode)
    {
        this.parent = parentNode;
    }

    public Node getChild()
    {
        return child;
    }

    public void setChild(Node node)
    {
        child = node;
    }

    public String getNamespaceUri()
    {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri)
    {
        this.namespaceUri = namespaceUri;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean getIsPrimary()
    {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary)
    {
        this.isPrimary = isPrimary;
    }
}
