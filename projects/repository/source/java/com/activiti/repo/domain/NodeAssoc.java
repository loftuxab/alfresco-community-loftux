package com.activiti.repo.domain;

/**
 * Represents a generic association between two nodes.  The association is named
 * and bidirectional by default.
 * 
 * @author Derek Hulley
 */
public interface NodeAssoc
{
    public long getId();

    public void setId(long id);

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

    public RealNode getSource();

    public void setSource(RealNode node);

    public Node getTarget();

    public void setTarget(Node node);

    public String getName();

    public void setName(String name);
}
