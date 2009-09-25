/*
 * Copyright (C) 2009-2009 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_dod5015.job;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * The Disposition Lifecycle Job Finds all disposition action nodes which are
 * for "retain" or "cutOff" actions Where asOf > now OR
 * dispositionEventsEligible = true; 
 * 
 * Runs the cut off or retain action for
 * elligible records. 
 * 
 * @author mrogers
 */
public class DispositionLifecycleJob implements Job
{
    private static Log logger = LogFactory.getLog(DispositionLifecycleJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        final RecordsManagementActionService rmActionService = (RecordsManagementActionService) context
                    .getJobDetail().getJobDataMap().get("recordsManagementActionService");
        final NodeService nodeService = (NodeService) context.getJobDetail().getJobDataMap().get(
                    "nodeService");
        final SearchService search = (SearchService) context.getJobDetail().getJobDataMap().get(
                    "searchService");
        final TransactionService trxService = (TransactionService) context.getJobDetail()
                    .getJobDataMap().get("transactionService");

        logger.debug("Job Starting");

        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                StringBuilder sb = new StringBuilder();
                sb.append("+TYPE:\"rma:dispositionAction\" ");
                sb.append("+(@rma\\:dispositionAction:(\"cutOff\" OR \"retain\"))");
                sb.append("+ISNULL:\"rma:dispositionActionCompletedAt\" ");
                sb.append("+( ");
                sb.append("@rma\\:dispositionEventsEligible:true "); 
                sb.append("OR @rma\\:dispositionAsOf:[MIN TO NOW] ");
                sb.append(") ");

                String query = sb.toString();

                ResultSet results = search.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                            SearchService.LANGUAGE_LUCENE, query);

                List<NodeRef> resultNodes = results.getNodeRefs();

                RetryingTransactionHelper trn = trxService.getRetryingTransactionHelper();

                for (NodeRef node : resultNodes)
                {
                    final NodeRef currentNode = node;

                    RetryingTransactionCallback<Boolean> processTranCB = new RetryingTransactionCallback<Boolean>()
                    {
                        public Boolean execute() throws Throwable
                        {
                            final String dispAction = (String) nodeService.getProperty(currentNode,
                                        RecordsManagementModel.PROP_DISPOSITION_ACTION);

                            // Run "retain" and "cutoff" actions.

                            if (dispAction != null)
                            {
                                if (dispAction.equalsIgnoreCase("cutoff") ||
                                    dispAction.equalsIgnoreCase("retain"))
                                {
                                    ChildAssociationRef parent = nodeService.getPrimaryParent(currentNode);
                                    if (parent.getTypeQName().equals(RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION))
                                    {
                                        // Check that the action is executable
                                        RecordsManagementAction rmAction = rmActionService.getDispositionAction(dispAction);
                                        if (rmAction.isExecutable(parent.getParentRef(), null) == true)
                                        {
                                            rmActionService.executeRecordsManagementAction(parent.getParentRef(), dispAction);
                                            if (logger.isDebugEnabled())
                                            {
                                                logger.debug("Processed action: " + dispAction + "on" + parent);
                                            }
                                        }
                                        else
                                        {
                                            logger.debug("The disposition action " + dispAction + " is not executable.");
                                        }
                                    }
                                    return null;
                                }
                            }
                            return Boolean.TRUE;
                        }
                    };
                    /**
                     * Now do the work, one action in each transaction
                     */
                    trn.doInTransaction(processTranCB);
                }
                return null;
            };

        }, AuthenticationUtil.getSystemUserName());

        logger.debug("Job Finished");
    }
}
