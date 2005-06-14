package org.alfresco.repo.domain;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;

/**
 * Represents a special type of association between nodes, that of the
 * parent-child relationship.
 * 
 * @author Derek Hulley
 */
public interface ChildAssoc
{
    /**
     * Performs the necessary work on the provided nodes to ensure that a bidirectional
     * association is properly set up.
     * <p>
     * The association attributes still have to be set up.
     * 
     * @param parentNode
     * @param childNode
     * 
     * @see #setName(String)
     * @see #setIsPrimary(boolean)
     */
    public void buildAssociation(Node parentNode, Node childNode);
    
    /**
     * Performs the necessary work on the {@link #getParent() parent} and
     * {@link #getChild() child} nodes to maintain the inverse association sets
     */
    public void removeAssociation();
    
    public ChildAssociationRef getChildAssocRef();

    public Long getId();

    public Node getParent();

    public Node getChild();
    
    /**
     * @return Returns the qualified name of the association type
     */
    public QName getTypeQName();
    
    /**
     * @param assocTypeQName the qualified name of the association type as defined
     *      in the data dictionary
     */
    public void setTypeQName(QName assocTypeQName);

    /**
     * @return Returns the qualified name of this association 
     */
    public QName getQName();

    /**
     * @param qname the qualified name of the association
     */
    public void setQName(QName qname);

    public boolean getIsPrimary();

    public void setIsPrimary(boolean isPrimary);
}
