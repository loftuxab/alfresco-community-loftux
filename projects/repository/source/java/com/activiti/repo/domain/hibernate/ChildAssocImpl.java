package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.QName;

/**
 * @author Derek Hulley
 */
public class ChildAssocImpl implements ChildAssoc
{
    private Long id;
    private ContainerNode parent;
    private Node child;
    private String namespaceUri;
    private String name;
    private boolean isPrimary;
    private ChildAssocRef childAssocRef;

    public void buildAssociation(ContainerNode parentNode, Node childNode)
    {
        // add the forward associations
        this.setParent(parentNode);
        this.setChild(childNode);
        // add the inverse associations
        parentNode.getChildAssocs().add(this);
        childNode.getParentAssocs().add(this);
    }
    
    public void removeAssociation()
    {
        // maintain inverse assoc from parent node to this instance
        this.getParent().getChildAssocs().remove(this);
        // maintain inverse assoc from child node to this instance
        this.getChild().getParentAssocs().remove(this);
    }
    
    public synchronized ChildAssocRef getChildAssocRef()
    {
        if (childAssocRef == null)
        {
            childAssocRef = new ChildAssocRef(getParent().getNodeRef(),
                    QName.createQName(getNamespaceUri(), getName()),
                    getChild().getNodeRef());
        }
        return childAssocRef;
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

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
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
