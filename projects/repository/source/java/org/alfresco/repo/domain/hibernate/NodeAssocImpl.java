package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.Node;
import org.alfresco.repo.domain.NodeAssoc;
import org.alfresco.repo.domain.RealNode;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.QName;

/**
 * Hibernate-specific implementation of the generic node association
 * 
 * @author Derek Hulley
 */
public class NodeAssocImpl implements NodeAssoc
{
    private long id;
    private RealNode source;
    private Node target;
    private String namespaceUri;
    private String localName;
    private NodeAssocRef nodeAssocRef;

    public NodeAssocImpl()
    {
    }

    public void buildAssociation(RealNode sourceNode, Node targetNode)
    {
        // add the forward associations
        this.setTarget(targetNode);
        this.setSource(sourceNode);
        // add the inverse associations
        sourceNode.getTargetNodeAssocs().add(this);
        targetNode.getSourceNodeAssocs().add(this);
    }
    
    public void removeAssociation()
    {
        // maintain inverse assoc from source node to this instance
        this.getSource().getTargetNodeAssocs().remove(this);
        // maintain inverse assoc from target node to this instance
        this.getTarget().getSourceNodeAssocs().remove(this);
    }
    
    public synchronized NodeAssocRef getNodeAssocRef()
    {
        if (nodeAssocRef == null)
        {
            nodeAssocRef = new NodeAssocRef(getSource().getNodeRef(),
                    QName.createQName(getNamespaceUri(), getLocalName()),
                    getTarget().getNodeRef());
        }
        return nodeAssocRef;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(32);
        sb.append("NodeAssoc")
          .append("[ source=").append(source)
          .append(", target=").append(target)
          .append(", name=").append(getQName())
          .append("]");
        return sb.toString();
    }

    public long getId()
    {
        return id;
    }

    /**
     * For Hibernate use
     */
    private void setId(long id)
    {
        this.id = id;
    }

    public RealNode getSource()
    {
        return source;
    }

    /**
     * For internal use
     */
    private void setSource(RealNode source)
    {
        this.source = source;
    }

    public Node getTarget()
    {
        return target;
    }

    /**
     * For internal use
     */
    private void setTarget(Node target)
    {
        this.target = target;
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
}
