package org.alfresco.module.org_alfresco_module_cloud.aos;

import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.webdav.CloudWebDAVHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.util.Pair;

public class CloudAosWebDAVHelper extends CloudWebDAVHelper
{
    
    @Override
    public FileInfo getNodeForPath(final NodeRef rootNodeRef, String path) throws FileNotFoundException
    {
        if (rootNodeRef == null)
        {
            throw new IllegalArgumentException("Root node may not be null");
        }
        else if (path == null)
        {
            throw new IllegalArgumentException("Path may not be null");
        }
        
        FileInfo fileInfo = null;
        
        // Check for the root path
        if ( path.length() == 0 || path.equals(PathSeperator))
        {
            return super.getNodeForPath(rootNodeRef, path);
        }
        else
        {
            Pair<String, List<String>> tenantAndSplitPath = getTenantAndSplitPath(path);
            List<String> splitPath = tenantAndSplitPath.getSecond();
            
            // Check for the root path
            if (splitPath.size() == 0)
            {
                fileInfo = getFileFolderService().getFileInfo(rootNodeRef);
            }
            else
            {
                splitPath.add(1, SiteService.DOCUMENT_LIBRARY);
                fileInfo = getFileFolderService().resolveNamePath(rootNodeRef, splitPath);
            }
        }
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Fetched node for path: \n" +
                    "   root: " + rootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   result: " + fileInfo);
        }
        
        return fileInfo;
    }

}
