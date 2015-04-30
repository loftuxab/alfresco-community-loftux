package org.alfresco.module.org_alfresco_module_cloud.aos;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;

import com.xaldon.officeservices.protocol.VermeerRequest;

public class CloudDocumentNode extends DocumentNode
{

    public CloudDocumentNode(FileInfo info, String path, AlfrescoVirtualFileSystem vfs, boolean isHistoryNode)
    {
        super(info, path, vfs, isHistoryNode);
    }

    public CloudDocumentNode(FileInfo info, String path, AlfrescoVirtualFileSystem vfs)
    {
        this(info, path, vfs, false);
    }
    
    @Override
    public boolean storeContent(VermeerRequest vermeerRequest, int callContext)
    {
        return storeContent(vermeerRequest.getAttachedFileInputStream(), callContext);
    }

    @Override
    public boolean storeContent(InputStream content, int callContext)
    {
        // detect if we have content
        PushbackInputStream internalContent = new PushbackInputStream(content, 1);
        boolean hasContent = false;
        try
        {
        	byte[] tempBuffer = new byte[1];
        	int bytesRead = internalContent.read(tempBuffer);
        	if(bytesRead == 1)
        	{
            	hasContent = true;
            	internalContent.unread(tempBuffer, 0, 1);
        	}
        }
        catch(IOException ioe)
        {
        	logger.error("Error accessing content stream", ioe);
        	return false;
        }
        NodeRef nodeToRead = fileInfo.getNodeRef();
        if(isCheckedOut())
        {
            nodeToRead = vfs.getCheckOutCheckInService().getWorkingCopy(nodeToRead);
        }
        boolean hadContentPropertyBeforeUpdate = false;
        try
        {
        	hadContentPropertyBeforeUpdate = vfs.getNodeService().getProperty(nodeToRead, ContentModel.PROP_CONTENT) != null;
        }
        catch(Exception e)
        {
            hadContentPropertyBeforeUpdate = true;
        }
        boolean result = super.storeContent(internalContent, callContext);
        if(result)
        {
            // Don't raise analytics events for hidden files, resource forks etc.
            if ((!fileInfo.isHidden()) && hasContent)
            {
                ContentReader reader = vfs.getFileFolderService().getReader(nodeToRead);
                String mimeType = (reader==null) ? "application/octest-stream" : reader.getMimetype();
                long fileSize = (reader==null) ? 0 : reader.getSize();
                Analytics.record_UploadDocument(mimeType, fileSize, hadContentPropertyBeforeUpdate, "aos");
            }            
        }
        return result;
    }

}
