/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.enterprise.repo.bulkimport.impl;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.bulkimport.impl.stores.StoreSelectorContentStoreMapProvider;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.bulkimport.ContentDataFactory;
import org.alfresco.repo.bulkimport.ContentStoreMapProvider;
import org.alfresco.repo.bulkimport.ImportableItem;
import org.alfresco.repo.bulkimport.MetadataLoader;
import org.alfresco.repo.bulkimport.NodeImporter;
import org.alfresco.repo.bulkimport.impl.AbstractNodeImporter;
import org.alfresco.repo.bulkimport.impl.AbstractNodeImporterFactory;
import org.alfresco.repo.bulkimport.impl.BulkImportStatusImpl.NodeState;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Triple;

/**
 * Constructs in place node importers tied to a specific store.
 * 
 * @since 4.0
 *
 */
public class InPlaceNodeImporterFactory extends AbstractNodeImporterFactory
{
    protected StoreSelectorContentStoreMapProvider storeMapProvider;
    protected String defaultStoreName;
    protected ContentDataFactory contentDataFactory;

    public void setStoreMapProvider(StoreSelectorContentStoreMapProvider storeMapProvider)
    {
        this.storeMapProvider = storeMapProvider;
    }

    public void setDefaultStoreName(String defaultStoreName)
    {
        this.defaultStoreName = defaultStoreName;
    }

    public void setContentDataFactory(ContentDataFactory contentDataFactory)
    {
        this.contentDataFactory = contentDataFactory;
    }

    public NodeImporter getNodeImporter(String contentStoreName, String relativePath)
    {
        InPlaceNodeImporter nodeImporter = new InPlaceNodeImporter();
        nodeImporter.setNodeService(nodeService);
        nodeImporter.setBehaviourFilter(behaviourFilter);
        nodeImporter.setFileFolderService(fileFolderService);
        nodeImporter.setMetadataLoader(metadataLoader);
        nodeImporter.setVersionService(versionService);
        nodeImporter.setImportStatus(importStatus);

        nodeImporter.setContentDataFactory(contentDataFactory);
        nodeImporter.setDefaultStoreName(defaultStoreName);
        nodeImporter.setStoreMapProvider(storeMapProvider);

        nodeImporter.setContentStoreName(contentStoreName);
        nodeImporter.setRelativePath(relativePath);

        nodeImporter.init();

        return nodeImporter;
    }

    /**
     * An in place node importer leaves the source files in their original location i.e. doesn't copy them into the main content store directory. It uses the storeSelector aspect
     * to annotate the node with its store location.
     * 
     * @since 4.0
     */
    private static class InPlaceNodeImporter extends AbstractNodeImporter
    {
        protected ContentDataFactory contentDataFactory;
        protected String defaultStoreName;
        protected ContentStoreMapProvider storeMapProvider;

        protected String contentStoreName;
        protected String relativePath;

        protected ContentStore store;

        public void setStoreMapProvider(ContentStoreMapProvider storeMapProvider)
        {
            this.storeMapProvider = storeMapProvider;
        }

        public void setContentStoreName(String contentStoreName)
        {
            this.contentStoreName = contentStoreName;
        }

        public void setRelativePath(String relativePath)
        {
            this.relativePath = relativePath;
        }

        public void setBehaviourFilter(BehaviourFilter behaviourFilter)
        {
            this.behaviourFilter = behaviourFilter;
        }

        public void setContentDataFactory(ContentDataFactory contentDataFactory)
        {
            this.contentDataFactory = contentDataFactory;
        }

        public void setDefaultStoreName(String defaultStoreName)
        {
            this.defaultStoreName = defaultStoreName;
        }

        public void init()
        {
            this.store = storeMapProvider.checkAndGetStore(contentStoreName);
        }

        protected void onContentAndMetadataImport(ImportableItem.ContentAndMetadata revision, MetadataLoader.Metadata metadata)
        {
            /** Get the {@link ContentData} corresponding to this (storeName, ContentStore, relativeFilePath), out of the provided {@link ContentDataFactory} */
            File contentFile = revision.getContentFile();
            if (!contentFile.isDirectory())
            {
                ContentData contentData = contentDataFactory.createContentData(store, contentFile);
                metadata.addProperty(ContentModel.PROP_CONTENT, contentData);
            }
            else
            {
                // not called for directories
            }
        }

