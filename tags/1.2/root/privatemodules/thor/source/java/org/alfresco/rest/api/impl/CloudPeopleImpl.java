/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.rest.api.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.site.CloudSiteService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.rest.api.model.Person;
import org.alfresco.rest.framework.core.exceptions.EntityNotFoundException;
import org.alfresco.service.namespace.QName;

/**
 * Centralises access to person services and maps between representations.
 * 
 * @author steveglover
 *
 */
public class CloudPeopleImpl extends PeopleImpl
{
	private CloudSiteService cloudSiteService;
	private CloudPersonService cloudPersonService;
	
	public void setCloudSiteService(CloudSiteService cloudSiteService)
	{
		this.cloudSiteService = cloudSiteService;
	}

    public void setCloudPersonService(CloudPersonService cloudPersonService)
    {
		this.cloudPersonService = cloudPersonService;
	}

	private List<String> filterUsers(List<String> peopleIds)
    {
        List<String> unfilteredUserIds = new ArrayList<String>(peopleIds);
        List<String> filteredUserIds = cloudSiteService.filterVisibleUsers(unfilteredUserIds, peopleIds.size());
        return filteredUserIds;
    }

    @Override
	public String validatePerson(String personId)
	{
    	personId = super.validatePerson(personId, false);

    	List<String> filteredUserIds = filterUsers(Collections.singletonList(personId));
        if(!filteredUserIds.contains(personId))
        {
        	throw new EntityNotFoundException(personId);
        }

    	return personId;
	}
	
    @Override
	public String validatePerson(String personId, boolean validateIsCurrentUser)
	{
    	personId = super.validatePerson(personId, validateIsCurrentUser);

    	List<String> filteredUserIds = filterUsers(Collections.singletonList(personId));
        if(!filteredUserIds.contains(personId))
        {
        	throw new EntityNotFoundException(personId);
        }

    	return personId;
	}

    @Override
    protected void processPersonProperties(String userName, final Map<QName, Serializable> nodeProps)
    {
    	if(!cloudPersonService.isFullProfileVisible(userName))
    	{
			nodeProps.remove(ContentModel.PROP_LOCATION);
			nodeProps.remove(ContentModel.PROP_TELEPHONE);
			nodeProps.remove(ContentModel.PROP_MOBILE);
			nodeProps.remove(ContentModel.PROP_EMAIL);
	
			nodeProps.remove(ContentModel.PROP_ORGANIZATION);
			nodeProps.remove(ContentModel.PROP_COMPANYADDRESS1);
			nodeProps.remove(ContentModel.PROP_COMPANYADDRESS2);
			nodeProps.remove(ContentModel.PROP_COMPANYADDRESS3);
			nodeProps.remove(ContentModel.PROP_COMPANYPOSTCODE);
			nodeProps.remove(ContentModel.PROP_COMPANYTELEPHONE);
			nodeProps.remove(ContentModel.PROP_COMPANYFAX);
			nodeProps.remove(ContentModel.PROP_COMPANYEMAIL);
			nodeProps.remove(ContentModel.PROP_SKYPE);
			nodeProps.remove(ContentModel.PROP_INSTANTMSG);
			nodeProps.remove(ContentModel.PROP_USER_STATUS);
			nodeProps.remove(ContentModel.PROP_USER_STATUS_TIME);
			nodeProps.remove(ContentModel.PROP_GOOGLEUSERNAME);
			nodeProps.remove(ContentModel.PROP_SIZE_QUOTA);
			nodeProps.remove(ContentModel.PROP_SIZE_CURRENT);
			nodeProps.remove(ContentModel.PROP_EMAIL_FEED_DISABLED);
			nodeProps.remove(Person.PROP_PERSON_DESCRIPTION);
    	}

		super.processPersonProperties(userName, nodeProps);
    }
}
