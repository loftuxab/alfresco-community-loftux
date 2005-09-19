/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.security;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * This service encapsulates the management of people and groups.
 * <p>
 * <p>
 * People and groups may be managed entirely in the repository or entirely in
 * some other implementation such as LDAP or via NTLM. Some properties may in
 * the repository and some in another store. Individual properties may or may
 * not be mutable.
 * <p>
 * 
 * @author Andy Hind
 */
public interface PersonService
{
    /**
     * Get a person by userName. The person is store in the repository. The
     * person may be created as a side effect of this call.
     * 
     * @param userName - the userName key to find the person
     * @return
     */
    public NodeRef getPerson(String userName);

    /**
     * Does this service create people on demand if they are missing. If this is
     * true, a call to getPerson() will create a person if they are missing.
     * 
     * @return true if people are created on demand and false otherwise.
     */
    public boolean createMissingPeople();

    /**
     * Get the list of properties that are mutable. Some service may only allow
     * a limited list of properties to be changed. This may be those persisted
     * in the repository or those that can be changed in some other
     * implementation such as LDAP.
     * 
     * @return A set of QNames that identify properties that can be changed
     */
    public Set<QName> getMutableProperties();

    /**
     * Set the properties on a person - some of these may be persisted in
     * different locations.
     * 
     * @param nodeRef - the node ref for the person
     * @param properties - the map of properties to set (as the NodeService)
     */
    public void setPersonProperties(NodeRef nodeRef, Map<QName, Serializable> properties);

    /**
     * Can this service create, delete and update person information?
     * 
     * @return true if this service allows mutation to people.
     */
    public boolean isMutable();

    /**
     * Create a new person with the given properties.
     * The userName is one of the properties.
     * Users with duplicate userNames are not allowed.
     * 
     * @param properties
     * @return
     */
    public NodeRef createPerson(Map<QName, Serializable> properties);

    /**
     * Get the groups for which the person identified by user name is a member.
     * 
     * @param userName
     * @return A set of string names for the group authorities. By convention these will start with "GROUP_".
     */
    public Set<String> getGroups(String userName);

    /**
     * Delete the person identified by the given user name.
     * 
     * @param userName
     */
    public void deletePerson(String userName);
    
    /**
     * Get all the people we know about.
     * 
     * @return a set of people in no specific order. 
     */
    public Set<NodeRef> getAllPeople();

    /**
     * Get all the groups we know about.
     * 
     * @return return a set of group names
     */
    public Set<String> getAllGroups();
    
    /**
     * Add a person to a group.
     * 
     * @param groupName
     * @param userName
     */
    public void addPersonToGroup(String groupName, String userName);
    
    /**
     * Delete a person from a group.
     * 
     * @param groupName
     * @param userName
     */
    public void deletePersonFromGroup(String groupName, String userName);
    
    /**
     * Add a subgroup to a group.
     * 
     * @param groupName
     * @param subGroupName
     */
    public void addSubGroupToGroup(String groupName, String subGroupName);
    
    /**
     * Delete a subgroup from a group.
     * The subgroup will still exist as a group.
     * 
     * @param groupName
     * @param subGroupName
     */
    public void deleteSubGroupFromGroup(String groupName, String subGroupName);
    
    /**
     * Delete a group.
     * 
     * @param groupName
     */
    public void deleteGroup(String groupName);
}
