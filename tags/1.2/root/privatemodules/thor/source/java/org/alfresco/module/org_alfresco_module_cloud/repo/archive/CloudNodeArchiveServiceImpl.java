/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.repo.archive;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.repo.node.archive.NodeArchiveServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;

/**
 * Cloud implementation of the node archive abstraction
 * 
 * @author Jamal Kaabi-Mofrad
 */
public class CloudNodeArchiveServiceImpl extends NodeArchiveServiceImpl
{
    private PersonService personService;

    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }

    @Override
    protected boolean hasAdminAccess(String userID)
    {
        return (isNetworkAdmin(userID) || super.hasAdminAccess(userID));
    }

    private boolean isNetworkAdmin(String userID)
    {
        NodeRef person = personService.getPerson(userID, false);
        return (person == null) ? false : nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN);
    }
}
