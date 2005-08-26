/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.transaction;

import javax.transaction.UserTransaction;

import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.transaction.SpringAwareUserTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * Default implementation of Transaction Service
 * 
 * @author David Caruana
 */
public class TransactionComponent implements TransactionService
{
    private PlatformTransactionManager transactionManager;
    
    /**
     * Construct Transaction Component
     * 
     * @param transactionManager platform transaction manager
     */
    public TransactionComponent(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }
    
    /**
     * @see org.springframework.transaction.TransactionDefinition#PROPAGATION_REQUIRED
     */
    public UserTransaction getUserTransaction()
    {
        return new SpringAwareUserTransaction(transactionManager);
    }

    /**
     * @see org.springframework.transaction.TransactionDefinition#PROPAGATION_REQUIRES_NEW
     */
    public UserTransaction getNonPropagatingUserTransaction()
    {
        SpringAwareUserTransaction txn = new SpringAwareUserTransaction(transactionManager);
        txn.setPropagationBehviour(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return txn;
    }
}
