/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site;

import java.util.HashMap;

import org.alfresco.connector.CredentialsVault;
import org.alfresco.connector.IdentityVault;
import org.alfresco.web.site.config.RuntimeConfig;
import org.alfresco.web.site.config.RuntimeConfigManager;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author muzquiano
 */
public abstract class RequestContext
{
    private static Log logger = LogFactory.getLog(RequestContext.class);

    protected RequestContext()
    {
        this.map = new HashMap();
    }
    
    public Configuration getSiteConfiguration()
    {
        return ModelUtil.getSiteConfiguration(this);
    }

    public String getWebsiteTitle()
    {
        String title = "Website";
        
        if(getSiteConfiguration() != null)
        {
            title = getSiteConfiguration().getName();
        }
        
        return title;
    }

    public String getPageTitle()
    {
        String title = "Default Page";
        
        if (getCurrentPage() != null)
        {
            title = getCurrentPage().getName();
        }
        
        return title;
    }

    public void setValue(String key, Object value)
    {
        if (key != null && value != null)
        {
            map.put(key, value);
        }
    }

    public Object getValue(String key)
    {
        return map.get(key);
    }

    public void removeValue(String key)
    {
        if (map.containsKey(key))
        {
            map.remove(key);
        }
    }

    public RuntimeConfig loadConfiguration(ModelObject obj)
    {
        return RuntimeConfigManager.loadConfiguration(this, obj);
    }

    public LinkBuilder getLinkBuilder()
    {
        return LinkBuilderFactory.newInstance(this);
    }

    public Page getCurrentPage()
    {
        return this.currentPage;
    }

    public void setCurrentPage(Page page)
    {
        this.currentPage = page;
    }
    
    public String getCurrentPageId()
    {
        if(getCurrentPage() != null)
        {
            return getCurrentPage().getId();
        }
        return null;
    }

    public Page getRootPage()
    {
        return ModelUtil.getRootPage(this);
    }

    public Template getCurrentTemplate()
    {
        if(getCurrentPage() != null)
        {
            return getCurrentPage().getTemplate(this);
        }
        return null;
    }
    
    public String getCurrentTemplateId()
    {
        if(getCurrentTemplate() != null)
        {
            return getCurrentTemplate().getId();
        }
        return null;
    }
    
    /*
    public Object getCurrentObject()
    {
        return null;
    }
    
    public void setCurrentObject(Object o)
    {        
    }
    */

    public String getCurrentObjectId()
    {
        return this.currentObjectId;
    }

    public void setCurrentObjectId(String objectId)
    {
        this.currentObjectId = objectId;
    }

    public String getCurrentFormatId()
    {
        return this.currentFormatId;
    }

    public void setCurrentFormatId(String formatId)
    {
        this.currentFormatId = formatId;
    }

    public IFileSystem getFileSystem()
    {
        return this.fileSystem;
    }

    public void setFileSystem(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    public String getStoreId()
    {
        if (this.storeId == null)
        {
            this.storeId = "";
        }
        return this.storeId;
    }

    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
    }

    // helpers

    public IModel getModel()
    {
        return Framework.getModel();
    }

    public AbstractConfig getConfig()
    {
        return Framework.getConfig();
    }

    public Log getLogger()
    {
        return logger;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    protected HashMap map;
    protected Page currentPage;
    protected String currentObjectId;
    protected String currentFormatId;
    protected IFileSystem fileSystem;
    protected String storeId;
    protected User user;

    public static String VALUE_HEAD_TAGS = "headTags";
    public static String VALUE_CREDENTIAL_VAULT = "credential_vault";
    public static String VALUE_IDENTITY_VAULT = "identity_vault";

    //

    public CredentialsVault getUserCredentialVault()
    {
        return (CredentialsVault) getValue(VALUE_CREDENTIAL_VAULT);
    }

    public IdentityVault getUserIdentityVault()
    {
        return (IdentityVault) getValue(VALUE_IDENTITY_VAULT);
    }

}
