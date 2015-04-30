/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteServiceImpl;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteMemberInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thor-specific extension to the SiteServiceImpl
 * 
 * Note: currently CloudPersonService & CloudSiteService are dependent on each other, for example to implement privacy rules 
 * (when listing people / site members - based on site membership unless in same home network).
 * 
 * @author Matt Ward, janv, sglover
 */
public class CloudSiteServiceImpl extends SiteServiceImpl implements CloudSiteService
{
    private static final Log logger = LogFactory.getLog(CloudSiteServiceImpl.class);
    
    private DirectoryService directoryService;
    private AccountService accountService;
    
    private NodeService nodeService;
    private CloudPersonService cloudPersonService;
    
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        super.setNodeService(nodeService);
        this.nodeService = nodeService;
    }
    
    public void setCloudPersonService(CloudPersonService cloudPersonService)
    {
        this.cloudPersonService = cloudPersonService;
    }
    
    @Override
    public SiteInfo createSite(final String sitePreset, 
                String passedShortName, 
                final String title, 
                final String description, 
                final SiteVisibility visibility,
                final QName siteType)
    {   
        SiteInfo siteInfo = super.createSite(sitePreset, passedShortName, title, description, visibility, siteType);
        
        // Ensure documentLibrary creation.
        NodeRef docLib = createContainer(siteInfo.getShortName(), SiteService.DOCUMENT_LIBRARY, ContentModel.TYPE_FOLDER, null);
        
        if (logger.isDebugEnabled())
        {
            if (docLib == null)
            {
                logger.debug("Unable to create documentLibrary (null returned).");
            }
            else
            {
                logger.debug("Created documentLibrary: " + docLib);                
            }
        }
        return siteInfo;
    }

    public void deleteSite(final String shortName)
    {
        final Map<String, String> allMembers = new HashMap<String, String>();
        listMembersUnfiltered(shortName, null, null, true, new SiteMembersCallback()
        {
            public void siteMember(String authority, String permission)
            {
                allMembers.put(authority, permission);
            }

            public boolean isDone()
            {
                // want all members
                return false;
            }
        });

        super.deleteSite(shortName);

        for (String userName : allMembers.keySet())
        {
            removeExternalUserWithNoMoreSites(userName);
        }
    }

    @Override
    public void removeMembership(final String shortName, final String userName)
    {
        super.removeMembership(shortName, userName);
        
        removeExternalUserWithNoMoreSites(userName);
    }
    
    // THOR-538 / THOR-1168 - if external user no longer belongs to any sites then remove them from the external network
    private void removeExternalUserWithNoMoreSites(final String userName)
    {
        AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                if (nodeService.hasAspect(cloudPersonService.getPerson(userName, false), CloudModel.ASPECT_EXTERNAL_PERSON))
                {
                    if (listSites(userName, 50).size() == 0)
                    {
                        cloudPersonService.removeExternalUser(accountService.getAccountByDomain(TenantUtil.getCurrentDomain()), userName);
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected Map<String, String> listMembersImpl(String shortName, String nameFilter, String roleFilter, int size, boolean collapseGroups)
    {
        SizedSiteMembersCallback callback = new SizedSiteMembersCallback(size);
        listMembersImpl(shortName, nameFilter, roleFilter, collapseGroups, callback);
        return callback.getMembers();
    }

    @Override
    protected void listMembersImpl(String shortName, String nameFilter, String roleFilter, boolean collapseGroups, SiteMembersCallback callback)
    {
        final String runAsUser = AuthenticationUtil.getRunAsUser();

        if(!AuthenticationUtil.isRunAsUserTheSystemUser() &&
            (! AuthenticationUtil.getAdminUserName().equals(AuthenticationUtil.getRunAsUser())) &&
                (! isMember(shortName, runAsUser)))
		{
        	final UserVisibilityChecker userVisibilityChecker = new UserVisibilityChecker(this, directoryService);
        	final SiteMembersCallback saveCallback = callback;
        	// override callback to do additional filtering
        	callback = new SiteMembersCallback()
        	{
        		@Override
        		public void siteMember(String authority, String permission)
        		{
                    // THOR-1173, ACE-1877
                    boolean isVisible = false;
                    try
                    {
                        isVisible = userVisibilityChecker.isVisible(authority);
                    }
                    catch (InvalidEmailAddressException e)
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.debug("Authority name " + authority + " is malformed");
                        }
                    }
        			if(isVisible)
        			{
        				saveCallback.siteMember(authority, permission);
        			}    				
        		}

                @Override
                public boolean isDone()
                {
                    return saveCallback.isDone();
                }
            };
        }

        listMembersUnfiltered(shortName, nameFilter, roleFilter, collapseGroups, callback);
    }
    
    @Override
    // CLOUD-1640
    protected List<SiteMemberInfo> listMembersInfoImpl(String shortName, String nameFilter, String roleFilter, int size, boolean collapseGroups)
    {
        final String runAsUser = AuthenticationUtil.getRunAsUser();
        List<SiteMemberInfo> allMembersInfo = super.listMembersInfoImpl(shortName, nameFilter, roleFilter, size, collapseGroups);
        List<SiteMemberInfo> visibleMembers = new ArrayList<SiteMemberInfo>(allMembersInfo.size());

        final UserVisibilityChecker userVisibilityChecker = new UserVisibilityChecker(this, directoryService);

        if (!AuthenticationUtil.isRunAsUserTheSystemUser()
                    && (!AuthenticationUtil.getAdminUserName().equals(
                                AuthenticationUtil.getRunAsUser()))
                    && (!isMember(shortName, runAsUser)))
        {
            for (SiteMemberInfo memberInfo : allMembersInfo)
            {
                if (userVisibilityChecker.isVisible(memberInfo.getMemberName()))
                {
                    visibleMembers.add(memberInfo);
                }
            }

            return visibleMembers;
        }
        return allMembersInfo;
    }
    
//    @SuppressWarnings("unchecked")
//  public PagingResults<SiteMembership> listMembersPaged(String shortName, boolean collapseGroups, PagingRequest pagingRequest)
//    {
//      SiteMembershipCannedQueryFactory sitesCannedQueryFactory = (SiteMembershipCannedQueryFactory)cannedQueryRegistry.getNamedObject("sitesCannedQueryFactory");
//
//        CannedQueryPageDetails pageDetails = new CannedQueryPageDetails(pagingRequest.getSkipCount(), pagingRequest.getMaxItems());
//      CannedQuerySortDetails sortDetails = new CannedQuerySortDetails(new Pair<Object, SortOrder>(SiteMembersCannedQueryParams.SortFields.LastName, SortOrder.ASCENDING), 
//              new Pair<Object, SortOrder>(SiteMembersCannedQueryParams.SortFields.FirstName, SortOrder.ASCENDING),
//              new Pair<Object, SortOrder>(SiteMembersCannedQueryParams.SortFields.Role, SortOrder.ASCENDING));
//        SiteMembersCannedQueryParams parameterBean = new SiteMembersCannedQueryParams(shortName, collapseGroups);
//        CannedQueryParameters params = new CannedQueryParameters(parameterBean, pageDetails, sortDetails, pagingRequest.getRequestTotalCountMax(), pagingRequest.getQueryExecutionId());
//
//      CannedQuery<SiteMembership> query = sitesCannedQueryFactory.getCannedQuery(params);
//
//      CannedQueryResults<SiteMembership> results = query.execute();
//
//      return getPagingResults(pagingRequest, results);
//    }
    
    private void listMembersUnfiltered(String shortName, String nameFilter, String roleFilter, boolean collapseGroups, SiteMembersCallback callback)
    {
        super.listMembersImpl(shortName, nameFilter, roleFilter, collapseGroups, callback);
    }
    
    private Map<String, String> listAllMembersUnfiltered(String shortName, String nameFilter, String roleFilter, boolean collapseGroups)
    {
        final Map<String, String> members = new HashMap<String, String>(32);
        SiteMembersCallback callback = new SiteMembersCallback()
        {
            @Override
            public void siteMember(String authority, String permission)
            {
                members.put(authority, permission);
            }

            @Override
            public boolean isDone()
            {
                return false;
            }
        };
        super.listMembersImpl(shortName, nameFilter, roleFilter, collapseGroups, callback);
        return members;
    }
    
//    protected boolean isUserVisible(String userName, Long personAccount, String runAsUserName, Long runAsUserAccount)
//    {
//      boolean ret = true;
//
//        if (! isSameHomeTenant(runAsUserAccount, personAccount))
//        {
//          Set<String> members = null;
//            if (members == null)
//            {
//                members = getAllSiteMemberships(runAsUserName);
//            }
//            
//            if (! members.contains(userName))
//            {
//                // skip - filtered out
//                ret = false;
//            }
//        }
//
//        return ret;
//    }
    
    public List<String> filterVisibleUsers(List<String> userNames, int maxItems)
    {
        String runAsUserName = AuthenticationUtil.getRunAsUser();
        
        List<String> results = new ArrayList<String>(userNames.size());
        
        if (! (AuthenticationUtil.getAdminUserName().equals(runAsUserName)))
        {
        	UserVisibilityChecker userVisibilityChecker = new UserVisibilityChecker(this, directoryService);
            int added = 0;
            
            for (String userName : userNames)
            {
                if(!userVisibilityChecker.isVisible(userName))
                {
                    // skip - filtered out
                    continue;
                }
                
                results.add(userName);
                added++;
                
                if (added == maxItems)
                {
                    break;
                }
            }
        }
        else
        {
            // special case: super admin (unfiltered)
            results = userNames;
        }
        
        return results;
    }
    
    @Override
    public boolean isSiteAdmin(String userName)
    {
        NodeRef person = cloudPersonService.getPersonOrNull(userName);
        boolean isNetworkAdmin = (person == null) ? false : nodeService.hasAspect(person,
                    CloudModel.ASPECT_NETWORK_ADMIN);

        return isNetworkAdmin || super.isSiteAdmin(userName);
    }

    private class VisibleUserFilter
    {
        private String runAsUserName;
        private boolean isAdmin;
        private Long runAsUserAccount;

        VisibleUserFilter()
        {
            this.runAsUserName = AuthenticationUtil.getRunAsUser();
            this.isAdmin = AuthenticationUtil.getAdminUserName().equals(runAsUserName);
            this.runAsUserAccount = directoryService.getHomeAccount(runAsUserName);
        }

        boolean filter(String userName)
        {
            boolean ret = false;

            if (!isAdmin)
            {
                // note: special case: super admin (unfiltered)
                Long personAccount = directoryService.getHomeAccount(userName);
                if (!isSameHomeTenant(runAsUserAccount, personAccount))
                {
                    Set<String> members = getAllSiteMemberships(runAsUserName);
                    if(!members.contains(userName))
                    {
                        ret = true;
                    }
                }
            }

            return ret;
        }
    }

    // note: if one or both are null => not in same home account/tenant, eg. public users (and also super admin users)
    public boolean isSameHomeTenant(Long account1, Long account2)
    {
        if (account1 == null)
        {
            return false;
        }
        return account1.equals(account2);
    }
    
    public Set<String> getAllSiteMemberships(String username)
    {
        List<SiteInfo> sites = listSites(username);
        
        Set<String> members = new HashSet<String>(100);
        
        if (sites.size() > 0)
        {
            for (SiteInfo site : sites)
            {
                members.addAll(listAllMembersUnfiltered(site.getShortName(), null, null, true).keySet());
            }
        }
        else
        {
            // user is not a member of any sites (eg. super "admin" may not have created any sites)
            members.add(username);
        }
        
        return members;
    }
    
    /*
     * A filter that determines if a user is in the same network/account as the runAs user, or
     * shares a site membership. Returns false if not.
     */
    static class UserVisibilityChecker
    {
        private DirectoryService directoryService;
        private CloudSiteService cloudSiteService;

        private String runAsUserName;
        private Long runAsUserAccount;

        UserVisibilityChecker(CloudSiteService cloudSiteService, DirectoryService directoryService)
        {
        	this.directoryService = directoryService;
        	this.cloudSiteService = cloudSiteService;
        	this.runAsUserName = AuthenticationUtil.getRunAsUser();
        	this.runAsUserAccount = directoryService.getHomeAccount(runAsUserName);
        }

        boolean isVisible(String userName)
        {
            boolean ret = true;

            Long personAccount = directoryService.getHomeAccount(userName);
            if (!cloudSiteService.isSameHomeTenant(runAsUserAccount, personAccount))
            {
                Set<String> members = getAllSiteMembershipsFromTransactionalResource(runAsUserName);

                if (!members.contains(userName))
                {
                    // skip - filtered out
                    ret = false;
                }
            }

            return ret;
        }
        
        
        private Set<String> getAllSiteMembershipsFromTransactionalResource(String runAsUserName)
        {
            Map<String, Set<String>> map = (Map<String, Set<String>>) AlfrescoTransactionSupport.getResource("TX_CACHED_SITE_MEMBERSHIPS");
            if (map == null)
            {
                map = new HashMap<String, Set<String>>();
                AlfrescoTransactionSupport.bindResource("TX_CACHED_SITE_MEMBERSHIPS", map); 
            }

            Set<String> userSites = map.get(runAsUserName);
            if(userSites == null)
            {
                userSites = cloudSiteService.getAllSiteMemberships(runAsUserName);
                map.put(runAsUserName, userSites);
            }
                 
            return userSites;
        }
    }
    
   
    
    static class SizedSiteMembersCallback implements SiteMembersCallback
    {
        private Map<String, String> allMembers;
        private int size;
        private int count;

        SizedSiteMembersCallback(int size)
        {
            this.allMembers = new HashMap<String, String>();
            this.size = size;
            this.count = 0;
        }

        public void siteMember(String authority, String permission)
        {
            allMembers.put(authority, permission);
            count++;
        }

        public boolean isDone()
        {
            return count >= size;
        }
        
        Map<String, String> getMembers()
        {
            return allMembers;
        }
    }
}
