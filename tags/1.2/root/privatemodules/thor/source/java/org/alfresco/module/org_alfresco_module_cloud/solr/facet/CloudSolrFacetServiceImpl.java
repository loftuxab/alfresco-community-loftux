/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.module.org_alfresco_module_cloud.solr.facet;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.repo.search.impl.solr.facet.SolrFacetServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;

/**
 * Cloud implementation of the {@link org.alfresco.repo.search.impl.solr.facet.SolrFacetService}
 * 
 * @author Jamal Kaabi-Mofrad
 */
public class CloudSolrFacetServiceImpl extends SolrFacetServiceImpl
{

    private PersonService personService;

    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }

    @Override
    public boolean isSearchAdmin(String userName)
    {
        NodeRef person = personService.getPersonOrNull(userName);
        boolean isNetworkAdmin = (person == null) ? false : nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN);
        return isNetworkAdmin || super.isSearchAdmin(userName);
    }
}
