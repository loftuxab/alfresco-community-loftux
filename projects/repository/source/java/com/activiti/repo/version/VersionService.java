/*
 * Created on Mar 29, 2005
 *
 * TODO put licence header here
 */
package com.activiti.repo.version;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.activiti.repo.ref.NodeRef;

/**
 * Interface for public and internal version operations.
 * 
 * @author Roy Wetherall
 */
public interface VersionService
{
    /**
     * Creates a new version based on the referenced node.
     * 
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * 
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * 
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef
     *            a node reference
     * @param versionProperties
     *            the version properties that are stored with the newly created
     *            version
     * @return TODO
     */
    public Version createVersion(NodeRef nodeRef, Map<String, String> versionProperties);

    /**
     * Creates a new version based on the referenced node.
     * 
     * If the node has not previously been versioned then a version history and
     * initial version will be created.
     * 
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * 
     * The version properties are sotred as version meta-data against the newly
     * created version.
     * 
     * @param nodeRef
     *            a node reference
     * @param versionProperties
     *            the version properties that are stored with the newly created
     *            version
     * @param versionChildren
     *            if true then the children of the referenced node are also
     *            versioned, false otherwise
     * @return TODO
     */
    public Collection<Version> createVersion(NodeRef nodeRef, Map<String, String> versionProperties,
            boolean versionChildren);

    /**
     * 
     * @param nodeRefs
     *            a list of node references
     * @param versionProperties
     *            the version properties that are stored with the newly created
     *            versions
     * @return TODO
     */
    public Collection<Version> createVersion(List<NodeRef> nodeRefs,
            Map<String, String> versionProperties);

    /**
     * Gets the version history information for a node.
     * 
     * If the node has not been versioned then null is returned.
     * 
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * 
     * @param nodeRef
     *            a node reference
     * @return the version history information
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef);

    //TODO getState
    //TODO restore ... 
}
