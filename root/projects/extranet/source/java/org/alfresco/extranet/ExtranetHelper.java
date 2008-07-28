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
package org.alfresco.extranet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.extranet.jira.JIRAService;
import org.alfresco.extranet.ldap.LDAPCompany;
import org.alfresco.extranet.ldap.LDAPGroup;
import org.alfresco.extranet.ldap.LDAPUser;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The Class ExtranetHelper.
 * 
 * @author muzquiano
 */
public class ExtranetHelper
{
    
    /**
     * Gets the user service.
     * 
     * @param request the request
     * 
     * @return the user service
     */
    public static UserService getUserService(HttpServletRequest request)
    {
        return getUserService(request.getSession().getServletContext());        
    }
    
    /**
     * Gets the user service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the user service
     */
    public static UserService getUserService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (UserService) context.getBean("extranet.service.user");
    }
    
    /**
     * Gets the group service.
     * 
     * @param request the request
     * 
     * @return the group service
     */
    public static GroupService getGroupService(HttpServletRequest request)
    {
        return getGroupService(request.getSession().getServletContext());
    }
    
    /**
     * Gets the group service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the group service
     */
    public static GroupService getGroupService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (GroupService) context.getBean("extranet.service.group");
    }
    
    /**
     * Gets the invitation service.
     * 
     * @param request the request
     * 
     * @return the invitation service
     */
    public static InvitationService getInvitationService(HttpServletRequest request)
    {
        return getInvitationService(request.getSession().getServletContext());
    }
    
    /**
     * Gets the invitation service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the invitation service
     */
    public static InvitationService getInvitationService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (InvitationService) context.getBean("extranet.service.invitation");        
    }
    
    /**
     * Gets the company service.
     * 
     * @param request the request
     * 
     * @return the company service
     */
    public static CompanyService getCompanyService(HttpServletRequest request)
    {
        return getCompanyService(request.getSession().getServletContext());
    }
    
    /**
     * Gets the company service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the company service
     */
    public static CompanyService getCompanyService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (CompanyService) context.getBean("extranet.service.company");                
    }
    
    /**
     * Gets the JIRA service.
     * 
     * @param request the request
     * 
     * @return the JIRA service
     */
    public static JIRAService getJIRAService(HttpServletRequest request)
    {
        return getJIRAService(request.getSession().getServletContext());
    }
    
    /**
     * Gets the JIRA service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the JIRA service
     */
    public static JIRAService getJIRAService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (JIRAService) context.getBean("extranet.service.jira");                
    }
    
    /**
     * Gets the sync service.
     * 
     * @param request the request
     * 
     * @return the sync service
     */
    public static SyncService getSyncService(HttpServletRequest request)
    {
        return getSyncService(request.getSession().getServletContext());        
    }
    
    /**
     * Gets the sync service.
     * 
     * @param servletContext the servlet context
     * 
     * @return the sync service
     */
    public static SyncService getSyncService(ServletContext servletContext)
    {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return (SyncService) context.getBean("extranet.service.sync");
    }
    
    
    
    
    // Helper Functions
        
    /**
     * Gets the entity service.
     * 
     * @param entityType the entity type
     * 
     * @return the entity service
     */
    public static EntityService getEntityService(HttpServletRequest request, String entityType)
    {
        EntityService service = null;
        
        if("company".equals(entityType))
        {
            service = getCompanyService(request);
        }
        else if("user".equals(entityType))
        {
            service = getUserService(request);
        }
        else if("group".equals(entityType))
        {
            service = getGroupService(request);
        }
        
        return service;
    }
    
    public static String[] getEntityPropertyNames(String entityType)
    {
        String[] propertyNames = null;
        
        // TODO: This needs to be rethought
        
        if("company".equals(entityType))
        {
            propertyNames = new LDAPCompany("dummy").getPropertyNames();
        }
        else if("user".equals(entityType))
        {
            propertyNames = new LDAPUser("dummy").getPropertyNames();
        }
        else if("group".equals(entityType))
        {
            propertyNames = new LDAPGroup("dummy").getPropertyNames();
        }        
        
        return propertyNames;
    }
    
    public static String getEntityClassName(String entityType)
    {
        String className = null;

        if("company".equals(entityType))
        {
            className = "org.alfresco.extranet.database.DatabaseCompany";
        }
        else if("user".equals(entityType))
        {
            className = "org.alfresco.extranet.database.DatabaseUser";
        }
        else if("group".equals(entityType))
        {
            className = "org.alfresco.extranet.database.DatabaseGroup";
        }     
        
        return className;
    }
    
    public static Entity newEntity(String entityType, String entityId)
    {
        String className = getEntityClassName(entityType);
        
        Class[] argTypes = new Class[] { String.class };
        Object[] args = new Object[] { entityId };
        
        return (Entity) org.alfresco.util.ReflectionHelper.newObject(className, argTypes, args);
    }
}
