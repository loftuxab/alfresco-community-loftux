/*
 * Created on 12-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.transaction;

import org.springframework.transaction.TransactionException;

public class LuceneTransactionException extends TransactionException
{

    /**
     * 
     */
    private static final long serialVersionUID = 3978985453464335925L;

    public LuceneTransactionException(String arg0)
    {
        super(arg0);
    }

    public LuceneTransactionException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

}
