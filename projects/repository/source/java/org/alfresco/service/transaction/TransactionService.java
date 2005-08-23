package org.alfresco.service.transaction;

import javax.transaction.UserTransaction;

/**
 * Contract for retrieving access to a transaction
 * 
 * @author David Caruana
 *
 */
public interface TransactionService
{
    /**
     * Gets a user transaction
     * 
     * @return the user transaction
     */
    UserTransaction getUserTransaction();
}
