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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
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
    
    /** Records management role zone */
    public static final String RM_ROLE_ZONE_PREFIX = "rmRoleZone";
    
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
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getCapabilities()
     */
    public Set<Capability> getCapabilities()
    {
        return new HashSet<Capability>(voter.getAllCapabilities());
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
    public Set<Role> getRoles(final NodeRef rmRootNode)
    {  
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Set<Role>>()
        {
            public Set<Role> doWork() throws Exception
            {
                Set<Role> result = new HashSet<Role>(13);
                
                Set<String> roleAuthorities = authorityService.getAllAuthoritiesInZone(getZoneName(rmRootNode), AuthorityType.GROUP);        
                for (String roleAuthority : roleAuthorities)
                {
                    String name = getShortRoleName(authorityService.getShortName(roleAuthority), rmRootNode);
                    String displayLabel = authorityService.getAuthorityDisplayName(roleAuthority);
                    Set<String> capabilities = getCapabilities(rmRootNode, roleAuthority);
                    
                    Role role = new Role(name, displayLabel, capabilities);
                    result.add(role);            
                }
                
                return result;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * 
     * @param rmRootNode
     * @return
     */
    private String getZoneName(NodeRef rmRootNode)
    {
        return RM_ROLE_ZONE_PREFIX + rmRootNode.getId();
    }
    
    private String getFullRoleName(String role, NodeRef rmRootNode)
    {
        return role + rmRootNode.getId();
    }
    
    private String getShortRoleName(String fullRoleName, NodeRef rmRootNode)
    {
        return fullRoleName.replaceAll(rmRootNode.getId(), "");
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getRole(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public Role getRole(final NodeRef rmRootNode, final String role)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Role>()
        {
            public Role doWork() throws Exception
            {                
                String roleAuthority = authorityService.getName(AuthorityType.GROUP, getFullRoleName(role, rmRootNode));
                
                String name = getShortRoleName(authorityService.getShortName(roleAuthority), rmRootNode);
                String displayLabel = authorityService.getAuthorityDisplayName(roleAuthority);                
                Set<String> capabilities = getCapabilities(rmRootNode, roleAuthority);
                
                return new Role(name, displayLabel, capabilities);
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * Gets the capabilities of a role on a node
     * 
     * @param rmRootNode
     * @param roleAuthority
     * @return
     */
    private Set<String> getCapabilities(NodeRef rmRootNode, String roleAuthority)
    {
        Set<AccessPermission> permissions = permissionService.getAllSetPermissions(rmRootNode);
        Set<String> capabilities = new HashSet<String>(52);
        for (AccessPermission permission : permissions)
        {
            if (permission.getAuthority().equals(roleAuthority) == true)
            {
                capabilities.add(permission.getPermission());
            }
        }
        return capabilities;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#existsRole(java.lang.String)
     */
    public boolean existsRole(final NodeRef rmRootNode, final String role)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Boolean>()
        {
            public Boolean doWork() throws Exception
            {                                
                String fullRoleName = authorityService.getName(AuthorityType.GROUP, getFullRoleName(role, rmRootNode));
            
                String zone = getZoneName(rmRootNode);
                Set<String> roles = authorityService.getAllAuthoritiesInZone(zone, AuthorityType.GROUP);
                return new Boolean(roles.contains(fullRoleName));
            }
        }, AuthenticationUtil.getAdminUserName()).booleanValue();
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#createRole(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.lang.String, java.util.Set)
     */
    public Role createRole(final NodeRef rmRootNode, final String role, final String roleDisplayLabel, final Set<Capability> capabilities)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Role>()
        {
            public Role doWork() throws Exception
            {
                String fullRoleName = getFullRoleName(role, rmRootNode);
                
                // Check that the role does not already exist for the rm root node
                Set<String> exists = authorityService.findAuthoritiesByShortName(AuthorityType.GROUP, fullRoleName);
                if (exists.size() != 0)
                {
                    throw new AlfrescoRuntimeException("The role " + role + " already exists for root rm node " + rmRootNode.getId());
                }
                
                // Create a group that relates to the records management role
                Set<String> zones = new HashSet<String>(1);
                zones.add(getZoneName(rmRootNode));
                String roleGroup = authorityService.createAuthority(AuthorityType.GROUP, fullRoleName, roleDisplayLabel, zones);
                
                // Assign the various capabilities to the group on the root records management node
                for (Capability capability : capabilities)
                {
                    permissionService.setPermission(rmRootNode, roleGroup, capability.getName(), true);
                }
                
                Set<String> capStrings = new HashSet<String>(capabilities.size());
                for (Capability capability : capabilities)
                {
                    capStrings.add(capability.getName());
                }
                return new Role(role, roleDisplayLabel, capStrings);
            }
        }, AuthenticationUtil.getAdminUserName());
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#updateRole(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.lang.String, java.util.Set)
     */
    public Role updateRole(final NodeRef rmRootNode, final String role, final String roleDisplayLabel, final Set<Capability> capabilities)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Role>()
        {
            public Role doWork() throws Exception
            {                                
                String roleAuthority = authorityService.getName(AuthorityType.GROUP, getFullRoleName(role, rmRootNode));

                authorityService.setAuthorityDisplayName(roleAuthority, roleDisplayLabel);
                
                // Remove all the current capabilities                
                permissionService.clearPermission(rmRootNode, roleAuthority);
                
                // Re-add the provided capabilities
                for (Capability capability : capabilities)
                {
                    permissionService.setPermission(rmRootNode, roleAuthority, capability.getName(), true);
                }
                
                Set<String> capStrings = new HashSet<String>(capabilities.size());
                for (Capability capability : capabilities)
                {
                    capStrings.add(capability.getName());
                }
                return new Role(role, roleDisplayLabel, capStrings);
                
            }
        }, AuthenticationUtil.getAdminUserName());
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#deleteRole(java.lang.String)
     */
    public void deleteRole(final NodeRef rmRootNode, final String role)
    {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
        {
            public Boolean doWork() throws Exception
            {                                
                String roleAuthority = authorityService.getName(AuthorityType.GROUP, getFullRoleName(role, rmRootNode));
                authorityService.deleteAuthority(roleAuthority);                
                return null;
                
            }
        }, AuthenticationUtil.getAdminUserName());
    }
}
