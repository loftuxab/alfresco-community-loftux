package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.activiti.repo.domain.Node;

/**
 * Simple named node to test out various features
 * 
 * @author derekh
 * 
 */
public class NodeImpl implements Node
{
    private Long id;

    private Set parentAssocs;

    private Map properties;

    public NodeImpl()
    {
        parentAssocs = new HashSet(3, 0.75F);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Set getParentAssocs()
    {
        return parentAssocs;
    }

    public void setParentAssocs(Set parentAssocs)
    {
        this.parentAssocs = parentAssocs;
    }

    public Map getProperties()
    {
        return properties;
    }

    public void setProperties(Map properties)
    {
        this.properties = properties;
    }
}
