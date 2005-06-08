package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.repo.domain.ContainerNode;
import org.alfresco.repo.domain.Node;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.QName;

/**
 * @author Derek Hulley
 */
public class ChildAssocImpl implements ChildAssoc
{
    private Long id;
    private ContainerNode parent;
    private Node child;
    private String typeNamespaceUri;
    private String typeLocalName;
    private String namespaceUri;
    private String localName;
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
            childAssocRef = new ChildAssocRef(
                    getTypeQName(),
                    getParent().getNodeRef(),
                    getQName(),
                    getChild().getNodeRef(),
                    this.isPrimary,
                    -1);
        }
        return childAssocRef;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(32);
        sb.append("ChildAssoc")
          .append("[ parent=").append(parent)
          .append(", child=").append(child)
          .append(", name=").append(getQName())
          .append(", isPrimary=").append(isPrimary)
          .append("]");
        return sb.toString();
    }

    public Long getId()
    {
        return id;
    }

    /**
     * For Hibernate use
     */
    private void setId(Long id)
    {
        this.id = id;
    }

    public ContainerNode getParent()
    {
        return parent;
    }

    /**
     * For Hibernate use
     */
    private void setParent(ContainerNode parentNode)
    {
        this.parent = parentNode;
    }

    public Node getChild()
    {
        return child;
    }

    /**
     * For Hibernate use
     */
    private void setChild(Node node)
    {
        child = node;
    }
    
    /**
     * @see #getTypeNamespaceUri()
     * @see #getTypeLocalName()
     */
    public QName getTypeQName()
    {
        return QName.createQName(getTypeNamespaceUri(), getTypeLocalName());
    }
    
    /**
     * @see #setTypeNamespaceUri(String)
     * @see #setTypeLocalName(String)
     */
    public void setTypeQName(QName qname)
    {
        setTypeNamespaceUri(qname.getNamespaceURI());
        setTypeLocalName(qname.getLocalName());
    }
    
    /**
     * For Hibernate use only
     */
    private String getTypeNamespaceUri()
    {
        return typeNamespaceUri;
    }

    /**
     * For Hibernate use only
     */
    private void setTypeNamespaceUri(String typeNamespaceUri)
    {
        this.typeNamespaceUri = typeNamespaceUri;
    }

    /**
     * For Hibernate use only
     */
    private String getTypeLocalName()
    {
        return typeLocalName;
    }

    /**
     * For Hibernate use only
     */
    private void setTypeLocalName(String name)
    {
        this.typeLocalName = name;
    }

    /**
     * @see #getNamespaceUri()
     * @see #getLocalName()
     */
    public QName getQName()
    {
        return QName.createQName(getNamespaceUri(), getLocalName());
    }

    /**
     * @see #setNamespaceUri(String)
     * @see #setLocalName(String)
     */
    public void setQName(QName qname)
    {
        setNamespaceUri(qname.getNamespaceURI());
        setLocalName(qname.getLocalName());
    }

    /**
     * For Hibernate use only
     */
    private String getNamespaceUri()
    {
        return namespaceUri;
    }

    /**
     * For Hibernate use only
     */
    private void setNamespaceUri(String namespaceUri)
    {
        this.namespaceUri = namespaceUri;
    }

    /**
     * For Hibernate use only
     */
    private String getLocalName()
    {
        return localName;
    }

    /**
     * For Hibernate use only
     */
    private void setLocalName(String name)
    {
        this.localName = name;
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
