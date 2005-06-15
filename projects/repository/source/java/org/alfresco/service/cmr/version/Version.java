/*
 * Created on Mar 29, 2005
 * 
 * TODO put licence header here
 *
 */
package org.alfresco.service.cmr.version;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;


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
     * Names of the system version properties
     */
    public static final String PROP_DESCRIPTION = "description";
    
    /**
     * Helper method to get the created date from the version property data.
     * 
     * @return  the date the version was created
     */
    public Date getCreatedDate();

    /**
     * Helper method to get the version label from the version property data.
     * 
     * @return  the version label
     */
    public String getVersionLabel();
    
    /**
     * Helper method to get the version type.
     * 
     * @return  the value of the version type as an enum value
     */
    public VersionType getVersionType();
    
    /**
     * Helper method to get the version description.
     * 
     * @return the version description
     */
    public String getDescription();

    /**
     * Get the map containing the version property values
     * 
     * @return  the map containing the version properties
     */
    public Map<String, Serializable> getVersionProperties();
    
    /**
     * Gets the value of a named version property.
     * 
     * @param name  the name of the property
     * @return      the value of the property
     * 
     */
    public Serializable getVersionProperty(String name);

    /**
     * Gets the reference to the node that contains the frozen state of the
     * version.
     * 
     * @return  a node reference
     */
    public NodeRef getNodeRef();
}
