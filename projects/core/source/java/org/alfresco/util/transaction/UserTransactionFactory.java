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
package org.alfresco.util.transaction;

import javax.transaction.UserTransaction;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Factory of <code>UserTransaction</code> instances.
 * 
 * @author Derek Hulley
 */
public class UserTransactionFactory extends AbstractFactoryBean
{
    private PlatformTransactionManager transactionManager;
    
    public UserTransactionFactory(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    /**
     * @return Returns the class of <code>UserTransaction</code>
     */
    public Class getObjectType()
    {
        return UserTransaction.class;
    }

    protected Object createInstance() throws Exception
    {
        return new SpringAwareUserTransaction(transactionManager);
    }
}
