/*
 * Created on Mar 29, 2005
 * 
 * TODO put licence header here
 *
 */
package com.activiti.repo.version;

import java.io.Serializable;
import java.util.Date;

import com.activiti.repo.ref.NodeRef;


/**
 * Version interface.
 * 
 * Allows access to version property values and frozen state node references.
 * The version history tree can also be navigated.
 * 
 * @author Roy Wetherall
 */
public interface Version extends Serializable
{
    /**
     * Helper method to get the created date from the version property data.
     * 
     * @return the date the version was created
     */
    public Date getCreatedDate();

    /**
     * Helper method to get the version label from the version property data.
     * 
     * @return the version label
     */
    public String getVersionLabel();

    /**
     * Gets the value of a named version property.
     * 
     * @param name the name of the property
     * @return the value of the property
     * 
     */
    public String getVersionProperty(String name);

    /**
     * Gets the reference to the node that contains the frozen state of the
     * version.
     * 
     * @return a node reference
     */
    public NodeRef getNodeRef();
}
