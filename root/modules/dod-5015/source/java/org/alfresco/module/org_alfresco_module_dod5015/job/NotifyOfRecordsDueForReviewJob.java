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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.org_alfresco_module_dod5015.job;

import java.util.List;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
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
 * This job finds all Vital Records which are due for review, optionally
 * excluding those for which notification has already been issued.
 * 
 * @author Neil McErlean
 */
public class NotifyOfRecordsDueForReviewJob implements Job
{
    private static Log logger = LogFactory.getLog(NotifyOfRecordsDueForReviewJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        final NodeService nodeService = (NodeService) context.getJobDetail().getJobDataMap().get(
                    "nodeService");
        final SearchService search = (SearchService) context.getJobDetail().getJobDataMap().get(
                    "searchService");
        final TransactionService trxService = (TransactionService) context.getJobDetail()
                    .getJobDataMap().get("transactionService");

        if (logger.isDebugEnabled())
        {
            logger.debug("Job " + this.getClass().getSimpleName() + " starting.");
        }

        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                // Query is for all records that are due for review and for which
                // a notification has not been sent.
                StringBuilder queryBuffer = new StringBuilder();
                queryBuffer.append("+ASPECT:\"rma:vitalRecord\" ");
//                sb.append("+TYPE:\"rma:recordFolder\" ");
//                sb.append("+(@rma\\:dispositionAction:(\"cutOff\" OR \"retain\"))");
                
                queryBuffer.append("+(@rma\\:reviewAsOf:[MIN TO NOW] ) ");
                queryBuffer.append("+( ");
                    queryBuffer.append("@rma\\:notificationIssued:false "); 
                    queryBuffer.append("OR ISNULL:\"rma:notificationIssued\" ");
                queryBuffer.append(") ");
                String query = queryBuffer.toString();

                ResultSet results = search.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                            SearchService.LANGUAGE_LUCENE, query);

                List<NodeRef> resultNodes = results.getNodeRefs();
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Found " + resultNodes.size() + " nodes due for review and without notification.");
                }

                RetryingTransactionHelper trn = trxService.getRetryingTransactionHelper();

                for (NodeRef node : resultNodes)
                {
                    final NodeRef currentNode = node;
                    RetryingTransactionCallback<Boolean> txCallback = new RetryingTransactionCallback<Boolean>()
                    {
                        public Boolean execute() throws Throwable
                        {
                            //TODO Send notification for these nodes and set rma:notificationIssued to true
                            
                            //TODO Perhaps we should roll up the notification message to list
                            // all parent folders.
                            return Boolean.TRUE;
                        }
                    };
                    /**
                     * Now do the work, one action in each transaction
                     */
                    trn.doInTransaction(txCallback);
                }
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());

        if (logger.isDebugEnabled())
        {
            logger.debug("Job " + this.getClass().getSimpleName() + " finished");
        }
    }
}
