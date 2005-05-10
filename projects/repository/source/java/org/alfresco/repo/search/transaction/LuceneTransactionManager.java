/*
 * Created on 12-Apr-2005
 */
package org.alfresco.repo.search.transaction;

import org.alfresco.repo.search.IndexerException;
import org.alfresco.repo.search.impl.lucene.LuceneIndexerAndSearcherFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * This Transaction Manager is intended to wrap one of the existing spring
 * transaction managers with support for transactions around the lucene index.
 * The intention is to keep this simple and avoid JTA overhead.
 * 
 * This does not have full recovery support at the moment but could have. After
 * the lucene indexer prepare we can only fail on IO errors.
 * 
 * TODO: We should track required deletions and the delta to commit and then we
 * can retry database commits that went through followed by lucene index commits
 * that failed. These will be serialised so could be retied on recovery and
 * block any further index action. Could persist a lock in a manager or to disk.
 * The lock manager could recover on start up etc. This will be required for
 * true JTA in any case.
 * 
 * @author andyh
 * 
 */
public class LuceneTransactionManager extends HibernateTransactionManager
{
    private LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory;

    public LuceneTransactionManager()
    {
        super();
    }

    public void setLuceneIndexerAndSearchFactory(LuceneIndexerAndSearcherFactory luceneIndexerAndSearcherFactory)
    {
        this.luceneIndexerAndSearcherFactory = luceneIndexerAndSearcherFactory;
    }

    protected void doCommit(DefaultTransactionStatus status) throws TransactionException
    {
        try
        {
            // TODO: The following call should mark for recovery - it does not
            luceneIndexerAndSearcherFactory.prepare();
        }
        catch (IndexerException e)
        {
            doRollback(status);
            throw new LuceneTransactionException("Lucene index transaction failed to prepare");
        }
        try
        {
            super.doCommit(status);
        }
        catch (TransactionException e)
        {
            luceneIndexerAndSearcherFactory.rollback();
            throw new LuceneTransactionException("Failed transaction manager commit");
        }

        // If the followiung commit fails it willclean up
        luceneIndexerAndSearcherFactory.commit();
    }

    protected void doRollback(DefaultTransactionStatus status) throws TransactionException
    {
        try
        {
            super.doRollback(status);
        }
        finally
        {
            luceneIndexerAndSearcherFactory.rollback();
        }
    }

}
