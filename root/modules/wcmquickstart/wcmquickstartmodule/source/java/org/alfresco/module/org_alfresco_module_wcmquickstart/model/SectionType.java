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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.rendition.RenditionDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:section type behaviours.
 * 
 * @author Roy Wetherall
 */
public class SectionType extends TransactionListenerAdapter implements WebSiteModel
{
	/** Transaction key */	
    private static final String AFFECTED_WEB_ASSETS = "AFFECTED_WEB_ASSETS";
    
    /** Array of office mimetypes */
    private static final String[] OFFICE_MIMETYPES = new String[]
    {
    	MimetypeMap.MIMETYPE_WORD,
    	MimetypeMap.MIMETYPE_EXCEL,
    	MimetypeMap.MIMETYPE_PPT
    };

    /** Log */
    private final static Log log = LogFactory.getLog(SectionType.class);

    /** Policy component */
    private PolicyComponent policyComponent;

    /** Behaviour filter */
    private BehaviourFilter behaviourFilter;

    /** Node service */
    private NodeService nodeService;

    /** Content service */
    private ContentService contentService;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    /** File folder service */
    private FileFolderService fileFolderService;
    
    /** Rendition service */
    private RenditionService renditionService;
    
    private ContextParserService contextParserService;
    
    /** Mimetype map */
    private MimetypeMap mimetypeMap;;

    /** The section index page name */
    private String sectionIndexPageName = "index.html";

    /** The section's collection folder name */
    private String sectionCollectionsFolderName = "collections";

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

    /**
     * Set the behaviour filter
     * @param behaviourFilter	behaviour filter
     */
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
     * Set the content service
     * 
     * @param contentService
     *            content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the file folder service
     * 
     * @param fileFolderSevice
     *            file folder service
     */
    public void setFileFolderService(FileFolderService fileFolderSevice)
    {
        this.fileFolderService = fileFolderSevice;
    }
    
    /**
     * Sets the rendition service
     * @param renditionService	rendition service
     */
    public void setRenditionService(RenditionService renditionService)
    {
	    this.renditionService = renditionService;
    }
    
    /**
     * Set the mimetype map
     * @param mimetypeMap	mimetype map
     */
    public void setMimetypeMap(MimetypeMap mimetypeMap)
    {
	    this.mimetypeMap = mimetypeMap;
    }

    /**
     * Sets the section index page name
     * 
     * @param sectionIndexPageName
     *            section index page name
     */
    public void setSectionIndexPageName(String sectionIndexPageName)
    {
        this.sectionIndexPageName = sectionIndexPageName;
    }

    /**
     * Sets the section collection folder name
     * @param sectionCollectionsFolderName
     *            section collections folder name
     */
    public void setSectionCollectionsFolderName(String sectionCollectionsFolderName)
    {
        this.sectionCollectionsFolderName = sectionCollectionsFolderName;
    }
    
    /**
     * Sets the context parser service
     * @param contextParserService	context parser service
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
	    this.contextParserService = contextParserService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        // Register the association behaviours
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                WebSiteModel.TYPE_SECTION, new JavaBehaviour(this, "onCreateNode"));

        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME,
                WebSiteModel.TYPE_SECTION, new JavaBehaviour(this, "onContentPropertyUpdate"));

        policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociationEveryEvent",
                        NotificationFrequency.EVERY_EVENT));
        
        policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociationTransactionCommit",
                        NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onDeleteChildAssociationEveryEvent",
                        NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onDeleteChildAssociationTransactionCommit",
                        NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * 
     * @param childAssoc
     * @param isNewNode
     */
    public void onCreateChildAssociationEveryEvent(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        NodeRef childNode = childAssoc.getChildRef();
        QName childNodeType = nodeService.getType(childNode);
        if (ContentModel.TYPE_FOLDER.equals(childNodeType))
        {
            // Down-cast created node to ws:section
            nodeService.setType(childNode, TYPE_SECTION);

            // Fire create section code
            processCreateNode(childNode);
        } 

        recordAffectedChild(childAssoc);
    }
    
    /**
     * 
     * @param childAssoc
     * @param isNewNode
     */
    public void onCreateChildAssociationTransactionCommit(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        processCommit(childAssoc.getChildRef());
    }
    
    /**
     * 
     * @param childAssoc
     */
    public void onDeleteChildAssociationEveryEvent(ChildAssociationRef childAssoc)
    {
        recordAffectedChild(childAssoc);
    }
    
    /**
     * 
     * @param childAssoc
     */
    public void onDeleteChildAssociationTransactionCommit(ChildAssociationRef childAssoc)
    {
        processCommit(childAssoc.getChildRef());
    }
    
