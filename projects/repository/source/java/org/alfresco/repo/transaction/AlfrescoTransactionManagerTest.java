/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
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

import org.alfresco.service.ServiceRegistry;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

/**
 * Tests integration between our <tt>UserTransaction</tt> implementation and
 * our <tt>TransactionManager</tt>.
 * 
 * @see org.alfresco.repo.transaction.AlfrescoTransactionManager
 * @see org.alfresco.util.transaction.SpringAwareUserTransaction
 * 
 * @author Derek Hulley
 */
public class AlfrescoTransactionManagerTest extends TestCase
{
    private ServiceRegistry serviceRegistry;
    
    public void setUp() throws Exception
    {
        ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
        serviceRegistry = (ServiceRegistry) ctx.getBean("serviceRegistry");
    }
    
    public void testTransactionId() throws Exception
    {
        // get a user transaction
        UserTransaction txn = serviceRegistry.getUserTransaction();
        assertNull("Thread shouldn't have a txn ID", AlfrescoTransactionManager.getTransactionId());
        
        // begine the txn
        txn.begin();
        String txnId = AlfrescoTransactionManager.getTransactionId();
        assertNotNull("Expected thread to have a txn id", txnId);
        
        // check that it is threadlocal
        Thread thread = new Thread(new Runnable()
                {
                    public void run()
                    {
                        String txnId = AlfrescoTransactionManager.getTransactionId();
                        assertNull("New thread seeing txn id");
                    }
                });
        
        // check that the txn id doesn't change
        String txnIdCheck = AlfrescoTransactionManager.getTransactionId();
        assertEquals("Transaction ID changed on same thread", txnId, txnIdCheck);
        
        // commit
        txn.commit();
        assertNull("Thread shouldn't have a txn ID after commit", AlfrescoTransactionManager.getTransactionId());
        
        // start a new transaction
        txn = serviceRegistry.getUserTransaction();
        txn.begin();
        txnIdCheck = AlfrescoTransactionManager.getTransactionId();
        assertNotSame("New transaction has same ID", txnId, txnIdCheck);
        
        // rollback
        txn.rollback();
        assertNull("Thread shouldn't have a txn ID after rollback", AlfrescoTransactionManager.getTransactionId());
    }
}
