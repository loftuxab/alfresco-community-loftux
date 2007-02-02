/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