    /**
     * 
     * @param childAssoc
     */
    private void recordAffectedChild(ChildAssociationRef childAssoc)
    {
        NodeRef nodeRef = childAssoc.getChildRef();
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_WEB_ASSETS);
        if (affectedNodeRefs == null)
        {
            affectedNodeRefs = new HashSet<NodeRef>(5);
            AlfrescoTransactionSupport.bindResource(AFFECTED_WEB_ASSETS, affectedNodeRefs);
        }
        affectedNodeRefs.add(nodeRef);
    }

    /**
     * 
     * @param childNode
     */
    private void processCommit(NodeRef childNode)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_WEB_ASSETS);
        if (affectedNodeRefs != null && affectedNodeRefs.remove(childNode))
        {
            if (nodeService.exists(childNode))
            {
                QName childNodeType = nodeService.getType(childNode);
                if (dictionaryService.isSubClass(childNodeType, ContentModel.TYPE_CONTENT) == true
                        && TYPE_VISITOR_FEEDBACK.equals(childNodeType) == false)
                {
                    // Check to see if this is an image
                    ContentReader reader = contentService.getReader(childNode, ContentModel.PROP_CONTENT);
                    if (reader != null && reader.exists())
                    {
                        String mimetype = reader.getMimetype();
                        if (mimetype != null && mimetype.trim().length() != 0)
                        {
                            if (isImageMimetype(reader.getMimetype()) == true)
                            {   
                                // Make content node an image
                                nodeService.setType(childNode, TYPE_IMAGE);
                            }
                            else if (mimetypeMap.isText(reader.getMimetype()) == true)
                            {   
                                // Make the content node an article
                                nodeService.setType(childNode, TYPE_ARTICLE);
                            }
                            else if (isOfficeMimetype(reader.getMimetype()) == true)
                            {
                            	// Get the rendition definition
                            	RenditionDefinition def = renditionService.loadRenditionDefinition(QName.createQName(NAMESPACE, "pdfWebasset"));
                            	if (def != null)
                            	{
                            		// Parse the path template in the context of the current node
            						String pathTemplate = (String)def.getParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE);
            						if (pathTemplate != null)
            						{
            							pathTemplate = contextParserService.parse(childNode, pathTemplate);
            							def.setParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE, pathTemplate);
            						}
                            		
            						// Create rendition
            						childNode = renditionService.render(childNode, def).getChildRef();;
                            	}
              
                            }

                        	// Apply the web asset aspect
                            nodeService.addAspect(childNode, ASPECT_WEBASSET, null);
                        }
                    }
                }

                List<ChildAssociationRef> parentAssocs = nodeService
                        .getParentAssocs(childNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                ArrayList<NodeRef> parentSections = new ArrayList<NodeRef>(
                        parentAssocs.size());
                for (ChildAssociationRef assoc : parentAssocs)
                {
                    NodeRef parentNode = assoc.getParentRef();
                    if (dictionaryService.isSubClass(nodeService
                            .getType(parentNode), WebSiteModel.TYPE_SECTION))
                    {
                        parentSections.add(parentNode);
                    }
                }
                
                try
                {
                    behaviourFilter.disableBehaviour(childNode, ASPECT_WEBASSET);
                    behaviourFilter.disableBehaviour(childNode,
                            ContentModel.ASPECT_AUDITABLE);
                    nodeService.setProperty(childNode, PROP_PARENT_SECTIONS,
                            parentSections);
                } 
                finally
                {
                    behaviourFilter.enableBehaviour(childNode,
                            ContentModel.ASPECT_AUDITABLE);
                    behaviourFilter.enableBehaviour(childNode, ASPECT_WEBASSET);
                }
            }
        }
    }    
    
    /**
     * On create node behaviour 
     * @param childAssocRef	child association reference
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        processCreateNode(childAssocRef.getChildRef());
    }
    
    /**
     * On creation of a section node
     * @param childAssocRef
     *            created child association reference
     */
    public void processCreateNode(NodeRef section)
    {
        // Create an index page for the section
        FileInfo indexPage = fileFolderService.create(section, sectionIndexPageName, TYPE_INDEX_PAGE);
        ContentWriter writer = fileFolderService.getWriter(indexPage.getNodeRef());
        writer.setEncoding("UTF-8");
        writer.setMimetype(MimetypeMap.MIMETYPE_HTML);
        writer.putContent("");
        nodeService.addAspect(indexPage.getNodeRef(), ASPECT_WEBASSET, null);

        // Create the collections folder node
        fileFolderService.create(section, sectionCollectionsFolderName, TYPE_WEBASSET_COLLECTION_FOLDER);
    }

    public void onContentPropertyUpdate(NodeRef nodeRef, QName propertyQName, ContentData beforeValue,
            ContentData afterValue)
    {
        //Hook in here to process tagscope information
        if (log.isDebugEnabled())
        {
            log.debug("onContentPropertyUpdate: " + nodeRef + ";  " + propertyQName + ";  "
                    + afterValue.toString());
        }
    }

    /**
     * Indicates whether this is am image mimetype or not.
     * 
     * @param mimetype mimetype
     * @return boolean true if image mimetype, false otherwise
     */
    private boolean isImageMimetype(String mimetype)
    {
        return mimetype.startsWith("image");
    }
    
    /**
     * Indicates whether this is an office mimetype or not.
     * @param mimetype
     * @return
     */
    private boolean isOfficeMimetype(String mimetype)
    {
    	return ArrayUtils.contains(OFFICE_MIMETYPES, mimetype);
    }

}
