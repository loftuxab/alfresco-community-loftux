/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed to be run at midnight. It finds any web assets that are either becoming available today or expiring today
 * and sets their "published" flag as appropriate
 * @author Brian
 *
 */
public class AvailabilityProcessor
{
    private static final Log log = LogFactory.getLog(AvailabilityProcessor.class);
    
    private RetryingTransactionHelper txHelper;
    private SearchService searchService;
    private NodeService nodeService;
    private BehaviourFilter behaviourFilter;
    
    public void run()
    {
        txHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                return AuthenticationUtil.runAs(new RunAsWork<Object>()
                {
                    @Override
                    public Object doWork() throws Exception
                    {
                        behaviourFilter.disableBehaviour(ContentModel.ASPECT_AUDITABLE);
                        try
                        {
                            //Find all web assets that are due to become available today
                            ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableFromDate:today +@ws\\:published:\"false\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to become available: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.TRUE);
                            }

                            //Find all web assets that are due to expire today
                            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableToDate:today +@ws\\:published:\"true\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to expire: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.FALSE);
                            }
                        }
                        finally
                        {
                            behaviourFilter.enableBehaviour(ContentModel.ASPECT_AUDITABLE);
                        }
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        });
    }
    
    public void setTxHelper(RetryingTransactionHelper txHelper)
    {
        this.txHelper = txHelper;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
    
}
