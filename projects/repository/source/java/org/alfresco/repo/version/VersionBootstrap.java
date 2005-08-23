package org.alfresco.repo.version;

import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.transaction.TransactionService;

/**
 * Bootstrap Version Store
 * 
 * @author David Caruana
 */
public class VersionBootstrap
{
    private TransactionService transactionService;
    private NodeService nodeService;
    
    
    /**
     * Sets the Transaction Service
     * 
     * @param userTransaction the user transaction
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Sets the Node Service
     * 
     * @param nodeService the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Bootstrap the Version Store
     */
    public void bootstrap()
    {
        UserTransaction userTransaction = transactionService.getUserTransaction();
        
        try
        {
            userTransaction.begin();

            //  Ensure that the version store has been created
            if (this.nodeService.exists(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, VersionStoreConst.STORE_ID)) == true)
            {
                userTransaction.rollback();
            }
            else
            {
                this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, VersionStoreConst.STORE_ID);
                userTransaction.commit();
            }
        }
        catch(Throwable e)
        {
            // rollback the transaction
            try { if (userTransaction != null) {userTransaction.rollback();} } catch (Exception ex) {}
            throw new AlfrescoRuntimeException("Bootstrap failed", e);
        }            
    }
    
}
