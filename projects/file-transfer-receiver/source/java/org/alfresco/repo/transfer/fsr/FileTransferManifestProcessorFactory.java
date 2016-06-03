package org.alfresco.repo.transfer.fsr;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.transfer.ManifestProcessorFactory;
import org.alfresco.repo.transfer.manifest.TransferManifestProcessor;
import org.alfresco.repo.transfer.requisite.TransferRequsiteWriter;
import org.alfresco.service.cmr.transfer.TransferReceiver;

public class FileTransferManifestProcessorFactory implements ManifestProcessorFactory
{
    /**
     * The requisite processor
     *
     * @param receiver TransferReceiver
     * @param transferId String
     * @param out TransferRequsiteWriter
     * @return the requisite processor
     */
    public TransferManifestProcessor getRequsiteProcessor(
            TransferReceiver receiver,
            String transferId,
            TransferRequsiteWriter out)
    {
        return new FileTransferReceiverRequisiteManifestProcessor(receiver, transferId, out);
    }

    /**
     * The commit processors
     *
     * @param receiver TransferReceiver
     * @param transferId String
     * @return the requisite processor
     */
    public List<TransferManifestProcessor> getCommitProcessors(TransferReceiver receiver, String transferId)
    {
        List<TransferManifestProcessor> processors = new ArrayList<TransferManifestProcessor>();
        
        DbHelper dbHelper = ((FileTransferReceiver)receiver).getDbHelper();
        processors.add(new ManifestProcessorImpl(receiver, transferId, dbHelper));

        return processors;
    }
}
