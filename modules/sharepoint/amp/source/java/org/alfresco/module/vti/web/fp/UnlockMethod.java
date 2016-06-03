package org.alfresco.module.vti.web.fp;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Implements the WebDAV UNLOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class UnlockMethod extends org.alfresco.repo.webdav.UnlockMethod
{
    private String alfrescoContext;
    private VtiPathHelper pathHelper;

    public UnlockMethod(VtiPathHelper pathHelper)
    {
        this.alfrescoContext = pathHelper.getAlfrescoContext();
        this.pathHelper = pathHelper;
    }

    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#getNodeForPath(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    @Override
    protected FileInfo getNodeForPath(NodeRef rootNodeRef, String path) throws FileNotFoundException
    {
        FileInfo nodeInfo = pathHelper.resolvePathFileInfo(path);
        if (nodeInfo == null)
        {
        	throw new FileNotFoundException(path);
        }
        FileInfo workingCopy = getWorkingCopy(nodeInfo.getNodeRef());
        return workingCopy != null ? workingCopy : nodeInfo;
    }
}
