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
package org.alfresco.module.org_alfresco_module_cloud.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.PermissionServiceSPI;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CloudPersonServiceImpl implements CloudPersonService
{
    private static final Log logger = LogFactory.getLog(CloudPersonServiceImpl.class);
    
    private PersonService personService;
    private NodeService nodeService;
    private AuthorityService authorityService;
	private DirectoryService directoryService;
    private PermissionServiceSPI permissionServiceSPI;

	public void setDirectoryService(DirectoryService directoryService)
	{
		this.directoryService = directoryService;
	}

	public void setPersonService(PersonService personService)
	{
		this.personService = personService;
	}

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPermissionServiceSPI(PermissionServiceSPI service)
    {
        this.permissionServiceSPI = service;
    }
    
    public void setAuthorityService(AuthorityService service)
    {
        this.authorityService = service;
    }

    private boolean isAdministrator(PersonInfo personInfo)
    {
        return authorityService.isAdminAuthority(personInfo.getUserName());
    }

    @Override
    public PagingResults<NodeRef> getPeople(final String nameFilter, final String sortBy, final int skipCount, final int maxItems, final TYPE type, final Boolean networkAdmin)
    {
        int skip = skipCount;

        // Build the filter
        final List<QName> filter = new ArrayList<QName>();
        filter.add(ContentModel.PROP_FIRSTNAME);
        filter.add(ContentModel.PROP_LASTNAME);
        filter.add(ContentModel.PROP_USERNAME);

        // Build the sorting. The user controls the primary sort, we supply
        // additional ones automatically
        final List<Pair<QName, Boolean>> sort = new ArrayList<Pair<QName, Boolean>>();
        if ("lastName".equals(sortBy))
        {
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
        } else if ("firstName".equals(sortBy))
        {
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
        } else
        {
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
        }

        final List<NodeRef> ret = new ArrayList<NodeRef>(maxItems);
        PagingResults<PersonInfo> results = null;

        final PagingRequest paging = new PagingRequest(skip, maxItems);
        paging.setRequestTotalCountMax(Integer.MAX_VALUE);
        
        Set<QName> inclusiveAspects = new HashSet<QName>();
        Set<QName> exclusiveAspects = new HashSet<QName>();
        if (type == TYPE.EXTERNAL)
        {
            inclusiveAspects.add(CloudModel.ASPECT_EXTERNAL_PERSON);
        }
        else if (type == TYPE.INTERNAL)
        {
            exclusiveAspects.add(CloudModel.ASPECT_EXTERNAL_PERSON);
        }

        if (networkAdmin != null)
        {
            if (networkAdmin == true)
            {
                inclusiveAspects.add(CloudModel.ASPECT_NETWORK_ADMIN);
            }
            else
            {
                exclusiveAspects.add(CloudModel.ASPECT_NETWORK_ADMIN);
            }
        }
        inclusiveAspects = inclusiveAspects.isEmpty() ? null : inclusiveAspects;
        exclusiveAspects = exclusiveAspects.isEmpty() ? null : exclusiveAspects;
        
        results = personService.getPeople(nameFilter, filter, inclusiveAspects, exclusiveAspects, false, sort, paging);
        List<PersonInfo> people = results.getPage();

        for (PersonInfo person : people)
        {
            if (ret.size() >= maxItems)
            {
                break;
            }

            ret.add(person.getNodeRef());
        }

        final boolean hasMore = results.hasMoreItems();
        final Pair<Integer, Integer> totalResultCount = results.getTotalResultCount();
        return new PagingResults<NodeRef>()
        {
            @Override
            public List<NodeRef> getPage()
            {
                return ret;
            }

            @Override
            public boolean hasMoreItems()
            {
                return hasMore;
            }

            @Override
            public Pair<Integer, Integer> getTotalResultCount()
            {
                return totalResultCount;
            }

            @Override
            public String getQueryExecutionId()
            {
                return null;
            }
        };
    }

    public void removeExternalUser(final Account account, final String email)
    {
        removePersonProfile(email, account.getTenantId());
        directoryService.removeSecondaryAccount(email, account.getId());
    }

    public void removePersonProfile(final String email, final String tenantDomain)
    {
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Object>()
        {
            public Void doWork() throws Exception
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Removing external person profile for " + email + " in " + tenantDomain);
                }

                // find person for email id
                NodeRef person = personService.getPerson(email);
                if (person == null)
                {
                    return null;
                }

                // delete person, but not associated user authentication
                personService.deletePerson(person, false);

                // TODO: should we remove permissions, what if user is
                // re-invited again?
                permissionServiceSPI.deletePermissions(email);

                return null;
            }
        }, tenantDomain);
    }

    public NodeRef getPerson(final String userName, final boolean autoCreateHomeFolderAndMissingPersonIfAllowed)
    {
        return personService.getPerson(userName, autoCreateHomeFolderAndMissingPersonIfAllowed);
    }
    
    /**
     * see {@link PersonService#getPersonOrNull(String)}
     */
    public NodeRef getPersonOrNull(String userName)
    {
        return personService.getPersonOrNull(userName);
    }

    public NodeRef createPerson(Map<QName, Serializable> properties)
    {
        return personService.createPerson(properties);
    }

    // true if user is in same home tenant as the current (runAs) user
    // note: if either user is a public user then false (since public user has a null home tenant)
    public boolean isFullProfileVisible(String userName)
    {
        return isSameHomeTenantAsRunAsUser(directoryService, userName);
    }
    
    public static boolean isSameHomeTenantAsRunAsUser(DirectoryService directoryService, String userName)
    {
        return isSameHomeTenant(directoryService, AuthenticationUtil.getRunAsUser(), userName);
    }
    
    private static boolean isSameHomeTenant(DirectoryService directoryService, String userName1, String userName2)
    {
        if ((userName1 != null) && (userName1.equals(userName2)))
        {
            return true;
        }
        
        String superAdmin = AuthenticationUtil.getAdminUserName();
        if ((superAdmin.equals(userName1)) || (superAdmin.equals(userName2)))
        {
            // special case: super admin
            return true;
        }
        
        Long account1 = directoryService.getHomeAccount(userName1);;
        if (account1 == null)
        {
            // eg. public user
            return false;
        }
        return account1.equals(directoryService.getHomeAccount(userName2));
    }

}