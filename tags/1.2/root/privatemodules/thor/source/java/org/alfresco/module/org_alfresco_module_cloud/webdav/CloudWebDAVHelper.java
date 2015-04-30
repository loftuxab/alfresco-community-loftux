/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.module.org_alfresco_module_cloud.webdav;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVMethod;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

/**
 * @author janv
 * @since Thor
 */
public class CloudWebDAVHelper extends WebDAVHelper
{
    private DirectoryService directoryService;
    private AccountService accountService;
    private boolean premiumAccountsOnly;
    
    private final static String NETWORKS_FOLDER_MARKER = "<<Networks>>";
    // Document Library
    public static final int DOCLIB_PATH_ELEMENT_INDEX = 2;
    
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }

    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }

    public DirectoryService getDirectoryService()
    {
        return directoryService;
    }
    
    public AccountService getAccountService()
    {
        return accountService;
    }
    
    public void setPremiumAccountsOnly(boolean premiumAccountsOnly)
    {
        this.premiumAccountsOnly = premiumAccountsOnly;
    }

    public boolean isPremiumAccountsOnly()
    {
        return premiumAccountsOnly;
    }
    
    @Override
    public String getURLForPath(HttpServletRequest request, String path, boolean isFolder, String userAgent)
    {
        String urlPathPrefix = getUrlPathPrefix(request);
        StringBuilder urlStr = new StringBuilder(urlPathPrefix);
        
        if (!path.equals(WebDAV.RootPath))
        {
            // split the path and URL encode each path element
            int elementIndex = 0;
            for (StringTokenizer t = new StringTokenizer(path, PathSeperator); t.hasMoreTokens(); elementIndex++)
            {
                String pathElement = t.nextToken();
                if (!pathElementIsDocumentLibrary(elementIndex, pathElement))
                {                    
                    urlStr.append(WebDAVHelper.encodeURL(pathElement, userAgent));
                    if (t.hasMoreTokens())
                    {
                        urlStr.append(PathSeperator);
                    }
                }
            }
        }
        
        // If the URL is to a collection add a trailing slash
        if (isFolder && urlStr.charAt(urlStr.length() - 1) != PathSeperatorChar)
        {
            urlStr.append(PathSeperator);
        }
        
        logger.debug("getURLForPath() path:" + path + " => url:" + urlStr);
        
        // Return the URL string
        return urlStr.toString();
    }

    /**
     * Is the path element with the given text and index the documentLibrary?
     * 
     * @param elementIndex
     * @param pathElement
     * @return true if the path element is the document library.
     */
    private static boolean pathElementIsDocumentLibrary(int elementIndex, String pathElement)
    {
        return elementIndex == DOCLIB_PATH_ELEMENT_INDEX && pathElement.equals(SiteService.DOCUMENT_LIBRARY);
    }
    
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
            fileInfo = getDummyFolder(NETWORKS_FOLDER_MARKER);
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
    
    /**
     * Given a tenant-prefixed path, will return a list of path elements within the tenant
     * (i.e. the tenant is stripped before splitting the path). 
     */
    @Override
    public List<String> splitAllPaths(String path)
    {
        return getTenantAndSplitPath(path).getSecond();
    }
    
    /**
     * Break down path into path elements, as per {@link WebDAVHelper#splitAllPaths(String)}.
     * 
     * @param path
     * @return tokenized path
     */
    public List<String> tokenizePath(String path)
    {
        return super.splitAllPaths(path);
    }
    
    public Pair<String, List<String>> getTenantAndSplitPath(String path)
    {
        final List<String> splitPath = super.splitAllPaths(path);
        
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
    
    @Override
    public List<FileInfo> getChildren(FileInfo folderInfo) throws WebDAVServerException
    {
        List<FileInfo> ret = null;
        
        if (! ((folderInfo.getNodeRef() == null) && (folderInfo.getName().equals(NETWORKS_FOLDER_MARKER))))
        {
            ret = super.getChildren(folderInfo);
        }
        else
        {
            // list networks
            List<Long> accountIds = directoryService.getAllAccounts(AuthenticationUtil.getRunAsUser());
            
            if (accountIds.size() == 0)
            {
                throw new WebDAVServerException(HttpServletResponse.SC_FORBIDDEN);
            }
            else
            {
                Map<String, FileInfo> networks = new TreeMap<String, FileInfo>();
                
                for (final Long accountId : accountIds)
                {
                    final String tenantDomain = accountService.getAccountTenant(accountId);
                    networks.put(tenantDomain, getDummyFolder(tenantDomain));
                }
                
                ret = new ArrayList<FileInfo>();
                ret.addAll(networks.values());
            }
        }
        
        return ret;
    }
    
    private FileInfo getDummyFolder(final String name)
    {
        return new FileInfo()
        {
            private static final long serialVersionUID = 4771609938495569303L;
            private Date dummyDate = new Date();
            
            public NodeRef getNodeRef() { return null; }
            public boolean isFolder() { return true; }
            public boolean isLink() { return false; }
            public boolean isHidden() { return false; }
            public NodeRef getLinkNodeRef() { return null; }
            public String getName() { return name; }
            public Date getCreatedDate() { return dummyDate; }
            public Date getModifiedDate() { return dummyDate; }
            public ContentData getContentData() { return null; }
            public Map<QName, Serializable> getProperties() { return Collections.emptyMap(); }
            public QName getType() { return ContentModel.TYPE_FOLDER; }
            public String toString()
            {
                // for debug
                StringBuilder sb = new StringBuilder(80);
                sb.append("FileInfo")
                  .append("[name=").append(getName())
                  .append(", isFolder=").append(isFolder())
                  .append(", nodeRef=").append(getNodeRef())
                  .append("]");
                return sb.toString();
            }
        };
    }
    
    @Override
    public String determineTenantDomain(WebDAVMethod method)
    {
        String path = method.getPath();
        Pair<String, List<String>> tenantAndSplitPath = getTenantAndSplitPath(path);
        String tenantDomain = tenantAndSplitPath.getFirst();
        return tenantDomain;
    }

    @Override
    public String getDestinationPath(String contextPath, String servletPath, String destURL)
    {
        String destPath = super.getDestinationPath(contextPath, servletPath, destURL);

        Pair<String, List<String>> tenantAndPath = getTenantAndSplitPath(destPath);
        String destTenant = tenantAndPath.getFirst();
        List<String> destSplitPath = tenantAndPath.getSecond();
        checkDestinationTenant(destTenant);
        destPath = buildPathInsertingDocLib(destTenant, destSplitPath);
        return destPath;
    }

    /**
     * Ensure the destination tenant (in a MOVE or COPY) is the same as the source tenant,
     * since inter-tenant moves/copies are not supported.
     * 
     * @param splitPath
     */
    private void checkDestinationTenant(String destTenant)
    {            
        String userTenant = getTenantService().getCurrentUserDomain();
        if (!userTenant.equals(destTenant))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Disallowing request, source tenant=" + userTenant +
                            ", destination tenant=" + destTenant);
            }
            throw new RuntimeException(new WebDAVServerException(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
        }
    }

    /**
     * Given a path of the form /tenant-domain/site-name/rest-of-path
     * this method inserts the documentLibrary path element at the
     * correct position, to give /tenant-domain/site-name/documentLibrary/rest-of-path
     * <p>
     * Note, this is a crude method that assumes the first two elements are
     * tenant and site name.
     * 
     * @param path Path that doesn't include the documentLibrary.
     * @return New path including documentLibrary element.
     */
    public String insertDocLibPathElement(String path)
    {
        Pair<String, List<String>> tenantAndPath = getTenantAndSplitPath(path);
        return buildPathInsertingDocLib(tenantAndPath.getFirst(), tenantAndPath.getSecond());
    }

    /**
     * Build a WebDAV path given a tenant and remaining path elements. The remaining path elements
     * should have the site name as the first element (for splitPath.size() &gt; 0).
     * 
     * @param tenant The tenant domain to include in the path.
     * @param splitPath The remaining path elements.
     * @return
     */
    protected String buildPathInsertingDocLib(String tenant, List<String> splitPath)
    {
        StringBuilder path = new StringBuilder(100);
        
        // The first element will be the tenant
        splitPath.add(0, tenant);
        
        // Insert the documentLibrary path element where required.
        if (splitPath.size() >= DOCLIB_PATH_ELEMENT_INDEX)
        {
            splitPath.add(DOCLIB_PATH_ELEMENT_INDEX, SiteService.DOCUMENT_LIBRARY);
        }
        
        // Rebuild the path...
        for (int i = 0; i < splitPath.size(); i++)
        {
            // Each element is separated from the next
            path.append(WebDAV.PathSeperator);
            String pathElement = splitPath.get(i);
            path.append(pathElement);
        }
        return path.toString();
    }
}
