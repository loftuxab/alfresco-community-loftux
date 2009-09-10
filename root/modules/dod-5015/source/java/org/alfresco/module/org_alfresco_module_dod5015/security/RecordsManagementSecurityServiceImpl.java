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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Records management permission service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSecurityServiceImpl implements RecordsManagementSecurityService, RecordsManagementModel
{
    /** Entry voter for capability related support */
    private RMEntryVoter voter;
    
    /** Authority service */
    private AuthorityService authorityService;
    
    /** Permission service */
    private PermissionService permissionService;
    
    private PolicyComponent policyComponent;
    
    private NodeService nodeService;
    
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
    
    /**
     * Set the authortiy service
     * 
     * @param authorityService
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    /**
     * Set the permission service
     * 
     * @param permissionService
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }  
    
    /**
     * Set the policy component
     * 
     * @param policyComponent
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set the node service
     * 
     * @param nodeService
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void init()
    {
        policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
                ASPECT_RECORDS_MANAGEMENT_ROOT, 
                new JavaBehaviour(this, "onCreateRootNode", NotificationFrequency.TRANSACTION_COMMIT));
    }
    
    /**
     * Create root node behaviour
     * 
     * @param childAssocRef
     */
    public void onCreateRootNode(ChildAssociationRef childAssocRef)
    {
       // Bootstrp in the default set of roles for the newly created root node
        NodeRef rmRootNode = childAssocRef.getChildRef();
        if (nodeService.exists(rmRootNode) == true)
        {
            bootstrapDefaultRoles(rmRootNode);
        }
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
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#bootstrapDefaultRoles(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void bootstrapDefaultRoles(final NodeRef rmRootNode)
    {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
        {
            public Object doWork()
            {
                try
                {
                    JSONArray array = null;
                    try
                    {
                        // Load up the default roles from JSON
                        InputStream is = getClass().getClassLoader().getResourceAsStream("alfresco/module/org_alfresco_module_dod5015/security/rm-default-roles-bootstrap.json");
                        if  (is == null)
                        {
                            throw new AlfrescoRuntimeException("Could not load default bootstrap roles configuration");
                        }
                        array = new JSONArray(convertStreamToString(is));
                    }
                    catch (IOException ioe)
                    {
                        throw new AlfrescoRuntimeException("Unable to load rm-default-roles-bootstrap.json configuration file.", ioe);
                    }
                    
                    // Add each role to the rm root node
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        
                        // Get the name of the role
                        String name = null;
                        if (object.has("name") == true)
                        {
                            name = object.getString("name");
                            if (existsRole(rmRootNode, name) == true)
                            {
                                throw new AlfrescoRuntimeException("The bootstrap role " + name + " already exists on the rm root node " + rmRootNode.toString());
                            }
                        }
                        else
                        {
                            throw new AlfrescoRuntimeException("No name given to default bootstrap role.  Check json configuration file.");
                        }
                        
                                                
                        // Get the role's display label
                        String displayLabel = name;
                        if (object.has("displayLabel") == true)
                        {
                            displayLabel = object.getString("displayLabel");
                        }
                        
                        // Get the roles capabilities
                        Set<Capability> capabilities = new HashSet<Capability>(30);
                        if (object.has("capabilities") == true)
                        {
                            JSONArray arrCaps = object.getJSONArray("capabilities");
                            for (int index = 0; index < arrCaps.length(); index++)
                            {
                                String capName = arrCaps.getString(index);
                                // Handle special "Filing" capability
                                if ("Filing".equals(capName) == true)
                                {
                                    permissionService.setPermission(rmRootNode, getFullRoleName(name, rmRootNode), capName, true);
                                }
                                else
                                {
                                    Capability capability = getCapability(capName);
                                    if (capability == null)
                                    {
                                        throw new AlfrescoRuntimeException("The capability '" + capName + "' configured for the deafult boostrap role '" + name + "' is invalid.");
                                    }
                                    capabilities.add(capability);
                                }
                            }
                        }
                        
                        // Create the role
                        createRole(rmRootNode, name, displayLabel, capabilities);
                    }
                }
                catch (JSONException exception)
                {
                    throw new AlfrescoRuntimeException("Error loading json configuration file rm-default-roles-bootstrap.json", exception);
                }
                
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    public String convertStreamToString(InputStream is) throws IOException
    {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
         
        String line = null;
        try 
        {
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
        }
        finally 
        {
            try {is.close();} catch (IOException e) {}
        }
         
        return sb.toString();
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
                    
                    Role role = new Role(name, displayLabel, capabilities, roleAuthority);
                    result.add(role);            
                }
                
                return result;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService#getRolesByUser(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public Set<Role> getRolesByUser(final NodeRef rmRootNode, final String user)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Set<Role>>()
        {
            public Set<Role> doWork() throws Exception
            {
                Set<Role> result = new HashSet<Role>(13);
                
                Set<String> roleAuthorities = authorityService.getAllAuthoritiesInZone(getZoneName(rmRootNode), AuthorityType.GROUP);        
                for (String roleAuthority : roleAuthorities)
                {
                    Set<String> users = authorityService.getContainedAuthorities(AuthorityType.USER, roleAuthority, false);
                    if (users.contains(user) == true)
                    {                    
                        String name = getShortRoleName(authorityService.getShortName(roleAuthority), rmRootNode);
                        String displayLabel = authorityService.getAuthorityDisplayName(roleAuthority);
                        Set<String> capabilities = getCapabilities(rmRootNode, roleAuthority);
                        
                        Role role = new Role(name, displayLabel, capabilities, roleAuthority);
                        result.add(role);  
                    }
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
    
    /**
     * Get the full role name
     * 
     * @param role
     * @param rmRootNode
     * @return
     */
    private String getFullRoleName(String role, NodeRef rmRootNode)
    {
        return role + rmRootNode.getId();
    }
    
    /**
     * Get the short role name
     * 
     * @param fullRoleName
     * @param rmRootNode
     * @return
     */
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
                
                return new Role(name, displayLabel, capabilities, getFullRoleName(role, rmRootNode));
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
                Set<String> zones = new HashSet<String>(2);
                zones.add(getZoneName(rmRootNode));
                zones.add(AuthorityService.ZONE_APP_DEFAULT);
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
                return new Role(role, roleDisplayLabel, capStrings, fullRoleName);
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
                return new Role(role, roleDisplayLabel, capStrings, getFullRoleName(role, rmRootNode));
                
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
