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
package org.alfresco.opencmis;

import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.repo.tenant.NetworksService;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.FileFilterMode.Client;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.spi.Holder;

/**
 * Override OpenCMIS service object - for cloud network/tenant switching
 * 
 * @author janv
 * @since Alfresco Cloud Module
 */
public class CloudAlfrescoCmisService extends PublicApiAlfrescoCmisService
{
//    private CMISConnector connector;
//    private TenantAdminService tenantAdminService;
//    private NetworksService networksService;
    
    public CloudAlfrescoCmisService(CMISConnector connector, TenantAdminService tenantAdminService, NetworksService networksService)
    {
        super(connector, tenantAdminService, networksService);
//        
//        this.connector = connector;
//        this.networksService = networksService;
//        this.tenantAdminService = tenantAdminService;
    }
    
//    @Override
//    public String create(String repositoryId, Properties properties, String folderId,
//                ContentStream contentStream, VersioningState versioningState,
//                List<String> policies, ExtensionsData extension)
//    {
//        FileFilterMode.setClient(Client.cmis);
//        try
//        {
//            return super.create(
//                        repositoryId,
//                        properties,
//                        folderId,
//                        contentStream,
//                        versioningState,
//                        policies,
//                        extension);
//        }
//        finally
//        {
//            FileFilterMode.clearClient();
//        }
//    }

    /**
     * Overridden to capture content upload for publishing to analytics service.
     */
    @Override
    public String createDocument(String repositoryId, Properties properties, String folderId,
                ContentStream contentStream, VersioningState versioningState,
                List<String> policies, Acl addAces, Acl removeAces, ExtensionsData extension)
    {
        String newId = super.createDocument(
                    repositoryId,
                    properties,
                    folderId,
                    contentStream,
                    versioningState,
                    policies,
                    addAces,
                    removeAces,
                    extension);
        if (contentStream != null)
        {
            recordAnalyticsUpload(newId, false);
        }
        return newId;
    }

    /**
     * Overridden to capture content upload for publishing to analytics service.
     */
    @Override
    public void setContentStream(String repositoryId, Holder<String> objectId,
                Boolean overwriteFlag, Holder<String> changeToken, ContentStream contentStream,
                ExtensionsData extension)
    {
        FileFilterMode.setClient(Client.cmis);
        try
        {
            super.setContentStream(repositoryId, objectId, overwriteFlag, changeToken, contentStream, extension);
            recordAnalyticsUpload(objectId.getValue(), overwriteFlag);
        }
        finally
        {
            FileFilterMode.clearClient();
        }
    }

    /**
     * Emit an analytics event for a CMIS upload.
     * 
     * @param newId
     */
    private void recordAnalyticsUpload(String newId, boolean fileModified)
    {
        if (newId != null)
        {
            FileInfo fileInfo = getFileInfo(newId);
            // Don't raise analytics events for hidden files, resource forks etc.
            if (!fileInfo.isHidden())
            {
                ContentData contentData = fileInfo.getContentData();
                String mimeType = contentData.getMimetype();
                long fileSize = contentData.getSize();
                Analytics.record_UploadDocument(mimeType, fileSize, fileModified);            
            }
        }
    }

    /**
     * @param cmisObjectId
     * @return
     */
    private FileInfo getFileInfo(String cmisObjectId)
    {
        FileFolderService fileFolderService = connector.getFileFolderService();
        NodeRef nodeRef = getOrCreateNodeInfo(cmisObjectId).getNodeRef();
        FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
        return fileInfo;
    }


//    @Override
//    public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension)
//    {
//    	// for currently authenticated user
//    	PagingResults<Network> networks = networksService.getNetworks(new PagingRequest(0, Integer.MAX_VALUE));
//    	List<Network> page = networks.getPage();
//        final List<RepositoryInfo> repoInfos = new ArrayList<RepositoryInfo>(page.size());
//        for (Network network : page)
//        {
//            repoInfos.add(getRepositoryInfo(network));
//        }
//        return repoInfos;
//    }
    
//    @Override
//    public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension)
//    {
//        checkRepositoryId(repositoryId);
//        
//        return getRepositoryInfo(repositoryId);
//    }
    
//    private RepositoryInfo getRepositoryInfo(final Network network)
//    {
//    	final String networkId = network.getTenantDomain();
//    	final String tenantDomain = networkId.equals(TenantUtil.SYSTEM_TENANT) ? TenantService.DEFAULT_DOMAIN : networkId;
//
//        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<RepositoryInfo>()
//        {
//            public RepositoryInfo doWork()
//            {
//                RepositoryInfoImpl repoInfo = (RepositoryInfoImpl)connector.getRepositoryInfo();
//
//                repoInfo.setId(!networkId.equals("") ? networkId : TenantUtil.SYSTEM_TENANT);
//                repoInfo.setName(tenantDomain);
//                repoInfo.setDescription(tenantDomain);
//                
//                return repoInfo;
//            }
//        }, tenantDomain);
//    }
    
//    @Override
//    public void checkRepositoryId(String repositoryId)
//    {
//        String tenantDomain = repositoryId;
//        
//        if (tenantDomain.equals(TenantUtil.SYSTEM_TENANT))
//        {
//            // TODO check for super admin
//            return;
//        }
//        
//        if (! tenantAdminService.isEnabledTenant(tenantDomain))
//        {
//            throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
//        }
//    }
    
//    @Override
//    public void beforeCall()
//    {
//        // NOTE: Don't invoke super beforeCall to exclude authentication which is already supported by
//        //       Web Script F/W
//        //super.beforeCall();
//        
//        CallContext context = getContext();
//        if (context != null)
//        {
//            String tenantDomain = context.getRepositoryId();
//            if ((tenantDomain == null) || (tenantDomain.equals(TenantUtil.SYSTEM_TENANT)))
//            {
//                tenantDomain = TenantService.DEFAULT_DOMAIN;
//            }
//            
//            TenantContextHolder.setTenantDomain(tenantDomain);
//        }
//    }
//    
//    @Override
//    public void afterCall()
//    {
//        // NOTE: Don't invoke super afterCall to exclude authentication which is already supported by
//        //       Web Script F/W
//        //super.afterCall();
//    }
//    
//    @Override
//    public void close()
//    {
//    	super.close();
//    }
}
