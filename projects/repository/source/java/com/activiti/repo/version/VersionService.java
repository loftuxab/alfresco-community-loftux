/*
 * Created on Mar 29, 2005
 *
 * TODO put licence header here
 */
package com.activiti.repo.version;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;

/**
 * Interface for public and internal version operations.
 * 
 * @author Roy Wetherall
 */
public interface VersionService
{
    /**
     * The version store protocol label, used in store references
     */
    public static final String VERSION_STORE_PROTOCOL = "versionStore";
    
    /**
     * Version namespace
     */
    public static final String VERSION_NAMESPACE =  "http://www.activiti.com/version/1.0";
    
    /**
     * Aspect name
     */
    public static final QName ASPECT_VERSION = QName.createQName(VERSION_NAMESPACE, "version");
    
    /**
     * The current version label attribute name
     */
    public static final QName ATTR_CURRENT_VERSION_LABEL = QName.createQName(VERSION_NAMESPACE, "currentVersionLabel");
    
    /**
     * Gets the reference to the version store
     * 
     * @return  reference to the version store
     */
    public StoreRef getVersionStoreReference();
    
    /**
     * Creates a new version based on the referenced node.
     * <p>
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * <p>
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef            a node reference
     * @param versionProperties  the version properties that are stored with the newly created
     *                           version
     * @return                   the created version object
     * @throws                   ReservedVersionNameException 
     */
    public Version createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException;

    /**
     * Creates a new version based on the referenced node.
     * <p>
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * <p>
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef            a node reference
     * @param versionProperties  the version properties that are stored with the newly created
     *                           version
     * @param versionChildren    if true then the children of the referenced node are also
     *                           versioned, false otherwise
     * @return                   the created version object(s)
     * @throws                   ReservedVersionNameException 
     */
    public Collection<Version> createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties,
            boolean versionChildren)
            throws ReservedVersionNameException;

    /**
     * Creates new versions based on the list of node references provided.
     * 
     * @param nodeRefs           a list of node references
     * @param versionProperties  version property values
     * @return                   a collection of newly created versions
     * @throws                   ReservedVersionNameException 
     */
    public Collection<Version> createVersion(
            Collection<NodeRef> nodeRefs, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException;

    /**
     * Gets the version history information for a node.
     * <p>
     * If the node has not been versioned then null is returned.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * 
     * @param nodeRef  a node reference
     * @return         the version history information
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef);       

    // TODO figure out what is still missing, eg restore, getState ...
}
