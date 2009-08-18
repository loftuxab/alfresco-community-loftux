/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

/**
 * Records management permission service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSecurityServiceImpl implements RecordsManagementSecurityService
{
    /** Entry voter for capability related support */
    private RMEntryVoter voter;
    
    /** Authority service */
    private AuthorityService authorityService;
    
    /** Permission service */
    private PermissionService permissionService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Records management role zone */
    public static final String RM_ROLE_ZONE = "rmRoleZone";

    /**
     * Set the RMEntryVoter
     * 
     * @param voter
     */
    public void setVoter(RMEntryVoter voter)
    {
        this.voter = voter;       
    }
    
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getCapabilities()
     */
    public List<Capability> getCapabilities()
    {
        return new ArrayList<Capability>(voter.getAllCapabilities());
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getCapabilities(org.alfresco.service.cmr.repository.NodeRef)
     */
    public Map<Capability, AccessStatus> getCapabilities(NodeRef nodeRef)
    {
        return voter.getCapabilities(nodeRef);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getCapability(java.lang.String)
     */
    public Capability getCapability(String name)
    {
        return voter.getCapability(name);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getProtectedAspects()
     */
    public Set<QName> getProtectedAspects()
    {
        return voter.getProtetcedAscpects();
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getProtectedProperties()
     */
    public Set<QName> getProtectedProperties()
    {
       return voter.getProtectedProperties();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getRoles()
     */
    public List<String> getRoles()
    {
        return null;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#existsRole(java.lang.String)
     */
    public boolean existsRole(String role)
    {
        return false;
    }
    
    public void createRole(String role, String roleDisplayLabel, List<Capability> capabilities)
    {
        Set<String> zones = new HashSet<String>(1);
        zones.add(RM_ROLE_ZONE);
        
        // Create a group that relates to the records management role
        String roleGroup = this.authorityService.createAuthority(AuthorityType.GROUP, role, roleDisplayLabel, zones);
        
        // TODO figure out how to cope with more than one root records managment node
        // Find the file plan node reference
        ResultSet resultSet = this.searchService.query(
                                new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"), 
                                SearchService.LANGUAGE_LUCENE, 
                                "ASPECT:\"rma:recordsManagementRoot\"");
        if (resultSet.length() == 0)
        {
            throw new AlfrescoRuntimeException("No records mamangement root node found.");
        }
        if (resultSet.length() != 1)
        {
            throw new AlfrescoRuntimeException("More than one records managment root node round.  This is currently unsupported.");
        }
        NodeRef rootRMNodeRef = resultSet.getNodeRef(0);
        
        // Assign the various capabilities to the group on the root records management node
        for (Capability capability : capabilities)
        {
            this.permissionService.setPermission(rootRMNodeRef, roleGroup, capability.getName(), true);
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#updateRole(java.lang.String, java.util.List)
     */
    public void updateRole(String role, List<Capability> capabilities)
    {
             
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#deleteRole(java.lang.String)
     */
    public void deleteRole(String role)
    {
             
    }
}
