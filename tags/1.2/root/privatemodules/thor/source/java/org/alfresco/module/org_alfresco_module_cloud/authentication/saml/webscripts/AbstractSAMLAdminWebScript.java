/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * SAML Admin - abstract WebScript
 * 
 * @author janv
 * @since Cloud SAML
 */
public abstract class AbstractSAMLAdminWebScript extends AbstractWebScript
{
    private Log logger = LogFactory.getLog(getClass());
    
    private NetworkAdmin networkAdmin;
    private AccountService accountService;
    
    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    
    public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException
    {
        networkAdmin.runAs(new NetworkAdminRunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                unprotectedExecuteImpl(req, res);
                return null;
            }
        });
    }
    
    protected void validateAccount(final String tenantDomain)
    {
        Account account = accountService.getAccountByDomain(tenantDomain);
        if(account != null)
        {
            String level = account.getType().getSubscriptionLevel();
            if ((level == null) || (AccountType.SubscriptionLevel.valueOf(level) != AccountType.SubscriptionLevel.Enterprise))
            {
                // Can only SAML-enable Enterprise Networks
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "Cannot SAML-enabled Network that does not have Enterprise subscription level: "
                    + tenantDomain);
            }
        }
    }
    
    abstract protected void unprotectedExecuteImpl(WebScriptRequest req, WebScriptResponse res) throws IOException;
    
    /**
     * Set attachment header
     * 
     * @param res
     * @param attach
     * @param attachFileName
     */
    // note: borrowed from StreamContent
    protected void setAttachment(WebScriptResponse res, boolean attach, String attachFileName)
    {
        if (attach == true)
        {
            String headerValue = "attachment";
            if (attachFileName != null && attachFileName.length() > 0)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Attaching content using filename: " + attachFileName);
                }
                
                headerValue += "; filename*=UTF-8''" + WebDAVHelper.encodeURL(attachFileName) + "; filename=\"" + attachFileName + "\"";
            }
            
            // set header based on filename - will force a Save As from the browse if it doesn't recognize it
            // this is better than the default response of the browser trying to display the contents
            res.setHeader("Content-Disposition", headerValue);
        }
    }
}
