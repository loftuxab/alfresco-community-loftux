package org.alfresco.module.vti.web.fp;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.URLDecoder;


/**
 * Implements the WebDAV MKCOL method with VTI specific behaviours
 */
public class MkcolMethod extends org.alfresco.repo.webdav.MkcolMethod
{
    private String alfrescoContext;
    private VtiPathHelper pathHelper;

    public MkcolMethod(VtiPathHelper pathHelper)
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
        FileInfo nodeInfo = pathHelper.resolvePathFileInfo(URLDecoder.decode(path));
        if (nodeInfo == null)
        {
        	throw new FileNotFoundException(path);
        }
        FileInfo workingCopy = getWorkingCopy(nodeInfo.getNodeRef());
        return workingCopy != null ? workingCopy : nodeInfo;
    }
    
    /**
     * Returns the path, excluding the Servlet Context (if present)
     * @see org.alfresco.repo.webdav.WebDAVMethod#getPath()
     */
    @Override
    public String getPath()
    {
       return AbstractMethod.getPathWithoutContext(alfrescoContext, m_request);
    }
}
