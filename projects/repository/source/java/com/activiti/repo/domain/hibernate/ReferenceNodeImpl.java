package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ReferenceNode;

/**
 * @author Derek Hulley
 */
public class ReferenceNodeImpl extends NodeImpl implements ReferenceNode
{
    private String referencedPath;

    public String getReferencedPath()
    {
        return referencedPath;
    }

    public void setReferencedPath(String path)
    {
        referencedPath = path;
    }
}
