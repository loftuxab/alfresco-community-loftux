/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.vti.handler.alfresco;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.AbstractLifecycleBean;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;

/**
 * Helper for AlfrescoVtiMethodHandler. Help for path resolving and url formatting
 *
 * @author Dmitry Lazurkin
 *
 */
public class VtiPathHelper extends AbstractLifecycleBean
{
    private final static Log logger = LogFactory.getLog("org.alfresco.module.vti.handler");

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private PermissionService permissionService;
    private SearchService searchService;
    private NamespaceService namespaceService;

    private AuthenticationComponent authenticationComponent;

    private NodeRef rootNodeRef;

    private String rootPath;
    private String storePath;

    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setRootPath(String rootPath)
    {
        this.rootPath = rootPath;
    }

    public void setStorePath(String storePath)
    {
        this.storePath = storePath;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Resolve file info for file with URL path
     *
     * @param initialURL URL path
     * @return file info null if file or folder doesn't exist
     */
    public FileInfo resolvePathFileInfo(String initialURL)
    {
        FileInfo fileInfo = null;

        if (initialURL.length() == 0)
        {
            fileInfo = fileFolderService.getFileInfo(rootNodeRef);
        }
        else
        {
            try
            {
                List<String> splitPath = Arrays.asList(initialURL.split("/"));
                fileInfo = fileFolderService.resolveNamePath(rootNodeRef, splitPath);
            }
            catch (FileNotFoundException e)
            {
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Resolved file info for '" + initialURL + "' is " + fileInfo);
        }

        return fileInfo;
    }

    /**
     * Resolves file info for file with URL path in parent directory with file info
     *
     * @param parentFileInfo file info for parent directory
     * @param childName url path relative to parent directory
     * @return resolved file info for childName or null, if it doesn't exist
     */
    public FileInfo resolvePathFileInfo(FileInfo parentFileInfo, String childName)
    {
        FileInfo childFileInfo = null;

        try
        {
            childFileInfo = fileFolderService.resolveNamePath(parentFileInfo.getNodeRef(), Collections.singletonList(childName));
        }
        catch (FileNotFoundException e)
        {
        }

        return childFileInfo;
    }

    /**
     * Split URL path to document name and path to parent folder of that document
     *
     * @param path URL path
     * @return first item of pair - path to parent folder, second item - document name
     */
    public static Pair<String, String> splitPathParentChild(String path)
    {
        int indexOfName = path.lastIndexOf("/");

        String name = path.substring(indexOfName + 1);
        String parent = "";

        if (indexOfName != -1)
        {
            parent = path.substring(0, indexOfName);
        }

        return new Pair<String, String>(parent, name);
    }

    /**
     * Format FrontPageExtension URL path from file information
     *
     * @param fileInfo file information
     * @return URL path
     */
    public String toUrlPath(FileInfo fileInfo)
    {
        String urlPath ;
        if (fileInfo.getNodeRef().equals(rootNodeRef))
        {
            urlPath = "";
        }
        else
        {
            StringBuilder builder = new StringBuilder(nodeService.getPath(fileInfo.getNodeRef()).toDisplayPath(nodeService, permissionService));
            builder.delete(0, nodeService.getPath(rootNodeRef).toDisplayPath(nodeService, permissionService).length() +
                    ((String) nodeService.getProperty(rootNodeRef, ContentModel.PROP_NAME)).length() + 1);
            if (builder.length() != 0)
            {
                builder.deleteCharAt(0);
                builder.append("/");
            }
            builder.append(fileInfo.getName());
            urlPath = builder.toString();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Convert " + fileInfo + " to url path '" + urlPath + "'");
        }

        return urlPath;
    }

	@Override
	protected void onBootstrap(ApplicationEvent event) {
		rootNodeRef = AuthenticationUtil.runAs(new RunAsWork<NodeRef>() 
		{
            public NodeRef doWork() throws Exception
            {
                StoreRef storeRef = new StoreRef(storePath);
                if (nodeService.exists(storeRef) == false)
                {
                    throw new RuntimeException("No store for path: " + storeRef);
                }

                NodeRef storeRootNodeRef = nodeService.getRootNode(storeRef);

                List<NodeRef> nodeRefs = searchService.selectNodes(storeRootNodeRef, rootPath, null, namespaceService, false);

                if (nodeRefs.size() > 1)
                {
                    throw new RuntimeException("Multiple possible roots for : \n" + "   root path: " + rootPath + "\n" + "   results: " + nodeRefs);
                }
                else if (nodeRefs.size() == 0)
                {
                    throw new RuntimeException("No root found for : \n" + "   root path: " + rootPath);
                }
                else
                {
                    return nodeRefs.get(0);
                }
            }
		}, authenticationComponent.getSystemUserName());
	}

	@Override
	protected void onShutdown(ApplicationEvent event) {
		// do nothing
	}

}
