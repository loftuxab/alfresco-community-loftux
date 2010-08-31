/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.tagging.TagDetails;
import org.alfresco.service.cmr.tagging.TagScope;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * cm:tagscope aspect behaviours.
 * 
 * @author Brian Remmington
 */
public class TagScopeAspect implements WebSiteModel
{
    private final static Log log = LogFactory.getLog(TagScopeAspect.class);

    /** Policy component */
    private PolicyComponent policyComponent;

    /** Node service */
    private NodeService nodeService;

    /** Behaviour Filter */
    private BehaviourFilter behaviourFilter;
    
    /** Tagging Service */
    private TaggingService taggingService;

    /**
     * Set the policy component
     * 
     * @param policyComponent
     *            policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the tagging service
     * 
     * @param taggingService
     *           tagging service
     */
    public void setTaggingService(TaggingService taggingService)
    {
        this.taggingService = taggingService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME,
                ContentModel.ASPECT_TAGSCOPE, new JavaBehaviour(this, "onContentPropertyUpdate"));
    }

    public void onContentPropertyUpdate(NodeRef nodeRef, QName propertyQName, ContentData beforeValue,
            ContentData afterValue)
    {
        if (nodeService.exists(nodeRef) && nodeService.getType(nodeRef).equals(TYPE_SECTION))
        {
            if (log.isDebugEnabled())
            {
                log.debug("onContentPropertyUpdate on section: " + nodeRef + ";  " + propertyQName + ";  "
                        + afterValue.toString());
            }
            ArrayList<String> tags = new ArrayList<String>();
            ArrayList<Integer> tagCounts = new ArrayList<Integer>();
            
            TagScope tagScope = taggingService.findTagScope(nodeRef);
            if (tagScope != null)
            {
                List<TagDetails> tagDetails = tagScope.getTags();
                for (TagDetails tagDetail : tagDetails)
                {
                    tags.add(tagDetail.getName());
                    tagCounts.add(tagDetail.getCount());
                }
            }
            
            behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_TAGSCOPE);
            try
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Setting section top tags to be " + tags + " on node " + nodeRef);
                    log.debug("Setting section top tag counts to be " + tagCounts + " on node " + nodeRef);
                }
                nodeService.setProperty(nodeRef, PROP_TOP_TAGS, tags);
                nodeService.setProperty(nodeRef, PROP_TOP_TAG_COUNTS, tagCounts);
            }
            finally
            {
                behaviourFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_TAGSCOPE);
            }
        }
    }
}
