package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.RealNode;

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
    private String name;

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

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public RealNode getSource()
    {
        return source;
    }

    public void setSource(RealNode source)
    {
        this.source = source;
    }

    public Node getTarget()
    {
        return target;
    }

    public void setTarget(Node target)
    {
        this.target = target;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
