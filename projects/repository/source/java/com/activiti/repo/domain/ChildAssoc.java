package com.activiti.repo.domain;

/**
 * Represents a special type of association between nodes, that of the
 * parent-child relationship.
 * 
 * @author Derek Hulley
 */
public interface ChildAssoc
{
    public long getId();

    public void setId(long id);

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
    public void buildAssociation(ContainerNode parentNode, Node childNode);

    public ContainerNode getParent();

    public void setParent(ContainerNode node);

    public Node getChild();

    public void setChild(Node node);

    public String getName();

    public void setName(String name);

    public boolean getIsPrimary();

    public void setIsPrimary(boolean isPrimary);
}
