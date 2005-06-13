package org.alfresco.repo.domain;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.namespace.QName;

/**
 * Represents a generic association between two nodes.  The association is named
 * and bidirectional by default.
 * 
 * @author Derek Hulley
 */
public interface NodeAssoc
{
    public long getId();

    /**
     * Wires up the necessary bits on the source and target nodes so that the association
     * is immediately bidirectional.
     * <p>
     * The association attributes still have to be set.
     * 
     * @param sourceNode
     * @param targetNode
     * 
     * @see #setName(String)
     */
    public void buildAssociation(RealNode sourceNode, Node targetNode);

    /**
     * Performs the necessary work on the {@link #getSource()() source} and
     * {@link #getTarget()() target} nodes to maintain the inverse association sets
     */
    public void removeAssociation();

    public AssociationRef getNodeAssocRef();
    
    public RealNode getSource();

    public Node getTarget();

    /**
     * @return Returns the qualified name of this association type 
     */
    public QName getTypeQName();

    /**
     * @param qname the qualified name of the association type
     */
    public void setTypeQName(QName qname);
}
