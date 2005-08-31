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

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ParameterCheck;

/**
 * Class containing transactions helper methods and interfaces.
 * 
 * @author Roy Wetherall
 */
public class TransactionUtil
{
	/**
	 * Transaction work interface.
	 * <p>
	 * This interface encapsulates a unit of work that should be done within a transaction.
	 */
	public interface TransactionWork
	{
		/**
		 * Method containing the work to be done in the user transaction.
		 */
		Object doWork();
	}
	
	/**
	 * Execute the transaction work in a user transaction
	 * 
	 * @param transactionService	the transaction service
	 * @param transactionWork		the transaction work
	 * @param ignoreException		indicates whether errors raised in the work are ignored or
	 * 								re-thrown
	 */
	public static Object executeInUserTransaction(
			TransactionService transactionService, 
			TransactionWork transactionWork,
			boolean ignoreException)
	{
		return executeInTransaction(transactionService, transactionWork, ignoreException, false);
	}
	
	/**
	 * Execute the transaction work in a non propigating user transaction
	 * 
	 * @param transactionService	the transaction service
	 * @param transactionWork		the transaction work
	 * @param ignoreException		indicates whether errors raised in the work are ignored or
	 * 								re-thrown
	 */
	public static Object executeInNonPropigatingUserTransaction(
			TransactionService transactionService, 
			TransactionWork transactionWork,
			boolean ignoreException)
	{
		return executeInTransaction(transactionService, transactionWork, ignoreException, true);
	}
	
	/**
	 * Execute the transaction work in a user transaction of a specified type
	 * 
	 * @param transactionService	the transaction service
	 * @param transactionWork		the transaction work
	 * @param ignoreException		indicates whether errors raised in the work are ignored or
	 * 								re-thrown
	 * @param nonPropigatingUserTransaction
	 * 								indicates whether the transaction should be non propigating
	 * 								or not
	 */
	private static Object executeInTransaction(
			TransactionService transactionService, 
			TransactionWork transactionWork,
			boolean ignoreException,
			boolean nonPropigatingUserTransaction)
	{
		ParameterCheck.mandatory("transactionWork", transactionWork);
		
		Object result = null;
		
		// Get the right type of user transaction
		UserTransaction txn = null;
		if (nonPropigatingUserTransaction == true)
		{
			txn = transactionService.getNonPropagatingUserTransaction();
		}
		else
		{
			txn = transactionService.getUserTransaction();
		}
		
		try
		{
			try
			{
				// Beging the transaction, do the work and then commit the transaction
				txn.begin();
				result = transactionWork.doWork();
				txn.commit();
			}
			catch (Throwable exception)
			{
				try
				{
					// Roll back the exception
					if (txn.getStatus() == Status.STATUS_ACTIVE)
					{					
						txn.rollback();
					}
				}
				catch (SystemException systemException)
				{
					// Ignore system exception
				}
				
				// Re-throw the exception (if appropriate)
				if (ignoreException == false)
				{
					if (exception instanceof RuntimeException)
					{
						throw (RuntimeException)exception;
					}
					else
					{
						throw new RuntimeException("Error during execution of transaction.", exception);
					}
				}
			}
		}
		finally
		{
			try
			{
				if (txn.getStatus() == Status.STATUS_ACTIVE)
				{					
					txn.rollback();
				}
			}
			catch (SystemException systemException)
			{
				// Ignore system exception
			}
		}
		
		return result;
	}
}