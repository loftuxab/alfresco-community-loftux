/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.AbstractRoutingContentStore;
import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.NodeContentContext;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Implementation of a {@link AbstractRoutingContentStore routing content store} that
 * diverts and moves content based on the <b>cm:storeSelector</b> aspect.
 * 
 * @author Philippe Dubois
 * @author Derek Hulley
 * @since 3.2
 */
public class StoreSelectorAspectContentStore
         extends AbstractRoutingContentStore
         implements
                 InitializingBean,
                 NodeServicePolicies.OnUpdatePropertiesPolicy,
                 NodeServicePolicies.OnAddAspectPolicy,
                 NodeServicePolicies.BeforeRemoveAspectPolicy
{
    private static final String ERR_INVALID_DEFAULT_STORE = "content.routing.err.invalid_default_store";
    // private static final String KEY_CONTENT_COPIED_RECORD =
    // "StoreSelectorAspectContentStore.ContentCopiedRecord";

    private static Log logger = LogFactory.getLog(StoreSelectorAspectContentStore.class);

    // private ContentMoveTransactionListener transactionListener;
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private DictionaryService dictionaryService;
    private Map<String, ContentStore> storesByName;
    private List<ContentStore> stores;
    private String defaultStoreName;
    private BehaviourFilter policyFilter;
    /*
     * Policies delegates
     */
    ClassPolicyDelegate<StoreSelectorPolicies.AfterMoveContentPolicy> onContentMoveDelegate;
    ClassPolicyDelegate<StoreSelectorPolicies.AfterMoveContentPolicy> afterMoveContentDelegate;

    public void setPolicyFilter(BehaviourFilter policyFilter)
    {
        this.policyFilter = policyFilter;
    }

    public StoreSelectorAspectContentStore()
    {
    }

    /**
     * @param nodeService the service to access the properties
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param policyComponent register to receive updates to the <b>cm:storeSelector</b> aspect
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * @param dictionaryService used to check for content property types
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param storesByName a map of content stores keyed by a common name
     */
    public void setStoresByName(Map<String, ContentStore> storesByName)
    {
        this.storesByName = storesByName;
        this.stores = new ArrayList<ContentStore>(storesByName.values());
    }

    /**
     * @return Returns the stores keyed by store name
     */
    public Map<String, ContentStore> getStoresByName()
    {
        return storesByName;
    }

    /**
     * Set the name of the store to select if the content being created
     * is not associated with any specific value in the
     * <b>cm:storeSelector</b> or if the aspect is not present.
     * 
     * @param defaultStoreName the name of one of the stores
     * @see #setStoresByName(Map)
     */
    public void setDefaultStoreName(String defaultStoreName)
    {
        this.defaultStoreName = defaultStoreName;
    }

    public void init()
    {
        // Register on content update policy
        onContentMoveDelegate = policyComponent.registerClassPolicy(
                    StoreSelectorPolicies.AfterMoveContentPolicy.class);
        afterMoveContentDelegate = policyComponent.registerClassPolicy(
                    StoreSelectorPolicies.AfterMoveContentPolicy.class);
    }

    /**
     * Checks that the required properties are present
     */
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
        PropertyCheck.mandatory(this, "policyFilter", policyFilter);
        PropertyCheck.mandatory(this, "storesByName", storesByName);
        PropertyCheck.mandatory(this, "defaultStoreName", defaultStoreName);
        // Check that the default store name is valid
        if (storesByName.get(defaultStoreName) == null)
        {
            AlfrescoRuntimeException.create(ERR_INVALID_DEFAULT_STORE, defaultStoreName, storesByName.keySet());
        }
        // Register to receive change updates relevant to the aspect
        // Register to receive property change updates
        policyComponent.bindClassBehaviour(
                OnAddAspectPolicy.QNAME,
                ContentModel.ASPECT_STORE_SELECTOR,
                new JavaBehaviour(this, "onAddAspect"));
        policyComponent.bindClassBehaviour(
                OnUpdatePropertiesPolicy.QNAME,
                ContentModel.ASPECT_STORE_SELECTOR,
                new JavaBehaviour(this, "onUpdateProperties"));

        // Register to receive change updates when aspect is removed. Event used to
        // put the content back on the default store
        policyComponent.bindClassBehaviour(BeforeRemoveAspectPolicy.QNAME, ContentModel.ASPECT_STORE_SELECTOR,
                new JavaBehaviour(this, "beforeRemoveAspect"));

    }

    @Override
    protected List<ContentStore> getAllStores()
    {
        return stores;
    }

    @Override
    protected ContentStore selectWriteStore(ContentContext ctx)
    {
        ContentStore store;
        String storeNameProp;
        if (!(ctx instanceof NodeContentContext))
        {
            storeNameProp = "<NodeRef not available>";
            store = storesByName.get(defaultStoreName);
        }
        else
        {
            NodeRef nodeRef = ((NodeContentContext) ctx).getNodeRef(); // Never null
            storeNameProp = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_STORE_NAME);
            if (storeNameProp == null)
            {
                storeNameProp = "<null>";
                store = storesByName.get(defaultStoreName);
            }
            else
            {
                store = storesByName.get(storeNameProp);
                if (store == null)
                {
                    // There was no store with that name
                    storeNameProp = "<unmapped store: " + storeNameProp + ">";
                    store = storesByName.get(defaultStoreName);
                }
            }
        }
        // Done
        if (logger.isDebugEnabled())
        {
            logger.debug(
                    "ContentStore selected: \n" +
                    "   Node context:   " + ctx + "\n" +
                    "   Store name:     " + storeNameProp + "\n" +
                    "   Store Selected: " + store);
        }
        return store;
    }

    /**
     * Helper method to select a store, taking into account <tt>null</tt> and invalid values.
     */
    private ContentStore selectStore(String storeName)
    {
        if (storeName == null || !storesByName.containsKey(storeName))
        {
            storeName = defaultStoreName;
        }
        return storesByName.get(storeName);
    }

    /**
     * Ensures that all content is moved to the correct store.
     * <p>
     * Spoofs a call to {@link #onUpdateProperties(NodeRef, Map, Map)}.
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        Map<QName, Serializable> after = nodeService.getProperties(nodeRef);
        // Pass the call through. It is only interested in a single property.
        onUpdateProperties(nodeRef, Collections.<QName, Serializable> emptyMap(), after);
    }

    protected String safeCopyContent(ContentStore oldStore, ContentStore newStore, NodeContentContext oldCtx)
    {
        String oldUrl = oldCtx.getContentUrl();
        ContentReader reader = oldStore.getReader(oldUrl);
        if (reader == null || !reader.exists())
        {
            // Look for the content in the target store
            ContentReader newReader = newStore.getReader(oldUrl);
            // Nothing to copy
            if (newReader == null || !newReader.exists())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(
                            "URL not present in old or new stores: \n" +
                            "   Context:   " + oldCtx + "\n" +
                            "   Old Store: " + oldStore + "\n" +
                            "   New Store: " + newStore);
                }
                return null;
            }
            else
            {
                // The content is already in the new store
                return newReader.getContentUrl();
            }
        }
        // copy
        ContentContext ctx = new NodeContentContext(null, null, oldCtx.getNodeRef(), oldCtx.getPropertyQName());
        ContentWriter writer = newStore.getWriter(ctx);
        // Copy it
        writer.putContent(reader);
        return writer.getContentUrl();

    }

    /**
     * Called after an <b>aspect</b> has been removed from a node
     * <p>
     * Reponsibility of the method is to put the content back on the default store after the aspect is removed.
     * 
     * @param nodeRef the node from which the aspect will be removed
     * @param aspectTypeQName the type of the aspect
     */
    public void beforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        Map<QName, Serializable> before = nodeService.getProperties(nodeRef);
        Map<QName, Serializable> after = new HashMap<QName, Serializable>(before);
        after.put(ContentModel.PROP_STORE_NAME, defaultStoreName);
        onUpdateProperties(nodeRef, before, after);
    }

    /**
     * Keeps the content in the correct store based on changes to the <b>cm:storeName</b> property
     */
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        String storeNameBefore = (String) before.get(ContentModel.PROP_STORE_NAME);
        String storeNameAfter = (String) after.get(ContentModel.PROP_STORE_NAME);
        if (EqualsHelper.nullSafeEquals(storeNameBefore, storeNameAfter))
        {
            // We're not interested in the change
            return;
        }

        // Find out which store to move the content to
        ContentStore oldStore = selectStore(storeNameBefore);
        ContentStore newStore = selectStore(storeNameAfter);
        // Don't do anything if the store did not change
        if (oldStore == newStore)
        {
            return;
        }
        // take a copy of the properties and update in parallel
        Map<QName, Serializable> newProperties = new HashMap<QName, Serializable>(after);

        for (QName propertyQName : after.keySet())
        {
            PropertyDefinition propDef = dictionaryService.getProperty(propertyQName);
            if (propDef == null)
            {
                // Ignore
                continue;
            }
            if (!propDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
            {
                // It is not content
                continue;
            }
            // The property value
            Serializable propertyValue = after.get(propertyQName);
            if (propertyValue == null)
            {
                // Ignore missing values
                continue;
            }
            // Get the content URLs, being sensitive to collections
            if (propDef.isMultiValued())
            {
                Collection<ContentData> contentValues = DefaultTypeConverter.INSTANCE.getCollection(
                        ContentData.class,
                        propertyValue);
                if (contentValues.size() == 0)
                {
                    // No content
                    continue;
                }
                // buid a new collection of content data
                Collection<ContentData> newContentValues = new ArrayList<ContentData>();
                for (ContentData contentValue : contentValues)
                {
                    String contentUrl = contentValue.getContentUrl();
                    ContentData newContentData = null;
                    if (contentUrl != null)
                    {
                        NodeContentContext ctx = new NodeContentContext(null, contentUrl, nodeRef, propertyQName);
                        String newContentUrl = safeCopyContent(oldStore, newStore, ctx);

                        if (newContentUrl == null)
                        {
                            ContentReader reader = newStore.getReader(contentUrl);
                            if (reader != null && reader.exists())
                            {
                                // ALF-8815: Content is already in new store. This could happen if a node has been copied and we are setting its store name for the first time
                                newContentUrl = contentUrl;
                            }
                            else
                            {
                                for (ContentStore store : getAllStores())
                                {
                                    // check if content Url exists in any other store
                                    if (store.exists(ctx.getContentUrl()))
                                    {
                                        newContentUrl = safeCopyContent(store, newStore, ctx);
                                        break;
                                    }

                                }
                                if (newContentUrl == null)
                                {
                                    // Should never happen
                                    throw new RuntimeException("Content transfer failed from " + oldStore + " to " + newStore + " on node " + nodeRef);
                                }
                            }
                        }

                        newContentData = new ContentData(
                                newContentUrl,
                                contentValue.getMimetype(),
                                contentValue.getSize(),
                                contentValue.getEncoding(),
                                contentValue.getLocale());
                    }
                    else
                    {
                        // Should not happen
                        newContentValues.add(contentValue);
                    }
                    newContentValues.add(newContentData);
                }
                // add the newContentValues in the new property map
                newProperties.put(propertyQName, (Serializable) newContentValues);
            }
            else
            {
                ContentData contentValue = DefaultTypeConverter.INSTANCE.convert(ContentData.class, propertyValue);
                String contentUrl = contentValue.getContentUrl();
                if (contentUrl != null)
                {
                    NodeContentContext ctx = new NodeContentContext(null, contentUrl, nodeRef, propertyQName);
                    String newContentUrl = safeCopyContent(oldStore, newStore, ctx);
                    ContentData newContentData = null;
                    if (newContentUrl == null)
                    {
                        ContentReader reader = newStore.getReader(contentUrl);
                        if (reader != null && reader.exists())
                        {
                            // ALF-8815: Content is already in new store. This could happen if a node has been copied and we are setting its store name for the first time
                            newContentUrl = contentUrl;
                        }
                        else
                        {
                            for (ContentStore store : getAllStores())
                            {
                                // check if content Url exists in any other store
                                if (store.exists(ctx.getContentUrl()))
                                {
                                    newContentUrl = safeCopyContent(store, newStore, ctx);
                                    break;
                                }
                            }
                            if (newContentUrl == null)
                            {
                                // Should never happen
                                throw new RuntimeException(
                                        "Content transfer failed: \n" +
                                        "   Context:     " + ctx + "\n" +
                                        "   Old store: : " + storeNameBefore + " - " + oldStore + "\n" +
                                        "   New store:   " + storeNameAfter + " - " + newStore);
                            }
                        }
                    }
                    newContentData = new ContentData(
                            newContentUrl,
                            contentValue.getMimetype(),
                            contentValue.getSize(),
                            contentValue.getEncoding(),
                            contentValue.getLocale());
                    newProperties.put(propertyQName, newContentData);
                }
            }
        }
        policyFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_STORE_SELECTOR);
        try
        {
            // set the properties
            nodeService.setProperties(nodeRef, newProperties);
        }
        finally
        {
            policyFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_STORE_SELECTOR);
        }

        // Trigger policy after that content is moved
        Set<QName> types = getTypeAndAspectQNames(nodeRef);
        StoreSelectorPolicies.AfterMoveContentPolicy policyDep = onContentMoveDelegate.get(nodeRef, types);
        policyDep.afterMoveContent(nodeRef, after, newProperties);
        StoreSelectorPolicies.AfterMoveContentPolicy policy = afterMoveContentDelegate.get(nodeRef, types);
        policy.afterMoveContent(nodeRef, after, newProperties);
    }

    /**
     * Get all aspect and node type qualified names
     * 
     * @param nodeRef the node we are interested in
     * @return Returns a set of qualified names containing the node type and all the node aspects, or null if the node
     *         no longer exists
     */
    protected Set<QName> getTypeAndAspectQNames(NodeRef nodeRef)
    {
        Set<QName> qnames = null;
        try
        {
            Set<QName> aspectQNames = nodeService.getAspects(nodeRef);

            QName typeQName = nodeService.getType(nodeRef);

            qnames = new HashSet<QName>(aspectQNames.size() + 1);
            qnames.addAll(aspectQNames);
            qnames.add(typeQName);
        }
        catch (InvalidNodeRefException e)
        {
            qnames = Collections.emptySet();
        }
        // done
        return qnames;
    }

    /**
     * A constraint that acts as a list of values, where the values are the store names injected into the
     * {@link StoreSelectorAspectContentStore}.
     * <p>
     * If the store is not active or is incorrectly configured, then this constraint will contain a single value of
     * 'Default'. Any attempt to set another value will lead to constraint failures.
     * 
     * @author Derek Hulley
     * @since 3.2
     */
    public static class StoreSelectorConstraint extends ListOfValuesConstraint
    {
        private StoreSelectorAspectContentStore store;

        /**
         * Required default constructor
         */
        public StoreSelectorConstraint()
        {
        }

        public void setStore(StoreSelectorAspectContentStore store)
        {
            this.store = store;
        }

        @Override
        public void initialize()
        {
            checkPropertyNotNull("store", store);
            List<String> allowedValues = new ArrayList<String>(store.getStoresByName().keySet());
            super.setAllowedValues(allowedValues);
            // Now initialize as we have set the LOV
            super.initialize();
        }
    }
}
