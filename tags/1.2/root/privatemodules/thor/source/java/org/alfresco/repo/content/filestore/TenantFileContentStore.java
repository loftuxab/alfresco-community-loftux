/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.UnsupportedContentUrlException;
import org.alfresco.repo.domain.tenant.TenantAdminDAO;
import org.alfresco.repo.domain.tenant.TenantEntity;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantRoutingContentStore;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * MT-aware File Content Store
 * 
 * Note: This store mimicks the behaviour of the TenantS3ContentStore, but uses the local file system.
 *       This is used for local development of Thor.
 */
public class TenantFileContentStore extends FileContentStore implements TenantRoutingContentStore
{
    private static final Log logger = LogFactory.getLog(TenantFileContentStore.class);
    
    private File rootDirectory;
    private String rootAbsolutePath;
    private File tenantDirectory;
    private String tenantAbsolutePath;
    private TenantAdminDAO tenantAdminDAO;

    public void setTenantAdminDAO(TenantAdminDAO tenantAdminDAO)
    {
        this.tenantAdminDAO = tenantAdminDAO;
    }

    private TenantFileContentStore(String rootDirectory)
    {
        super(rootDirectory);
        File root = new File(rootDirectory);
        this.rootDirectory = root.getAbsoluteFile();
        this.rootAbsolutePath = root.getAbsolutePath();
        
        String tenantDirectory = rootDirectory + "-tenants";
        File tenant = new File(tenantDirectory);
        if (!tenant.exists())
        {
            if (!tenant.mkdirs())
            {
                throw new ContentIOException("Failed to create store root: " + tenantDirectory, null);
            }
        }
        this.tenantDirectory = tenant.getAbsoluteFile();
        this.tenantAbsolutePath = tenant.getAbsolutePath();
        
        if (logger.isDebugEnabled())
            logger.debug("Root dir: " + rootDirectory + ", Tenant dir: " + tenantDirectory);
    }
    
    @Override
    File createNewFile() throws IOException
    {
        String newUrl = createNewUrl();
        return super.createNewFile(newUrl);
    }

    /*package*/ String makeContentUrl(File file)
    {
        String path = file.getAbsolutePath();
        // check if it belongs to this store
        if (!path.startsWith(rootAbsolutePath))
        {
            throw new AlfrescoRuntimeException(
                    "File does not fall below the store's root: \n" +
                    "   file: " + file + "\n" +
                    "   store: " + this);
        }
        // strip off the file separator char, if present
        String rootPath = path.startsWith(tenantAbsolutePath) ? tenantAbsolutePath : rootAbsolutePath;
        int index = rootPath.length();
        if (path.charAt(index) == File.separatorChar)
        {
            index++;
        }
        // strip off the root path and adds the protocol prefix
        String url = FileContentStore.STORE_PROTOCOL + ContentStore.PROTOCOL_DELIMITER + path.substring(index);
        // replace '\' with '/' so that URLs are consistent across all filesystems
        url = url.replace('\\', '/');
        
        if (logger.isDebugEnabled())
            logger.debug("Converted path " + path + " to url " + url);
        
        // done
        return url;
    }
    
    /*package*/ File makeFile(String contentUrl)
    {
        // take just the part after the protocol
        Pair<String, String> urlParts = super.getContentUrlParts(contentUrl);
        String protocol = urlParts.getFirst();
        String relativePath = urlParts.getSecond();
        // Check the protocol
        if (!protocol.equals(FileContentStore.STORE_PROTOCOL))
        {
            throw new UnsupportedContentUrlException(this, contentUrl);
        }
        
        // look in tenant directory (default behaviour of this store)
        File file = new File(tenantDirectory, relativePath);
        if (!file.exists())
        {
            // for backwards compatibility with vanilla tenant-aware file content store

            // system tenant
            file = new File(rootDirectory, relativePath);
            if (!file.exists())
            {
                String tenantDomain = TenantContextHolder.getTenantDomain();
                if (tenantDomain != null)
                {
                    // current tenant
                    file = new File(tenantDirectory, tenantDomain + "/" + relativePath);
                    if (!file.exists())
                    {
                        // second - scan all tenants (ouch)
                        List<TenantEntity> tenants = tenantAdminDAO.listTenants(true);
                        for (TenantEntity tenant : tenants)
                        {
                            if (!tenant.getEnabled())
                            {
                                logger.error("Did not expect to receive disabled tenants from tenantAdminDAO.listTenants.");
                            }
                            String td = tenant.getTenantDomain();
                            File tenantFile = new File(tenantDirectory, td + "/" + relativePath);
                            if (tenantFile.exists())
                            {
                                // if not found, then leave with current tenant mapping
                                file = tenantFile;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Converted url " + contentUrl + " to file " + file.getAbsolutePath() + " (exists: " + file.exists() + ")");
        
        return file;
    }
    
    private String createNewUrl()
    {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        String tenantDomain = TenantUtil.getCurrentDomain();
        if (tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // stored under "system" (aka default) tenant
            // Note: can't use TenantWebScriptServlet.SYSTEM_TENANT as hyphens can cause problems in file paths
            tenantDomain = "system";
        }
        
        // create the URL
        StringBuilder sb = new StringBuilder(20);
        sb.append(FileContentStore.STORE_PROTOCOL)
          .append(ContentStore.PROTOCOL_DELIMITER)
          .append(tenantDomain).append('/')
          .append(year).append('/')
          .append(month).append('/')
          .append(day).append('/')
          .append(hour).append('/')
          .append(minute).append('/')
          .append(GUID.generate()).append(".bin");
        String newContentUrl = sb.toString();
        
        if (logger.isDebugEnabled())
            logger.debug("Created new url " + newContentUrl);
        
        // done
        return newContentUrl;
    }

    @Override
    public void onEnableTenant()
    {
    }

    @Override
    public void onDisableTenant()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public String getRootLocation()
    {
        return tenantAbsolutePath;
    }
}
