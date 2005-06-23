/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.version;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.alfresco.service.cmr.repository.AspectMissingException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

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
     * @param  nodeRef              a node reference
     * @param  versionProperties    the version properties that are stored with the newly created
     *                              version
     * @return                      the created version object
     * @throws ReservedVersionNameException
     *                              thrown if a reserved property name is used int he version properties 
     *                              provided
     * @thrown AspectMissingException
     *                              thrown if the version aspect is missing                              
     */
    public Version createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException, AspectMissingException;

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
     * @param nodeRef               a node reference
     * @param versionProperties     the version properties that are stored with the newly created
     *                              version
     * @param versionChildren       if true then the children of the referenced node are also
     *                              versioned, false otherwise
     * @return                      the created version object(s)
     * @throws ReservedVersionNameException
     *                              thrown if a reserved property name is used int he version properties 
     *                              provided
     * @thrown AspectMissingException
     *                              thrown if the version aspect is missing
     */
    public Collection<Version> createVersion(
            NodeRef nodeRef, 
            Map<String, Serializable> versionProperties,
            boolean versionChildren)
            throws ReservedVersionNameException, AspectMissingException;

    /**
     * Creates new versions based on the list of node references provided.
     * 
     * @param nodeRefs              a list of node references
     * @param versionProperties     version property values
     * @return                      a collection of newly created versions
     * @throws ReservedVersionNameException
     *                              thrown if a reserved property name is used int he version properties 
     *                              provided
     * @thrown AspectMissingException
     *                              thrown if the version aspect is missing
     */
    public Collection<Version> createVersion(
            Collection<NodeRef> nodeRefs, 
            Map<String, Serializable> versionProperties)
            throws ReservedVersionNameException, AspectMissingException;

    /**
     * Gets the version history information for a node.
     * <p>
     * If the node has not been versioned then null is returned.
     * <p>
     * If the node referenced does not or can not have the version aspect
     * applied to it then an exception will be raised.
     * 
     * @param  nodeRef  a node reference
     * @return          the version history information
     * @thrown AspectMissingException
     *                  thrown if the version aspect is missing
     */
    public VersionHistory getVersionHistory(NodeRef nodeRef)
        throws AspectMissingException;     
	
	/**
	 * Gets the version object for the current version of the node reference
	 * passed.
	 * <p>
	 * Returns null if the node is not versionable or has not been versioned.
	 * @param nodeRef   the node reference
	 * @return			the version object for the current version
	 */
	public Version getCurrentVersion(NodeRef nodeRef);
}
