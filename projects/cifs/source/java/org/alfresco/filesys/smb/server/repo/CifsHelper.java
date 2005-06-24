/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.filesys.smb.server.repo;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.filesys.FileExistsException;
import org.alfresco.filesys.server.filesys.FileName;
import org.alfresco.filesys.util.WildCard;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class with helper methods.
 *  
 * @author derekh
 */
public class CifsHelper
{
    private static Log logger = LogFactory.getLog(CifsHelper.class);
    
    /**
     * Creates a file or directory using the given paths.
     * <p>
     * If the directory path doesn't exist, then all the parent directories will be created.
     * If the file path is <code>null</code>, then the file will not be created
     * 
     * @param serviceRegistry used to connect to the repo
     * @param rootNodeRef the root node of the path
     * @param path the path to a node
     * @param isFile true if the node to be created must be a file
     * @return Returns a newly created file or folder node
     * @throws FileExistsException if the file or folder already exists
     */
    public static NodeRef createNode(
            ServiceRegistry serviceRegistry,
            NodeRef rootNodeRef,
            String path,
            boolean isFile) throws FileExistsException
    {
        MimetypeService mimetypeService = serviceRegistry.getMimetypeService();
        NodeService nodeService = serviceRegistry.getNodeService();
        
        // start a transaction for this
        UserTransaction txn = serviceRegistry.getUserTransaction();
        NodeRef currentNodeRef = rootNodeRef;
        try
        {
            txn.begin();
            
            // split the directory path up into its constituents
            StringTokenizer tokenizer = new StringTokenizer(path, FileName.DOS_SEPERATOR_STR, false);
            
            // walk the directories, creating them on the fly if required
            while (tokenizer.hasMoreTokens())
            {
                String pathElement = tokenizer.nextToken();
                
                // determine whether we are searching for a file or directory
                boolean lastToken = !tokenizer.hasMoreTokens();
                boolean fileToken = (isFile && lastToken);
                QName typeQName = fileToken ? ContentModel.TYPE_CONTENT : ContentModel.TYPE_FOLDER;
                
                String encodedPath = QName.createValidLocalName(pathElement);
                // check if the node exists
                try
                {
                    // will throw FileNotFound if no node matches
                    NodeRef existingNodeRef = CifsHelper.getNodeRef(
                            serviceRegistry,
                            currentNodeRef,
                            pathElement);
                    // the existence of the node is only an issue if we are on the last token, i.e. there
                    // will be a name clash
                    if (lastToken)
                    {
                        throw new FileExistsException("Directory or file exists: \n" +
                                "   device root: " + rootNodeRef + "\n" +
                                "   path: " + path + "\n" +
                                "   existing dir: " + existingNodeRef);
                    }
                    else
                    {
                        // directory exists, but we are either creating a file or have more folders to go
                        // move onto the existing folder node
                        currentNodeRef = existingNodeRef;
                    }
                }
                catch (FileNotFoundException e)
                {
                    // we can go ahead and create the node as it doesn't exist
                    // set properties
                    Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
                    properties.put(ContentModel.PROP_NAME, pathElement);   // the path element acts as the full name
                    if (fileToken)
                    {
                        String mimetype = mimetypeService.guessMimetype(pathElement);
                        properties.put(ContentModel.PROP_MIME_TYPE, mimetype);
                    }
                    // create node
                    ChildAssociationRef assocRef = nodeService.createNode(
                            currentNodeRef,
                            ContentModel.ASSOC_CONTAINS,
                            QName.createQName(NamespaceService.ALFRESCO_URI, encodedPath),
                            typeQName,
                            properties);
                    currentNodeRef = assocRef.getChildRef();
                }
            }
            // commit
            txn.commit();
        }
        catch (Throwable e)
        {
            try
            {
                if (txn.getStatus() == Status.STATUS_ACTIVE)
                {
                    txn.rollback();
                }
            }
            catch (Throwable ee)
            {
                throw new AlfrescoRuntimeException("Failed to rollback transaction", e);
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created node: \n" +
                    "   device root: " + rootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   is file: " + isFile + "\n" +
                    "   new node: " + currentNodeRef);
        }
        return currentNodeRef;
    }

