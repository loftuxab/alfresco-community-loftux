/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco.v3;

import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoUserGroupServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.repo.site.SiteInfo;
import org.alfresco.repo.site.SiteService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of UserGroupServiceHandler and AbstractAlfrescoUserGroupServiceHandler
 * 
 * @author PavelYur
 */
public class AlfrescoUserGroupServiceHandler extends AbstractAlfrescoUserGroupServiceHandler
{

    private SiteService siteService;

    private TransactionService transactionService;

    private static Log logger = LogFactory.getLog(AlfrescoUserGroupServiceHandler.class);

    /**
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#addUserCollectionToRole(java.lang.String, java.lang.String, java.util.List)
     */
    public void addUserCollectionToRole(String dws, String roleName, List<UserBean> usersList)
    {
        dws = VtiPathHelper.removeSlashes(dws);
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is started.");

        if (logger.isDebugEnabled())
            logger.debug("Getting siteInfo for '" + dws + "'.");
        SiteInfo siteInfo = siteService.getSite(dws);

        if (siteInfo == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: Site info not found.");
            throw new VtiHandlerException(VtiHandlerException.NOT_FOUND);
        }

        for (UserBean userBean : usersList)
        {
            NodeRef person = personService.getPerson(userBean.getLoginName().substring("ALFRESCO\\".length()));
            if (person != null)
            {
                String userName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);

                if (AuthorityType.getAuthorityType(userName) == AuthorityType.GUEST)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Error: Not allowed operation: guest can not be added.");
                    throw new RuntimeException("Not allowed operation: guest can not be added.");
                }
                UserTransaction tx = transactionService.getUserTransaction(false);
                try
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Setting membership [" + dws + ", " + userName + "].");

                    tx.begin();

                    siteService.setMembership(dws, userName, roleName);

                    tx.commit();
                }
                catch (Exception e)
                {
                    try
                    {
                        tx.rollback();
                    }
                    catch (Exception tex)
                    {
                    }
                    if (logger.isDebugEnabled())
                        logger.debug("Error: The user does not have sufficient rights.", e);
                    throw new VtiHandlerException(VtiHandlerException.NOT_PERMISSIONS);
                }

            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Error: The user does not have sufficient rights.");
                throw new VtiHandlerException(VtiHandlerException.NOT_PERMISSIONS);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is finished.");
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#isUserMember(java.lang.String, java.lang.String)
     */
    public boolean isUserMember(String dwsUrl, String username)
    {
        return siteService.isMember(dwsUrl, username);
    }
}
