/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
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
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:section type behaviours.
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class SectionType extends TransactionListenerAdapter implements WebSiteModel
{
    /** Transaction key */
    private static final String AFFECTED_WEB_ASSETS = "AFFECTED_WEB_ASSETS";

    /** Array of office mimetypes */
    private static final String[] OFFICE_MIMETYPES = new String[] { MimetypeMap.MIMETYPE_WORD,
            MimetypeMap.MIMETYPE_EXCEL, MimetypeMap.MIMETYPE_PPT, MimetypeMap.MIMETYPE_OPENXML_WORDPROCESSING,
            MimetypeMap.MIMETYPE_OPENXML_SPREADSHEET, MimetypeMap.MIMETYPE_OPENXML_PRESENTATION,
            MimetypeMap.MIMETYPE_OPENDOCUMENT_TEXT, MimetypeMap.MIMETYPE_OPENDOCUMENT_SPREADSHEET,
            MimetypeMap.MIMETYPE_OPENDOCUMENT_PRESENTATION

    };

    /** Log */
    private final static Log log = LogFactory.getLog(SectionType.class);

    private PolicyComponent policyComponent;
    private BehaviourFilter behaviourFilter;
    private NodeService nodeService;
    private ContentService contentService;
    private DictionaryService dictionaryService;
    private FileFolderService fileFolderService;
    private RenditionService renditionService;
    private TransactionService transactionService;
    private ContextParserService contextParserService;
    private NamespaceService namespaceService;
    private MimetypeMap mimetypeMap;
    private SectionHierarchyProcessor sectionHierarchyProcessor;

    /** The section index page name */
    private String sectionIndexPageName = "index.html";

    /** The section's collection folder name */
    private String sectionCollectionsFolderName = "collections";

    /**
     * This is the list of collections that will be created automatically for
     * any new section.
     */
    private List<AssetCollectionDefinition> collectionDefinitions = Collections.emptyList();

    private Set<String> typesToIgnore = new TreeSet<String>();

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
     * 
     * @param behaviourFilter
     *            behaviour filter
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

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
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
     * 
     * @param renditionService
     *            rendition service
     */
    public void setRenditionService(RenditionService renditionService)
    {
        this.renditionService = renditionService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the mimetype map
     * 
     * @param mimetypeMap
     *            mimetype map
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
     * 
     * @param sectionCollectionsFolderName
     *            section collections folder name
     */
    public void setSectionCollectionsFolderName(String sectionCollectionsFolderName)
    {
        this.sectionCollectionsFolderName = sectionCollectionsFolderName;
    }

    /**
     * Sets the context parser service
     * 
     * @param contextParserService
     *            context parser service
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }

    public void setSectionHierarchyProcessor(SectionHierarchyProcessor sectionHierarchyProcessor)
    {
        this.sectionHierarchyProcessor = sectionHierarchyProcessor;
    }

    /**
     * When a new content node is added into a section, behaviours configured by
     * this class normally cause it to be specialised to either an article or an
     * image. If you have types for which you don't want this to happen, supply
     * their names as prefixed qualified names ("ws:indexPage", for instance) to
     * this method.
     * 
     * @param typesToIgnore
     */
    public void setTypesToIgnore(Set<String> typesToIgnore)
    {
        this.typesToIgnore = typesToIgnore;
    }

    /**
     * When a new section is created asset collections can be auto-created.
     * Inject the definitions of them here.
     * 
     * @param collectionDefinitions
     */
    public void setAssetCollectionDefinitions(List<AssetCollectionDefinition> collectionDefinitions)
    {
        if (collectionDefinitions == null)
        {
            this.collectionDefinitions = Collections.emptyList();
        }
        else
        {
            this.collectionDefinitions = collectionDefinitions;
        }
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        // Register the association behaviours
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, WebSiteModel.TYPE_SECTION,
                new JavaBehaviour(this, "onCreateNode"));

        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME,
                WebSiteModel.TYPE_SECTION, new JavaBehaviour(this, "onContentPropertyUpdate"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onCreateChildAssociationEveryEvent", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onCreateChildAssociationTransactionCommit", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onDeleteChildAssociationEveryEvent", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onDeleteChildAssociationTransactionCommit", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * 
     * @param childAssoc
     * @param isNewNode
     */
    public void onCreateChildAssociationEveryEvent(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        if (log.isDebugEnabled())
        {
            log.debug("onCreateChildAssociationEveryEvent: parent == " + childAssoc.getParentRef() + "; child == "
                    + childAssoc.getChildRef() + "; newNode == " + isNewNode);
        }
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
        if (log.isDebugEnabled())
        {
            log.debug("Recording affected child of section " + childAssoc.getParentRef() + ":  "
                    + childAssoc.getChildRef());
        }
        NodeRef nodeRef = childAssoc.getChildRef();
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport.getResource(AFFECTED_WEB_ASSETS);
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
    @SuppressWarnings("unchecked")
    private void processCommit(NodeRef childNode)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport.getResource(AFFECTED_WEB_ASSETS);
        Set<NodeRef> affectedSections = new HashSet<NodeRef>();
        if (affectedNodeRefs != null && affectedNodeRefs.remove(childNode))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Processing commit of section child:  " + childNode);
            }
            if (nodeService.exists(childNode))
            {
                QName childNodeType = nodeService.getType(childNode);
                if (dictionaryService.isSubClass(childNodeType, ContentModel.TYPE_CONTENT)
                        && !typesToIgnore.contains(childNodeType.toPrefixString(namespaceService)))
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
                                RenditionDefinition def = renditionService.loadRenditionDefinition(QName.createQName(
                                        NAMESPACE, "pdfWebasset"));
                                if (def != null)
                                {
                                    // Create rendition
                                    RenditionDefinition clone = cloneRenditionDefinition(def, childNode);
                                    childNode = renditionService.render(childNode, clone).getChildRef();
                                }
                            }
                        }
                    }
                    // Apply the web asset aspect
                    nodeService.addAspect(childNode, ASPECT_WEBASSET, null);
                }

                boolean childIsWebAsset = nodeService.hasAspect(childNode, ASPECT_WEBASSET);
                boolean childIsSection = dictionaryService.isSubClass(nodeService.getType(childNode),
                        WebSiteModel.TYPE_SECTION);

                if (childIsSection)
                {
                    affectedSections.add(childNode);
                }
                if (childIsWebAsset)
                {
                    List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(childNode,
                            ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                    ArrayList<NodeRef> parentSections = new ArrayList<NodeRef>(parentAssocs.size());
                    Set<NodeRef> ancestorSections = new HashSet<NodeRef>();
                    for (ChildAssociationRef assoc : parentAssocs)
                    {
                        NodeRef parentNode = assoc.getParentRef();
                        if (dictionaryService.isSubClass(nodeService.getType(parentNode), WebSiteModel.TYPE_SECTION))
                        {
                            parentSections.add(parentNode);
                            Collection<NodeRef> ancestors = (Collection<NodeRef>) nodeService.getProperty(parentNode,
                                    PROP_ANCESTOR_SECTIONS);
                            if (ancestors != null)
                            {
                                ancestorSections.addAll(ancestors);
                            }
                        }
                    }

                    try
                    {
                        behaviourFilter.disableBehaviour(childNode, ASPECT_WEBASSET);
                        behaviourFilter.disableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                        if (childIsWebAsset)
                        {
                            ancestorSections.addAll(parentSections);
                            if (log.isDebugEnabled())
                            {
                                log.debug("Section child is a web asset (" + childNode
                                        + "). Setting parent section ids:  " + parentSections);
                            }
                            nodeService.setProperty(childNode, PROP_PARENT_SECTIONS, parentSections);
                            nodeService.setProperty(childNode, PROP_ANCESTOR_SECTIONS, new ArrayList<NodeRef>(
                                    ancestorSections));
                        }
                    }
                    finally
                    {
                        behaviourFilter.enableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                        behaviourFilter.enableBehaviour(childNode, ASPECT_WEBASSET);
                    }
                }
            }
        }
        if (!affectedSections.isEmpty())
        {
            AlfrescoTransactionSupport.bindListener(new SectionCommitTransactionListener(affectedSections));
        }
    }

    public RenditionDefinition cloneRenditionDefinition(RenditionDefinition source, NodeRef context)
    {
        RenditionDefinition clone = renditionService.createRenditionDefinition(source.getRenditionName(), source
                .getActionDefinitionName());
        clone.setExecuteAsynchronously(source.getExecuteAsychronously());
        clone.setParameterValues(source.getParameterValues());

        String pathTemplate = (String) source.getParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE);
        if (pathTemplate != null)
        {
            String resolvedPath = contextParserService.parse(context, pathTemplate);
            clone.setParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE, resolvedPath);
        }

        return clone;
    }

    /**
     * On create node behaviour
     * 
     * @param childAssocRef
     *            child association reference
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        processCreateNode(childAssocRef.getChildRef());
        recordAffectedChild(childAssocRef);
    }

    /**
     * On creation of a section node
     * 
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
        recordAffectedChild(nodeService.getPrimaryParent(indexPage.getNodeRef()));

        // Create the collections folder node
        FileInfo collectionFolder = fileFolderService.create(section, sectionCollectionsFolderName,
                TYPE_WEBASSET_COLLECTION_FOLDER);

        // and create any configured collections within that folder...
        for (AssetCollectionDefinition collectionDef : collectionDefinitions)
        {
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(ContentModel.PROP_NAME, collectionDef.getName());
            props.put(ContentModel.PROP_TITLE, collectionDef.getTitle());
            if (collectionDef.getQuery() != null)
            {
                props.put(WebSiteModel.PROP_QUERY, collectionDef.getQuery());
                props.put(WebSiteModel.PROP_QUERY_LANGUAGE, collectionDef.getQueryType().getEngineName());
                props.put(WebSiteModel.PROP_QUERY_RESULTS_MAX_SIZE, collectionDef.getMaxResults());
                props.put(WebSiteModel.PROP_MINS_TO_QUERY_REFRESH, collectionDef.getQueryIntervalMinutes());
            }
            nodeService.createNode(collectionFolder.getNodeRef(), ContentModel.ASSOC_CONTAINS, QName.createQName(
                    NamespaceService.CONTENT_MODEL_1_0_URI, collectionDef.getName()),
                    WebSiteModel.TYPE_WEBASSET_COLLECTION, props);

        }
    }

    public void onContentPropertyUpdate(NodeRef nodeRef, QName propertyQName, ContentData beforeValue,
            ContentData afterValue)
    {
        // Hook in here to process tagscope information
        if (log.isDebugEnabled())
        {
            log.debug("onContentPropertyUpdate: " + nodeRef + ";  " + propertyQName + ";  " + afterValue.toString());
        }
    }

    /**
     * Indicates whether this is am image mimetype or not.
     * 
     * @param mimetype
     *            mimetype
     * @return boolean true if image mimetype, false otherwise
     */
    private boolean isImageMimetype(String mimetype)
    {
        return mimetype.startsWith("image");
    }

    /**
     * Indicates whether this is an office mimetype or not.
     * 
     * @param mimetype
     * @return
     */
    private boolean isOfficeMimetype(String mimetype)
    {
        return ArrayUtils.contains(OFFICE_MIMETYPES, mimetype);
    }

    private class SectionCommitTransactionListener extends TransactionListenerAdapter
    {
        private Set<NodeRef> sectionsToProcess = null;

        public SectionCommitTransactionListener(Set<NodeRef> affectedSections)
        {
            this.sectionsToProcess = affectedSections;
        }

        @Override
        public void afterCommit()
        {
            // For each section that has had its ancestors changed we need to
            // adjust any webassets directly
            // below it and any sections directly below it. We then need to
            // process all the affected subsections
            // in the same way
            final RetryingTransactionHelper.RetryingTransactionCallback<Object> work = 
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>()
            {
                public Object execute() throws Throwable
                {
                    sectionHierarchyProcessor.process(sectionsToProcess);
                    return null;
                }
            };

            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                @Override
                public Object doWork() throws Exception
                {
                    transactionService.getRetryingTransactionHelper().doInTransaction(work, false, true);
                    return null;
                }
            }, AuthenticationUtil.SYSTEM_USER_NAME);
        }
    }

}
