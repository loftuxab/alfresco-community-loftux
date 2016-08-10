/*
 * #%L
 * Alfresco File Transfer Receiver
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
