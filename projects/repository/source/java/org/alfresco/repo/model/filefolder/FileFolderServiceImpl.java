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
package org.alfresco.repo.model.filefolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the file/folder-specific service.
 * 
 * @author Derek Hulley
 */
public class FileFolderServiceImpl implements FileFolderService
{
    /** Shallow search for all files */
    private static final String XPATH_QUERY_SHALLOW_FILES =
        "./*" +
        "[(subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** Shallow search for all folder */
    private static final String XPATH_QUERY_SHALLOW_FOLDERS =
        "./*" +
        "[not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "'))]";
    
    /** Shallow search for all files and folders */
    private static final String XPATH_QUERY_SHALLOW_ALL =
        "./*" +
        "[like(@cm:name, $cm:name, false)" +
        " and not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "') or subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** Deep search for files and folders with a name pattern */
    private static final String XPATH_QUERY_DEEP_ALL =
        ".//*" +
        "[like(@cm:name, $cm:name, false)" +
        " and not (subtypeOf('" + ContentModel.TYPE_SYSTEM_FOLDER + "'))" +
        " and (subtypeOf('" + ContentModel.TYPE_FOLDER + "') or subtypeOf('" + ContentModel.TYPE_CONTENT + "'))]";
    
    /** empty parameters */
    private static final QueryParameterDefinition[] PARAMS_EMPTY = new QueryParameterDefinition[0];
    private static final QueryParameterDefinition[] PARAMS_ANY_NAME = new QueryParameterDefinition[1];
    
    private static Log logger = LogFactory.getLog(FileFolderServiceImpl.class);

    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private NodeService unprotectedNodeService;
    private SearchService searchService;
    
    private QName cmName;

    /**
     * Default constructor
     */
    public FileFolderServiceImpl()
    {
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setUnprotectedNodeService(NodeService unprotectedNodeService)
    {
        this.unprotectedNodeService = unprotectedNodeService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    public void init()
    {
        PARAMS_ANY_NAME[0] = new QueryParameterDefImpl(
                ContentModel.PROP_NAME,
                dictionaryService.getDataType(DataTypeDefinition.TEXT),
                true,
                "%");
    }

    /**
     * Helper method to convert node reference instances to file info
     * 
     * @param nodeRefs the node references
     * @return Return a list of file info
     */
    private List<FileInfo> toFileInfo(List<NodeRef> nodeRefs)
    {
        List<FileInfo> results = new ArrayList<FileInfo>(nodeRefs.size());
        for (NodeRef nodeRef : nodeRefs)
        {
            // get the file attributes
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
            String name = (String) properties.get(ContentModel.PROP_NAME);
            // is it a folder
            QName typeQName = unprotectedNodeService.getType(nodeRef);
            boolean isFolder = dictionaryService.isSubClass(typeQName, ContentModel.TYPE_FOLDER);
            
            // construct the file info and add to the results
            FileInfo fileInfo = new FileInfoImpl(nodeRef, isFolder, name);
            results.add(fileInfo);
        }
        return results;
    }
    
    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> list(NodeRef folderNodeRef)
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_ALL,
                PARAMS_ANY_NAME,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for files and folders: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> listFiles(NodeRef folderNodeRef)
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_FILES,
                PARAMS_EMPTY,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for files: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * TODO: Use Lucene search to get file attributes without having to visit the node service
     */
    public List<FileInfo> listFolders(NodeRef folderNodeRef)
    {
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                XPATH_QUERY_SHALLOW_FOLDERS,
                PARAMS_EMPTY,
                namespaceService,
                false);
        // convert the noderefs
        List<FileInfo> results = toFileInfo(nodeRefs);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Shallow search for folders: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   results: " + results);
        }
        return results;
    }

    /**
     * @see #search(NodeRef, String, boolean, boolean, boolean)
     */
    public List<FileInfo> search(NodeRef folderNodeRef, String namePattern, boolean includeSubFolders)
    {
        return search(folderNodeRef, namePattern, true, true, includeSubFolders);
    }

    /**
     * Full search with all options
     */
    public List<FileInfo> search(
            NodeRef folderNodeRef,
            String namePattern,
            boolean fileSearch,
            boolean folderSearch,
            boolean includeSubFolders)
    {
        // shortcut if the search is requesting nothing
        if (!fileSearch && !folderSearch)
        {
            return Collections.emptyList();
        }
        
        // if the name pattern is null, then we use the ANY pattern
        QueryParameterDefinition[] params = null;
        if (namePattern != null)
        {
            // the interface specifies the Lucene syntax, so perform a conversion
            namePattern = SearchLanguageConversion.convert(
                    SearchLanguageConversion.DEF_LUCENE,
                    SearchLanguageConversion.DEF_XPATH_LIKE,
                    namePattern);
            
            params = new QueryParameterDefinition[1];
            params[0] = new QueryParameterDefImpl(
                    ContentModel.PROP_NAME,
                    dictionaryService.getDataType(DataTypeDefinition.TEXT),
                    true,
                    namePattern);
        }
        else
        {
            params = PARAMS_ANY_NAME;
        }
        // determine the correct query to use
        String query = null;
        if (includeSubFolders)
        {
            query = XPATH_QUERY_DEEP_ALL;
        }
        else
        {
            query = XPATH_QUERY_SHALLOW_ALL;
        }
        // execute the query
        List<NodeRef> nodeRefs = searchService.selectNodes(
                folderNodeRef,
                query,
                params,
                namespaceService,
                false);
        List<FileInfo> results = toFileInfo(nodeRefs);
        // eliminate unwanted files/folders
        Iterator<FileInfo> iterator = results.iterator(); 
        while (iterator.hasNext())
        {
            FileInfo file = iterator.next();
            if (file.isFolder() && !folderSearch)
            {
                iterator.remove();
            }
            else if (!file.isFolder() && !fileSearch)
            {
                iterator.remove();
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Deep search: \n" +
                    "   context: " + folderNodeRef + "\n" +
                    "   pattern: " + namePattern + "\n" +
                    "   files: " + fileSearch + "\n" +
                    "   folders: " + folderSearch + "\n" +
                    "   deep: " + includeSubFolders + "\n" +
                    "   results: " + results);
        }
        return results;
    }
}
