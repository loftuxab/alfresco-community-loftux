package org.alfresco.module.org_alfresco_module_cloud.aos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.enterprise.repo.officeservices.service.Const;
import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.util.Pair;

import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.vfs.VFSNode;

public class AlfrescoCloudVirtualFileSystem extends AlfrescoVirtualFileSystem
{

    private String HISTORY_PATH_PREFIX = Const.HISTORY_PATH_ELEMENT + "/";

    private String NODEID_PATH_PREFIX = Const.NODEID_PATH_ELEMENT + "/";

    @Override
    public VFSNode getNodeByPath(UserData userData, String path, int callContext) throws AuthenticationRequiredException
    {
        path = normalizePath(path);

        if(path.startsWith(HISTORY_PATH_PREFIX))
        {
            return getNodeByHistoryPath(path);
        }

        setTenantFromPath(path);

        if(path.startsWith(NODEID_PATH_PREFIX))
        {
            return getNodeByNodeidPath(path);
        }

        FileInfo fileInfo = getFileInfoByRepositoryPath(path);
        if(fileInfo == null)
        {
            return null;
        }
        return convertFileInfo(fileInfo, path);
    }
    
    private void setTenantFromPath(String path)
    {
        if ( path.length() == 0 || path.equals(WebDAVHelper.PathSeperator))
        {
            return;
        }

        if(path.startsWith(NODEID_PATH_PREFIX))
        {
           path = path.substring(NODEID_PATH_PREFIX.length());
        }

        Pair<String, List<String>> tenantAndSplitPath = getTenantAndSplitPath(path);
        String tenant = tenantAndSplitPath.getFirst();
        if(CloudAosFilter.isCurrentThreadGuarded())
        {
            TenantContextHolder.setTenantDomain(tenant);
        }
    }

    protected VFSNode getNodeByHistoryPath(String path)
    {
        String remaining = path.substring(HISTORY_PATH_PREFIX.length());
        int firstSeparator = remaining.indexOf('/');
        if(firstSeparator < 1)
        {
            return null;
        }
        String versionLabel = remaining.substring(0, firstSeparator);
        String originalPath = remaining.substring(firstSeparator);
        setTenantFromPath(originalPath);
        
        FileInfo liveNode = originalPath.startsWith(NODEID_PATH_PREFIX) ? getFileInfoByNodeidPath(originalPath) : getFileInfoByRepositoryPath(originalPath);
        if(liveNode == null)
        {
            return null;
        }
        if(liveNode.isFolder())
        {
            return null;
        }
        
        VersionHistory versionHistory = getVersionService().getVersionHistory(liveNode.getNodeRef());
        if(versionHistory == null)
        {
            if(liveNode.isLink())
            {
                return null;
            }
            return new DocumentNode(liveNode,path,this,true);
        }
        else
        {
            for (Version version : versionHistory.getAllVersions())
            {
                if(versionLabel.equals(version.getVersionLabel()))
                {
                    FileInfo frozen = getFileFolderService().getFileInfo(version.getFrozenStateNodeRef());
                    return new DocumentNode(frozen,path,this,true);
                }
            }
            return null;
        }
    }
	
    protected FileInfo getFileInfoByNodeidPath(String path)
    {
        String remaining = path.substring(NODEID_PATH_PREFIX.length());
        String[] pathElements = remaining.split("/");
        if( (pathElements.length != 3) || (pathElements[1].length() <= 0) || (pathElements[1].length() <= 1) )
        {
            return null;
        }
        NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, pathElements[1]);
        try
        {
            return getFileFolderService().getFileInfo(nodeRef);
        }
        catch(InvalidNodeRefException inre)
        {
            return null;
        }
    }	
    
    protected static Pair<String, List<String>> getTenantAndSplitPath(String path)
    {
        final List<String> splitPath = splitAllPaths(path);
        
        String tenantDomain = TenantService.DEFAULT_DOMAIN;
        if (splitPath.size() > 0)
        {
            tenantDomain = splitPath.get(0);
            if (tenantDomain.equals(TenantUtil.SYSTEM_TENANT))
            {
                tenantDomain = TenantService.DEFAULT_DOMAIN;
            }
            splitPath.remove(0);
        }
        
        return new Pair<String,List<String>>(tenantDomain, splitPath);
    }

    protected static List<String> splitAllPaths(String path)
    {
        if (path == null || path.length() == 0)
        {
            return Collections.emptyList();
        }

        // split the path
        StringTokenizer token = new StringTokenizer(path, WebDAVHelper.PathSeperator);
        List<String> results = new ArrayList<String>(10);
        while (token.hasMoreTokens())
        {
            results.add(token.nextToken());
        }
        return results;
    }
    
    @Override
    protected VFSNode factoryDocumentNode(FileInfo fileInfo, String path)
    {
        return new CloudDocumentNode(fileInfo,path,this);
    }

}
