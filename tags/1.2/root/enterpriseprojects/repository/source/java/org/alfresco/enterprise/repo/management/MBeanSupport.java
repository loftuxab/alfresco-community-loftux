/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport.TxnReadState;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A base class for MBeans that require the Alfresco thread and transactional contexts to be set appropriately.
 * 
 * @author dward
 */
public class MBeanSupport
{
    protected Log getLogger()
    {
        return LogFactory.getLog(getClass());
    }

    /**
     * Default constructor.
     */
    public MBeanSupport()
    {
    }

    /**
     * Creates an MBeanSupport with the supplied transaction service.
     * 
     * @param transactionService
     *            the transaction service
     */
    public MBeanSupport(TransactionService transactionService)
    {
        setTransactionService(transactionService);
    }

    /** Stores the {@link ClassLoader} to use for invocations. Defaults to the current thread {@link ClassLoader}. */
    private ClassLoader managedResourceClassLoader = Thread.currentThread().getContextClassLoader();

    /** The transaction service. */
    private TransactionService transactionService;

    /**
     * Sets the transaction service.
     * 
     * @param transactionService
     *            the transactionService to set
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public TransactionService getTransactionService()
    {
        return transactionService;
    }

    /**
     * Checks if the repository is read only.
     * 
     * @return true, if the repository is read only
     */
    protected boolean isReadOnly()
    {
        return AuthenticationUtil.runAs(new RunAsWork<Boolean>()
        {

            public Boolean doWork() throws Exception
            {
                return MBeanSupport.this.transactionService.isReadOnly();
            }
        }, AuthenticationUtil.getSystemUserName());
    }

    /**
     * Do some work in the context of an Alfresco transaction, authenticated as the system user.
     * 
     * @param callback
     *            the callback to do the work
     * @param isReadOnly
     *            is a read-only transaction required?
     * @return the result of the callback
     */
    protected <T> T doWork(final RetryingTransactionCallback<T> callback, final boolean isReadOnly)
    {
        // We have to set the context classloader to one that can see all our spring resources
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            return AuthenticationUtil.runAs(new RunAsWork<T>()
            {
                public T doWork() throws Exception
                {
                    if (!isReadOnly && MBeanSupport.this.transactionService.isReadOnly())
                    {
                        throw new IllegalStateException("Change not allowed: Repository is Read Only");
                    }
                    try
                    {
                        boolean readOnly = transactionService.isReadOnly();
                        boolean requiresNew = !readOnly && AlfrescoTransactionSupport.getTransactionReadState() == TxnReadState.TXN_READ_ONLY;
                        return MBeanSupport.this.transactionService.getRetryingTransactionHelper().doInTransaction(
                                callback, isReadOnly() || isReadOnly, requiresNew);
                    }
                    catch (Exception e)
                    {
                        getLogger().error(e);
                        throw e;
                    }
                }
            }, AuthenticationUtil.getSystemUserName());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
