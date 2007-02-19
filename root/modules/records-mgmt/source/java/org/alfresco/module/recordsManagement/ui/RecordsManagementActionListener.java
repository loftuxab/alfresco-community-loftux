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
package org.alfresco.module.recordsManagement.ui;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.Node;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.BrowseBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.repo.component.property.UIPropertySheet;


/**
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementActionListener 
{
    /** The service registry */
    private ServiceRegistry services;
    
    /**
     * Set the service registry
     * 
     * @param services  the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) 
    {
        this.services = services;
    }
    
    public void executeScript(ActionEvent event)
    {
        // Get the script to be executed
        UIActionLink link = (UIActionLink)event.getComponent();
        Map<String, String> params = link.getParameterMap();
        
        String id = params.get("id");
        NodeRef documentNodeRef = new NodeRef(Repository.getStoreRef(), id);
        
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("document", new Node(documentNodeRef, this.services));
        
        // Add the parameters to the model passed to the script
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            model.put(entry.getKey(), entry.getValue());
        }
        
        // Determine whether the script is a node reference of a path
        String script = params.get("script");
        NodeRef nodeRef = new NodeRef(script);
        this.services.getScriptService().executeScript(nodeRef, ContentModel.PROP_CONTENT, model);      
        
        FacesContext context = FacesContext.getCurrentInstance();
        BrowseBean browseBean = (BrowseBean)FacesHelper.getManagedBean(context, "BrowseBean");
        String actionLocation = params.get("actionLocation");
        if (actionLocation.equals("document-details") == true)
        {
            browseBean.getDocument().reset();
            UIPropertySheet comp = (UIPropertySheet)event.getComponent().findComponent("document-details:document-props");
            comp.getChildren().clear();
        }
        else if (actionLocation.equals("folder-details") == true)
        {
            browseBean.getActionSpace().reset();
            UIPropertySheet comp = (UIPropertySheet)event.getComponent().findComponent("space-details:space-props");
            comp.getChildren().clear();
        }  
    }
}
