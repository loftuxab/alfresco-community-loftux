/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.enterprise.repo.content.cryptodoc.KeyEncryptedKeyProcessor;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyReference;
import org.alfresco.repo.batch.BatchProcessWorkProvider;
import org.alfresco.repo.batch.BatchProcessor;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author sglover
 *
 */
public class SymmetricKeyReencryptor
{
    private static final QName LOCK = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "SymmetricKeyReencryptor");

    private static Log logger = LogFactory.getLog(SymmetricKeyReencryptor.class);

    private int symmetricKeyReEncryptionNumthreads = 2;
    private int symmetricKeyReEncryptionBatchSize = 100;

    private ApplicationContext applicationContext;
    private TransactionService transactionService;
    private ContentDataDAO contentDataDAO;
    private KeyEncryptedKeyProcessor keyEncryptedKeyProcessor;
    private JobLockService jobLockService;
    private Executor executor;

    public void setKeyEncryptedKeyProcessor(KeyEncryptedKeyProcessor keyEncryptedKeyProcessor)
    {
        this.keyEncryptedKeyProcessor = keyEncryptedKeyProcessor;
    }

    public void setSymmetricKeyReEncryptionNumthreads(int symmetricKeyReEncryptionNumthreads)
    {
        this.symmetricKeyReEncryptionNumthreads = symmetricKeyReEncryptionNumthreads;
    }

    public void setSymmetricKeyReEncryptionBatchSize(int symmetricKeyReEncryptionBatchSize)
    {
        this.symmetricKeyReEncryptionBatchSize = symmetricKeyReEncryptionBatchSize;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setContentDataDAO(ContentDataDAO contentDataDAO)
    {
        this.contentDataDAO = contentDataDAO;
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }

    public void init()
    {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Attempts to get the lock. If the lock couldn't be taken, then <tt>null</tt> is returned.
     * 
     * @return Returns the lock token or <tt>null</tt>
     */
    private String getLock(long time)
    {
        try
        {
            return jobLockService.getLock(LOCK, time);
        }
        catch (LockAcquisitionException e)
        {
            return null;
        }
    }

    /**
     * Attempts to get the lock. If it fails, the current transaction is marked for rollback.
     * 
     * @return Returns the lock token
     */
    private void refreshLock(String lockToken, long time)
    {
        if (lockToken == null)
        {
            throw new IllegalArgumentException("Must provide existing lockToken");
        }
        jobLockService.refreshLock(lockToken, LOCK, time);
    }

    public void reEncryptSymmetricKeys(final KeyReference masterKeyRef, final Key masterDecryptionKey)
    {
        // run in a separate thread because the batch processor will not return until
        // all work has been given to the processing threads.
        executor.execute(new Runnable()
        {
            public void run()
            {
                final String masterKeyAlias = masterKeyRef.getAlias();

                // Take out a re-encryptor lock
                RetryingTransactionCallback<String> txnWork = new RetryingTransactionCallback<String>()
                {
                    public String execute() throws Exception
                    {
                        String lockToken = getLock(20000L);
                        return lockToken;
                    }
                };

                final String lockToken = transactionService.getRetryingTransactionHelper().doInTransaction(txnWork, false, true);
                if(lockToken == null)
                {
                    logger.warn("Can't get lock. Assume multiple re-encryptors ...");
                    return;
                }

                // provider provides symmetric keys that have been encrypted with masterKeyRef

                final long count = contentDataDAO.countSymmetricKeysForMasterKeyAlias(masterKeyAlias);

                BatchProcessWorkProvider<ContentUrlKeyEntity> provider = new BatchProcessWorkProvider<ContentUrlKeyEntity>()
                {
                    private long fromId = 0;

                    @Override
                    public int getTotalEstimatedWorkSize()
                    {
                        return (int)count;
                    }

                    @Override
                    public Collection<ContentUrlKeyEntity> getNextWork()
                    {
                        List<ContentUrlKeyEntity> results = contentDataDAO.getSymmetricKeysByMasterKeyAlias(masterKeyAlias,
                                fromId, symmetricKeyReEncryptionBatchSize);

                        if(results.size() > 0)
                        {
                            ContentUrlKeyEntity last = results.get(results.size() - 1);
                            fromId = last.getId();
                        }

                        return results;
                    }
                };

                final AtomicInteger counter = new AtomicInteger(0);
                BatchProcessor.BatchProcessWorker<ContentUrlKeyEntity> worker = new BatchProcessor.BatchProcessWorker<ContentUrlKeyEntity>()
                {
                    public String getIdentifier(ContentUrlKeyEntity entity)
                    {
                        return String.valueOf(entity.getId());
                    }

                    public void beforeProcess() throws Throwable
                    {
                        refreshLock(lockToken, symmetricKeyReEncryptionBatchSize * 100L);
                    }

                    public void afterProcess() throws Throwable
                    {
                    }

                    public void process(final ContentUrlKeyEntity contentUrlKey) throws Throwable
                    {
                        if(keyEncryptedKeyProcessor.reencryptSymmetricKey(contentUrlKey))
                        {
                            counter.incrementAndGet();
                        }
                    }
                };

                BatchProcessor<ContentUrlKeyEntity> bp = new BatchProcessor<ContentUrlKeyEntity>(
                        "Content Url Key Reencryptor",
                        transactionService.getRetryingTransactionHelper(),
                        provider,
                        symmetricKeyReEncryptionNumthreads, symmetricKeyReEncryptionBatchSize,
                        applicationContext,
                        logger, 100);
                bp.process(worker, true);

                final long countAfter = contentDataDAO.countSymmetricKeysForMasterKeyAlias(masterKeyAlias);
                logger.debug("Count after = " + countAfter);
            }
        });
    }
}
