package com.activiti.repo.domain;

/**
 * @author derekh
 */
public interface ReferenceNode extends Node
{
    /**
     * 
     * @return Returns the path being referenced
     */
    public String getReferencedPath();

    public void setReferencedPath(String path);
}
