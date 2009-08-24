/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;

public interface RMCaveatConfigService
{
    public void init();
    
    /**
     * Get allowed values for given caveat list (for current user)
     * @param constraintName
     * @return
     */
    public List<String> getRMAllowedValues(String constraintName);
    
    /**
     * Check whether access to 'record component' node is vetoed for current user due to caveat(s)
     * 
     * @param nodeRef
     * @return false, if caveat(s) veto access otherwise return true
     */
    public boolean hasAccess(NodeRef nodeRef);
    
    /*
     *  Get a single RM constraint
     */
    public RMConstraintInfo getRMConstraint(String listName);
    
    /*
     *  Get the names of all the caveat lists
     */
    public Set<RMConstraintInfo> getAllRMConstraints();
    
    /**
     * Get the details of a caveat list
     * @param listName
     * @return
     */
    public Map<String, List<String>> getListDetails(String listName);
    
    public NodeRef updateOrCreateCaveatConfig(File jsonFile);
     
    public NodeRef updateOrCreateCaveatConfig(String jsonString);
    
    public NodeRef updateOrCreateCaveatConfig(InputStream is);
    
    /**
     * add RM constraint list
     * @param listName the name of the RMConstraintList
     * @param listTitle 
     */
    public RMConstraintInfo addRMConstraint(String listName, String listTitle, String[] allowedValues);
    
    /**
     * add RM constraint list
     * @param listName the name of the RMConstraintList - can not be changed
     */
    public RMConstraintInfo updateRMConstraint(String listName, String listTitle, String[] allowedValues);

    
    /**
     * delete RM Constraint Name
     * 
     * @param listName the name of the RMConstraintList
     */
    public void deleteRMConstraint(String listName);
     
    /**
     * Add a single value to an authority in a list.   The existing values of the list remain.
     * 
     * @param listName the name of the RMConstraintList
     * @param authorityName
     * @param values
     * @throws AlfrescoRuntimeException if either the list or the authority do not already exist.
     */
    public void addRMConstraintListValue(String listName, String authorityName, String value);
    
    /**
     * Replace the values for an authority in a list.   
     * The existing values are removed.
     * 
     * If the authority does not already exist in the list, it will be added
     * 
     * @param listName the name of the RMConstraintList
     * @param authorityName
     * @param values
     */
    public void updateRMConstraintListAuthority(String listName, String authorityName, List<String>values);
     
    /**
     * Remove an authority from a list
     * 
     * @param listName the name of the RMConstraintList
     * @param authorityName
     * @param values
     */
    public void removeRMConstraintListAuthority(String listName, String authorityName);
 
    /**
     * Replace the values for an authority in a list.   
     * The existing values are removed.
     * 
     * If the authority does not already exist in the list, it will be added
     * 
     * @param listName the name of the RMConstraintList
     * @param value
     * @param authorities
     */
    public void updateRMConstraintListValue(String listName, String value, List<String>authorities);
     
    /**
     * Remove an authority from a list
     * 
     * @param listName the name of the RMConstraintList
     * @param authorityName
     * @param value
     */
    public void removeRMConstraintListValue(String listName, String valueName);

}