        protected void afterCreateNode(NodeRef nodeRef)
        {
            handleStoreSelector(nodeRef);
        }

        protected void handleStoreSelector(NodeRef nodeRef)
        {
            // TODO is contentStoreName being set correctly here? Where should it really come from? Check with original source.
            // => I think it comes from webscript target content store i.e. it's selected by the user and must match one defined for
            // the bean "storeSelectorContentStore"
            // handle store selector aspect separately
            String storeName = contentStoreName;
            QName storeSelectorAspectQName = ContentModel.ASPECT_STORE_SELECTOR;
            QName storeNamePropertyQName = ContentModel.PROP_STORE_NAME;

            String currentStore = defaultStoreName;
            if (nodeService.hasAspect(nodeRef, storeSelectorAspectQName))
            {
                currentStore = (String) nodeService.getProperty(nodeRef, storeNamePropertyQName);
            }

            if ((!storeName.equals(defaultStoreName)) && (!storeName.equals(currentStore))) // keeping case sensitivity here
            {
                try
                {
                    behaviourFilter.disableBehaviour(nodeRef, storeSelectorAspectQName); // don't trigger a store move as we're importing directly in it

                    Map<QName, Serializable> selectorProps = new HashMap<QName, Serializable>();
                    selectorProps.put(ContentModel.PROP_STORE_NAME, storeName);
                    nodeService.addAspect(nodeRef, storeSelectorAspectQName, selectorProps);

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("added store selector with store :'" + storeName + "' to new node : " + nodeRef.toString());
                    }
                }
                finally
                {
                    behaviourFilter.enableBehaviour(nodeRef, storeSelectorAspectQName);
                }
            }
        }

        protected final void importContentAndMetadata(NodeRef nodeRef, ImportableItem.ContentAndMetadata contentAndMetadata, MetadataLoader.Metadata metadata)
        {
            // For the in place importer, the content is not streamed through the content store via a ContentWriter.
            // Instead, the content URL property is set by providing a ContentData object that wraps its enclosing store and the location of the actual file
            // relatively to the store's root.
            onContentAndMetadataImport(contentAndMetadata, metadata);

            // Attach aspects and set all properties
            importImportableItemMetadata(nodeRef, contentAndMetadata.getContentFile(), metadata);
        }

        // Note: replaceExisting is ignored for in-place, since the content is already in the content store.
        protected NodeRef importImportableItemImpl(ImportableItem importableItem, boolean replaceExisting)
        {
            NodeRef target = importableItem.getParent().getNodeRef();
            if (target == null)
            {
                // the parent has not been created yet, retry
                throw new AlfrescoRuntimeException("Bulk importer: target is not known for importable item: " + importableItem.getParent());
            }
            NodeRef result = null;
            MetadataLoader.Metadata metadata = loadMetadata(importableItem.getHeadRevision());

            Triple<NodeRef, Boolean, NodeState> node = createOrFindNode(target, importableItem, replaceExisting, metadata);
            boolean isDirectory = node.getSecond() == null ? false : node.getSecond(); // Watch out for NPEs during unboxing!
            NodeState nodeState = node.getThird();

            result = node.getFirst();

            if (result != null && nodeState != NodeState.SKIPPED)
            {
                //call afterCreateNode only if node wasn't skipped
                afterCreateNode(result);

                int numVersionProperties = 0;

                importStatus.incrementImportableItemsRead(importableItem, isDirectory);

                // Load the item
                if (isDirectory)
                {
                    importImportableItemDirectory(result, importableItem, metadata);
                }
                else
                {
                    numVersionProperties = importImportableItemFile(result, importableItem, metadata, nodeState);
                }

                importStatus.incrementNodesWritten(importableItem, isDirectory, nodeState, metadata.getProperties().size() + 4, numVersionProperties);
            }
            else
            {
                if (isDirectory)
                {
                    skipImportableDirectory(importableItem);
                }
                else
                {
                    skipImportableFile(importableItem);
                }
            }

            return (result);
        }

        @Override
        public File getSourceFolder()
        {
            File file = new File(store.getRootLocation(), relativePath);
            return file;
        }
    }
}
