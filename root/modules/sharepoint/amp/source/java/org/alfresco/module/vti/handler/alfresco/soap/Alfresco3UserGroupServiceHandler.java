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
package org.alfresco.module.vti.handler.alfresco.soap;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.endpoints.WebServiceErrorCodeException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.soap.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.soap.usergroup.UserBean;
import org.alfresco.repo.site.SiteService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author PavelYur
 *
 */
public class Alfresco3UserGroupServiceHandler implements UserGroupServiceHandler 
{

    private PersonService personService;
    private VtiPathHelper pathHelper;
    private NodeService nodeService;
    private SiteService siteService;
    
    private TransactionService transactionService;
    
    private static Log logger = LogFactory.getLog(Alfresco3UserGroupServiceHandler.class);
        
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }       

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
 
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is started.");
        
        NodeRef dwsNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, dwsUrl);
        
        if (logger.isDebugEnabled())
            logger.debug("Getting fileinfo for '" + dwsNodeRef + "'.");
        FileInfo dwsFileInfo = pathHelper.getFileFolderService().getFileInfo(dwsNodeRef);

        if (dwsFileInfo == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: File info is null.");
            throw new WebServiceErrorCodeException(10);
        }

        if (dwsFileInfo.isFolder() == false)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: File info is not folder.");
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        for (UserBean userBean : usersList)
        {
            NodeRef person = personService.getPerson(userBean.getLogin().substring("ALFRESCO\\".length()));
            if (person != null)
            {
                String userName = (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);
                
                if (userName.equalsIgnoreCase("guest"))
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Error: Not allowed operation: guest can not be added.");
                    throw new RuntimeException("Not allowed operation: guest can not be added.");
                }
                UserTransaction tx = transactionService.getUserTransaction(false);
                try
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Setting membership [" + dwsFileInfo.getName() + ", " + userName + "].");
                    
                    tx.begin();
                    
                    siteService.setMembership(dwsFileInfo.getName(), userName, roleName);
                    
                    tx.commit();
                }
                catch (Exception e)
                {
                    try
                    {
                        tx.rollback();
                    }
                    catch (Exception tex){}
                    if (logger.isDebugEnabled())
                        logger.debug("Error: The user does not have sufficient rights.", e);
                    throw new WebServiceErrorCodeException(3);                    
                }
                                
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Error: The user does not have sufficient rights.");                
                throw new WebServiceErrorCodeException(3);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is finished.");
        
    }

    public List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getUserLoginFromEmail' is started.");
        
        List<UserBean> result = new LinkedList<UserBean>();

        for (String loginOrEmail : emailList)
        {
            if (logger.isDebugEnabled())
                logger.debug("Checking existence of login or email '" + loginOrEmail + "'.");
            
            if (personService.personExists(loginOrEmail))
            {
                if (logger.isDebugEnabled())
                    logger.debug("Login '" + loginOrEmail + "' is exist, adding to result.");

                result.add(getUserBean(personService.getPerson(loginOrEmail)));
            }
            else
            {
                boolean personFounded = false;
                
                for (NodeRef personNodeRef : personService.getAllPeople())
                {
                    if (nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL).equals(loginOrEmail))
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Email '" + loginOrEmail + "' is exist, adding to result.");
                        result.add(getUserBean(personNodeRef));
                        personFounded = true;
                    }
                }

                if (personFounded == false)
                {
                    UserBean userBean = new UserBean();
                    userBean.setEmail(loginOrEmail);
                    result.add(userBean);
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getUserLoginFromEmail' is finished.");

        return result;
    }
    
    /**
     * Returns user bean for person node reference
     *
     * @param personNodeRef person node reference
     * @return user bean
     */
    private UserBean getUserBean(NodeRef personNodeRef)
    {
        UserBean userBean = new UserBean();

        String userName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_USERNAME));

        String firstName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME));
        String lastName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME));
        
        userBean.setDisplayName(firstName + " " + lastName);
        userBean.setEmail(DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL)));
        userBean.setLogin("ALFRESCO\\" + userName);

        return userBean;
    }
    
}
