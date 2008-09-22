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
package org.alfresco.module.mediawikiintegration;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.mediawikiintegration.action.MediaWikiActionExecuter;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Manages wiki pages relating to sites.  If site are not supported by the repository intalled then 
 * this management will be ingnored.
 * 
 * @author Roy Wetherall
 */
public class MediaWikiSitePageManager implements NodeServicePolicies.OnCreateNodePolicy
{
    /** Policy Component */
    private PolicyComponent policyComponent;
    
    /** Node sevice */
    private NodeService nodeService;
    
    /** Action serivice **/
    private ActionService actionService;
    
    /** The template service */
    private TemplateService templateService;
    
    /**
     * Set policy component
     * 
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set node service
     * 
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set action service
     * 
     * @param actionService     action service
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Set template service
     * 
     * @param templateService   template service
     */
    public void setTemplateService(TemplateService templateService)
    {
        this.templateService = templateService;
    }
    
    /**
     * Initialise method
     */
    public void init()
    {
        QName siteType = QName.createQName("http://www.alfresco.org/model/site/1.0", "site");
        
        // Register the behaviours
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"), 
                siteType, 
                new JavaBehaviour(this, "onCreateNode"));
    }

    /**
     * Action to take when a site is created
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository.ChildAssociationRef)
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        // Get the site node
        NodeRef site = childAssocRef.getChildRef();
        
        // Get the title of the site to be used as the title of the created page
        String siteTitle = (String)this.nodeService.getProperty(site, ContentModel.PROP_TITLE);
        
        // Create the site page        
        NodeRef templateNodeRef = new NodeRef("workspace://SpacesStore/php_mediawiki_site_page");
        Object model = new HashMap<String, Object>(1);
        ((Map<String, Object>)model).put("site", site);
        String pageContent = this.templateService.processTemplate(templateNodeRef.toString(), model);
        
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put("pageContent", pageContent);
        
        Action action = actionService.createAction(MediaWikiActionExecuter.NAME);
        action.setParameterValue(MediaWikiActionExecuter.PARAM_MEDIAWIKI_ACTION, "createPage");
        action.setParameterValue(MediaWikiActionExecuter.PARAM_PAGE_TITLE, siteTitle);
        action.setParameterValue(MediaWikiActionExecuter.PARAM_PARAMS, params);
        actionService.executeAction(action, site, false, true);
    } 
}
