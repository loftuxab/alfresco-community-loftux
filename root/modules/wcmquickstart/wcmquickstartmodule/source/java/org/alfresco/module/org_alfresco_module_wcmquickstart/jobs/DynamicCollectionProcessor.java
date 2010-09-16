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

import java.util.Calendar;
import java.util.Date;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.WebassetCollectionHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 */
public class DynamicCollectionProcessor implements WebSiteModel
{
    /** Log */
	private static final Log log = LogFactory.getLog(DynamicCollectionProcessor.class);

	/** Query */
	private static final String QUERY = "+ TYPE:\"ws:webassetCollection\" + @ws\\:isDynamic:true";
	
	/** Transaction service */
    private TransactionService transactionService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Webasset Collection Helper */
    private WebassetCollectionHelper collectionHelper;
    
    /**
     * Set search service
     * @param searchService search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set transaction service
     * @param transactionService    transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Set collection helper
     * @param collectionHelper  collection helper
     */
    public void setCollectionHelper(WebassetCollectionHelper collectionHelper)
    {
        this.collectionHelper = collectionHelper;
    }

    /**
     * Run the processor job.  Refreshing any dynamic queries who's refresh date is before today.
     */
    public void run()
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(
                        new RetryingTransactionCallback<Object>()
                {
                    @Override
                    public Object execute() throws Throwable
                    {
                        //Find all web root nodes
                        ResultSet rs = searchService.query(
                        				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                        				SearchService.LANGUAGE_LUCENE, 
                        				QUERY);
                     
                        if (log.isDebugEnabled())
                        {
                            log.debug("Running dynamic collection refresh processor across " + rs.length() + " dynamic collection nodes");
                        }
                        
                        // Get the current date
                        Calendar now = Calendar.getInstance();
                        
                        // Interate over the dynamic queries 
                        for (NodeRef collection : rs.getNodeRefs())
                        {
                            Date refreshAtDate = (Date)nodeService.getProperty(collection, PROP_REFRESH_AT);
                            Calendar refreshAt = Calendar.getInstance();
                            if (refreshAtDate != null)
                            {
                                // Convert the date to calendar
                                refreshAt.setTime(refreshAtDate);
                            }
                                
                            if ((refreshAtDate == null) || now.after(refreshAt))
                            {
                                if (log.isDebugEnabled() == true)
                                {
                                    String collectionName = (String)nodeService.getProperty(collection, ContentModel.PROP_NAME);
                                    if (collectionName != null)
                                    {
                                        log.debug("Refreshing dynamic collection " + collectionName);
                                    }
                                }                                    
                                
                                // Refresh the collection
                                collectionHelper.refreshCollection(collection);
                            }
                        }
                        return null;
                    }   
                });
                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }
}
