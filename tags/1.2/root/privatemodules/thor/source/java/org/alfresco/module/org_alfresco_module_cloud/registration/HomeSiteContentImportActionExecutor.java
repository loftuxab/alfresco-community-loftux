/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.registration;

import java.io.File;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.importer.ACPImportPackageHandler;
import org.alfresco.repo.importer.DefaultImporterContentCache;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.view.ImportPackageHandler;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterContentCache;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;


public class HomeSiteContentImportActionExecutor extends ActionExecuterAbstractBase implements InitializingBean
{
    private static final Log logger = LogFactory.getLog(HomeSiteContentImportActionExecutor.class);
    private static final String SAMPLE_CONTENT_VIEW = "alfresco/module/org_alfresco_module_cloud/bootstrap/sampleContent.acp";
    
    public static final String NAME = "home-site-content-import";
    public static final String PARAM_USER = "user";
    
    private ImporterService importerService;
    private AccountService accountService;
    private NodeService nodeService;
    private ContentService contentService;
    private EmailAddressService emailAddressService;
    private DefaultImporterContentCache contentCache;
    

    public void setImporterService(ImporterService service) 
    {
        this.importerService = service;
    }

    public void setNodeService(NodeService service) 
    {
        this.nodeService = service;
    }

    public void setContentService(ContentService service) 
    {
        this.contentService = service;
    }

    public void setEmailAddressService(EmailAddressService service) 
    {
        this.emailAddressService = service;
    }
    
    public void setAccountService(AccountService service) 
    {
        this.accountService = service;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        contentCache = new DefaultImporterContentCache();
        contentCache.setContentService(contentService);
    }
    
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) 
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_USER, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_USER)));
    }

    @Override
    public void executeImpl(Action action, final NodeRef actionedUponNodeRef)
    {
        final String user = (String)action.getParameterValue(PARAM_USER);
        String domain = emailAddressService.getDomain(user);
        Account account = accountService.getAccountByDomain(domain);
        
        if (account == null)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Skipped import of home site sample content for user " + user + ". Account does not exist: " + domain);
            }
            
            return;
        }
        
        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                AuthenticationUtil.pushAuthentication();
                AuthenticationUtil.setFullyAuthenticatedUser(user);
                
                try
                {
                    Status status = nodeService.getNodeStatus(actionedUponNodeRef);
                    if (status == null || status.isDeleted())
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("Skipped import of home site sample content for user " + user + ". Document library does not exist: " + actionedUponNodeRef);
                        }
                        return null;
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Importing home site sample content for user " + user + " to " + actionedUponNodeRef);
                        }
                    }
                    
                    long start = System.currentTimeMillis();
                    importSampleContent(actionedUponNodeRef);
                    long end = System.currentTimeMillis();
                    
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Home site sample content imported in " + (end - start) + "ms for user " + user);
                    }
                    
                    return null;
                }
                finally
                {
                    AuthenticationUtil.popAuthentication();
                }
            }
        }, account.getTenantId());
    }

    private void importSampleContent(NodeRef documentLibrary)
    {
        final File viewFile = ImporterBootstrap.getFile(SAMPLE_CONTENT_VIEW);
        final ImportPackageHandler acpHandler = new ACPImportPackageHandler(viewFile, null);
        final Location location = new Location(documentLibrary);
        final ImporterBinding binding = new ImporterBinding()
        {
            @Override
            public String getValue(String key)
            {
                return null;
            }

            @Override
            public UUID_BINDING getUUIDBinding()
            {
                return UUID_BINDING.CREATE_NEW;
            }

            @Override
            public QName[] getExcludedClasses()
            {
                return null;
            }

            @Override
            public boolean allowReferenceWithinTransaction()
            {
                return false;
            }
            
            @Override
            public ImporterContentCache getImportConentCache()
            {
                return contentCache;
            }
        };

        importerService.importView(acpHandler, location, binding, (ImporterProgress) null);
    }

}