    /**
     * Finds the nodes being reference by the given directory and file paths.
     * <p>
     * Examples of the path are:
     * <ul>
     *   <li>\New Folder\New Text Document.txt</li>
     *   <li>\New Folder\Sub Folder</li>
     * </ul>
     * 
     * @param serviceRegistry used to connect to the repository
     * @param searchRootNodeRef the node from which to start the path search
     * @param path the search path to either a folder or file
     * @return Returns references to all matching nodes
     * @throws FileNotFoundException if no node could be found for the given parameters
     */
    public static List<NodeRef> getNodeRefs(
            ServiceRegistry serviceRegistry,
            NodeRef searchRootNodeRef,
            String path)
    {
        if (path.length() == 0)
        {
            // just return the search context
            return Collections.singletonList(searchRootNodeRef);
        }
        
        // get the required services
        NamespaceService namespaceService = serviceRegistry.getNamespaceService();
        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
        NodeService nodeService = serviceRegistry.getNodeService();
        
        // check for existence
        if (!nodeService.exists(searchRootNodeRef))
        {
            throw new AlfrescoRuntimeException("Search root node does not exist: " + searchRootNodeRef);
        }
        
        // split the directory path up to create the xpath
        StringTokenizer tokenizer = new StringTokenizer(path, FileName.DOS_SEPERATOR_STR, false);

        // helper variables
        ArrayList<QueryParameterDefinition> params = new ArrayList<QueryParameterDefinition>(tokenizer.countTokens() * 2);
        StringBuilder sb = new StringBuilder(tokenizer.countTokens() * 50);
        int tokenCount = 0;
        
        sb.append(".");
        while(tokenizer.hasMoreTokens())
        {
            // directory name
            String name = tokenizer.nextToken();
            boolean wildcardSearch = WildCard.containsWildcards(name);
            
            // determine whether we are searching for a file or directory
            boolean lastToken = !tokenizer.hasMoreTokens();
            
            String nameParam = ("alf:name" + tokenCount);
            String folderTypeParam = ("alf:foldertype" + tokenCount);
            String fileTypeParam = ("alf:filetype" + tokenCount);
            // append to the xpath
            if (wildcardSearch)
            {
                // use the like function (do not match FTS)
                sb.append("/*[like(@alf:name, $").append(nameParam).append(", false)");
            }
            else
            {
                sb.append("/*[@alf:name = $").append(nameParam);
            }
            sb.append(" and ")
              .append("(")
              .append("subtypeOf($").append(folderTypeParam).append(")")
              .append(" or ")
              .append("subtypeOf($").append(fileTypeParam).append(")")
              .append(")]");
            // create the query parameters
            params.add(new QueryParameterDefImpl(
                    QName.createQName(nameParam, namespaceService),
                    dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                    true,
                    name));
            params.add(new QueryParameterDefImpl(
                    QName.createQName(folderTypeParam, namespaceService),
                    dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                    true,
                    ContentModel.TYPE_FOLDER.toString()));
            params.add(new QueryParameterDefImpl(
                    QName.createQName(fileTypeParam, namespaceService),
                    dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT),
                    true,
                    ContentModel.TYPE_CONTENT.toString()));
            // advance token count
            tokenCount++;
        }
        
        QueryParameterDefinition[] queryParams = new QueryParameterDefinition[params.size()];
        queryParams = params.toArray(queryParams);
        String xpath = sb.toString();
        // execute the query
        List<NodeRef> nodes = nodeService.selectNodes(
                searchRootNodeRef,
                xpath,
                queryParams,
                namespaceService,
                false);

        // done - the context node that we end with is the last node on the path
        if (logger.isDebugEnabled())
        {
            logger.debug("Found node: \n" +
                    "   search context: " + searchRootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   nodes: " + nodes);
        }
        return nodes;
    }
    
