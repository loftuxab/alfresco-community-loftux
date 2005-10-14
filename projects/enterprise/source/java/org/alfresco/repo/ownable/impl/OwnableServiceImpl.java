/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Alfresco Network License. You may obtain a
 * copy of the License at
 *
 *   http://www.alfrescosoftware.com/legal/
 *
 * Please view the license relevant to your network subscription.
 *
 * BY CLICKING THE "I UNDERSTAND AND ACCEPT" BOX, OR INSTALLING,  
 * READING OR USING ALFRESCO'S Network SOFTWARE (THE "SOFTWARE"),  
 * YOU ARE AGREEING ON BEHALF OF THE ENTITY LICENSING THE SOFTWARE    
 * ("COMPANY") THAT COMPANY WILL BE BOUND BY AND IS BECOMING A PARTY TO 
 * THIS ALFRESCO NETWORK AGREEMENT ("AGREEMENT") AND THAT YOU HAVE THE   
 * AUTHORITY TO BIND COMPANY. IF COMPANY DOES NOT AGREE TO ALL OF THE   
 * TERMS OF THIS AGREEMENT, DO NOT SELECT THE "I UNDERSTAND AND AGREE"   
 * BOX AND DO NOT INSTALL THE SOFTWARE OR VIEW THE SOURCE CODE. COMPANY   
 * HAS NOT BECOME A LICENSEE OF, AND IS NOT AUTHORIZED TO USE THE    
 * SOFTWARE UNLESS AND UNTIL IT HAS AGREED TO BE BOUND BY THESE LICENSE  
 * TERMS. THE "EFFECTIVE DATE" FOR THIS AGREEMENT SHALL BE THE DAY YOU  
 * CHECK THE "I UNDERSTAND AND ACCEPT" BOX.
 */
package org.alfresco.repo.ownable.impl;

import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.ownable.OwnableService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;

public class OwnableServiceImpl implements OwnableService, InitializingBean
{
    private NodeService nodeService;
    
    private AuthenticationService authenticationService;

    public OwnableServiceImpl()
    {
        super();
    }

    // IOC
   
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    

    public void afterPropertiesSet() throws Exception
    {
        if(nodeService == null)
        {
            throw new IllegalArgumentException("A node service must be set");
        }
        if(authenticationService == null)
        {
            throw new IllegalArgumentException("An authentication service must be set");
        }
    }
    
    // OwnableService implmentation
    
  
    public String getOwner(NodeRef nodeRef)
    {
        String userName = null;
        // If ownership is not explicitly set then we fall back to the creator
        //
        if(nodeService.hasAspect(nodeRef, ContentModel.ASPECT_OWNABLE))
        {
           userName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(nodeRef, ContentModel.PROP_OWNER));
        }
        else if(nodeService.hasAspect(nodeRef, ContentModel.ASPECT_AUDITABLE))
        {
            userName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR));
        }
        return userName;
    }

    public void setOwner(NodeRef nodeRef, String userName)
    {
        if(!nodeService.hasAspect(nodeRef, ContentModel.ASPECT_OWNABLE))
        {
            HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
            properties.put(ContentModel.PROP_OWNER, userName);
            nodeService.addAspect(nodeRef, ContentModel.ASPECT_OWNABLE, properties);
        }
        else
        {
            nodeService.setProperty(nodeRef, ContentModel.PROP_OWNER, userName);
        }
        
    }

    public void takeOwnership(NodeRef nodeRef)
    {
        setOwner(nodeRef, authenticationService.getCurrentUserName());
    }

    public boolean hasOwner(NodeRef nodeRef)
    {
        return getOwner(nodeRef) != null;
    }

}
