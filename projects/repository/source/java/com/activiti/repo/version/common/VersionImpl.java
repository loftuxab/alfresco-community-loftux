/*
 * Created on Mar 29, 2005
 *
 */
package com.activiti.repo.version.common;

import java.util.Date;
import java.util.Map;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.version.Version;
import com.activiti.repo.version.VersionServiceException;

/**
 * Version class implementation.
 * 
 * Used to represent the data about a version stored in a version store.
 * 
 * @author Roy Wetherall
 */
public class VersionImpl implements Version
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3257567304324888881L;
    
    /**
     * Error message(s)
     */
    private static final String ERR_NO_NODE_REF = "A valid node reference must be supplied when creating a verison.";       
    
    /**
     * The properties of the version
     */
    private Map<String, String> versionProperties = null;
    
    /**
     * The node reference that represents the frozen state of the versioned object
     */
    private NodeRef nodeRef = null;    
    
    /**
     * The version label
     */
    private String versionLabel = null;
    
    /**
     * The created date of the version
     */
    private Date createdDate = null;
    
    /**
     * Constructor that initialises the state of the version object.
     * 
     * @param versionLabel
     * @param createddate
     * @param versionProperties
     * @param nodeRef
     */
    public VersionImpl(
            String versionLabel, 
            Date createdDate, 
            Map<String, String> versionProperties, 
            NodeRef nodeRef)
    {
        if (nodeRef == null)
        {
            // Exception - a node ref must be specified
            throw new VersionServiceException(VersionImpl.ERR_NO_NODE_REF);
        }
        
        // TODO A version label and createdDate must also be set
        
        this.versionLabel = versionLabel;
        this.createdDate = createdDate;
        this.versionProperties = versionProperties;
        this.nodeRef = nodeRef;        
    }
    

    /**
     * Helper method to get the created date from the version property data.
     * 
     * @return the date the version was created
     */
    public Date getCreatedDate()
    {
        return this.createdDate;
    }

    /**
     * Helper method to get the version label from the version property data.
     * 
     * @return the version label
     */
    public String getVersionLabel()
    {
        return this.versionLabel;
    }
    
    /**
     * Get the map containing the version property values
     * 
     * @return  the map containing the version properties
     */
    public Map<String, String> getVersionProperties()
    {
        return this.versionProperties;
    }

    /**
     * Gets the value of a named version property.
     * 
     * @param name the name of the property
     * @return the value of the property, null if the property is undefined.
     * 
     */
    public String getVersionProperty(String name)
    {
        String result = null;
        if (this.versionProperties != null)
        {
            result = this.versionProperties.get(name);
        }
        return result;
    }

    /**
     * Gets the reference to the node that contains the frozen state of the
     * version.
     * 
     * @return a node reference
     */
    public NodeRef getNodeRef()
    {
        return this.nodeRef;
    }
 }