    /**
     * Helper method to perform a Lucene-based search directly against the path provided.
     * Since the CIFS path is really just a sequence of unique node names, there is
     * no guarantee that it equates directly to the path.  Also, if the path contains wildcard
     * characters, then a single node request is invalid and no result will be returned.
     * <p>
     * Essentially, if the method returns a result, you can be guaranteed that it is the correct
     * result.  If the method returns <code>null</code>, it is NOT a guarantee that the path
     * can't be resolved to a node by another means, example by walking the hierarchy and
     * comparing the node names directly to the path elements.
     * <p>
     * However, most searches are for a specific file occuring below a set of folders that have
     * fairly short (<100 characters) names.  Under these conditions, the path search will be
     * unique.
     * 
     * @param serviceRegistry 
     * @param searchRootNodeRef the parent node from which to search
     * @param path a path to search.  This is the standard CIFS path
     * @return Returns the node referred to by the path, or null if not found.  Null will
     *      also be returned if the path uniqueness can't be guaranteed.
     */
    private static NodeRef getNodeRefFast(ServiceRegistry serviceRegistry, NodeRef searchRootNodeRef, String path)
    {
        // check for any wildcards and drop out directly if there are
        if (WildCard.containsWildcards(path))
        {
            return null;
        }
        
        // get required services
        NamespaceService namespaceService = serviceRegistry.getNamespaceService();
        NodeService nodeService = serviceRegistry.getNodeService();
        SearchService searchService = serviceRegistry.getSearchService();
        
        // build a Lucene query
        StringBuilder sb = new StringBuilder(250);
        
        // open the path literal
        sb.append("PATH:'");
        
        // get the path of the search root node and start the search path with this
        Path rootPath = nodeService.getPath(searchRootNodeRef);
        
        
        StringTokenizer tokenizer = new StringTokenizer(path, FileName.DOS_SEPERATOR_STR);
        while (tokenizer.hasMoreTokens())
        {
            String pathElement = tokenizer.nextToken();
            // normalise the path element.  This is where the discrepancy can arise between
            // the node's name and the path.  Mostly, when searching for 
            // stick it onto the
            String validPathElement = QName.createValidLocalName(pathElement);
            QName pathQName = QName.createQName("alf", validPathElement, namespaceService);
            
            // build the path
            sb.append('/').append(pathQName);
        }
        
        // close the path literal
        sb.append('\'');
        
        StoreRef storeRef = searchRootNodeRef.getStoreRef();
        String search = sb.toString();
        ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, search);
        // More than one results means that we don't have a unique path
        // This breaks the method contract so we have to return no result
        if (rs.length() == 0 || rs.length() > 1)
        {
            // no results
            return null;
        }
        // we have exactly one result - the path worked
        NodeRef result = rs.getNodeRef(0);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Found single result using Lucene search: \n" +
                    "   search root: " + searchRootNodeRef + "\n" +
                    "   path: " + path + "\n" +
                    "   lucene: " + search + "\n" +
                    "   result: " + result);
        }
        return result;
    }
    
    /**
     * Attempts to fetch a specific single node at the given path.
     * <p>
     * The first pass search attempts to use Lucene to extract a single node by
     * using the full path.  In the event that this pulls back multiple nodes,
     * the more expensive XPath hierarchy walking will be used.
     * 
     * @throws FileNotFoundException if the path can't be resolved to a node
     * 
     * @see #getNodeRefFast(ServiceRegistry, NodeRef, String)
     * @see #getNodeRefs(ServiceRegistry, NodeRef, String, boolean)
     */
    public static NodeRef getNodeRef(
            ServiceRegistry serviceRegistry,
            NodeRef searchRootNodeRef,
            String path) throws FileNotFoundException
    {
        // first attempt to get the node using the faster direct path search
        NodeRef nodeRef = CifsHelper.getNodeRefFast(serviceRegistry, searchRootNodeRef, path);
        if (nodeRef != null)
        {
            // the faster query worked
            return nodeRef;
        }
        
        // attempt to get the file/folder node using hierarchy walking
        List<NodeRef> nodeRefs = CifsHelper.getNodeRefs(
                serviceRegistry,
                searchRootNodeRef,
                path);
        if (nodeRefs.size() == 0)
        {
            throw new FileNotFoundException("Unable to find file: \n" +
                    "   search root: " + searchRootNodeRef + "\n" +
                    "   path: " + path);
        }
        else if (nodeRefs.size() > 1)
        {
            logger.warn("Multiple matching nodes: \n" +
                    "   search root: " + searchRootNodeRef + "\n" +
                    "   path: " + path);
        }
        // take the first one - not sure if it is possible for the path to refer to more than one
        nodeRef = nodeRefs.get(0);
        // done
        return nodeRef;
    }

}
