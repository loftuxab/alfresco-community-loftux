package org.alfresco.repo.transfer.fsr;

import java.io.Serializable;

/**
 * 
 * @author Brian
 * 
 */
public interface TransferStatusDAO
{
    TransferStatusEntity createTransferStatus(String transferId, Integer currentPos, Integer endPos, String status,
            Serializable error);

    TransferStatusEntity findByTransferId(String transferId);

    void update(TransferStatusEntity statusEntity);

    void delete(TransferStatusEntity statusEntity);

}
