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
 *
 * Created on 27-Jun-2005
 */
package org.alfresco.repo.importer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.acegisecurity.Authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Simple import of content into the repository
 * 
 * @author andyh
 */
public class FileImporterImpl implements FileImporter
{
    private static Logger s_logger = Logger.getLogger(FileImporterImpl.class);

    private AuthenticationService authenticationService;

    private NodeService nodeService;

    private ContentService contentService;

    private MimetypeService mimetypeService;

    public FileImporterImpl()
    {
        super();
    }

    public void loadFile(NodeRef container, File file, boolean recurse) throws FileImporterException
    {
        create(container, file, null, recurse);
    }

    public void loadFile(NodeRef container, File file, FileFilter filter, boolean recurse) throws FileImporterException
    {
        create(container, file, filter, recurse);
    }

    public void loadFile(NodeRef container, File file) throws FileImporterException
    {
        create(container, file, null, false);
    }

    private NodeRef create(NodeRef container, File file, FileFilter filter, boolean recurse)
    {
        if (file.isDirectory())
        {
            NodeRef directoryNodeRef = createDirectory(container, file);
            
            if(recurse)
            {
                File[] files = ((filter == null) ? file.listFiles() : file.listFiles(filter));
                for(int i = 0; i < files.length; i++)
                {
                    create(directoryNodeRef, files[i], filter, recurse);
                }
            }
            
            return directoryNodeRef;
        }
        else
        {
            return createFile(container, file);
        }
    }

    private NodeRef createFile(NodeRef containerNodeRef, File file)
    {
        // create properties for content type
        Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(3, 1.0f);
        contentProps.put(ContentModel.PROP_NAME, file.getName());
        contentProps.put(ContentModel.PROP_ENCODING, "UTF-8");
        contentProps.put(ContentModel.PROP_MIME_TYPE, mimetypeService.guessMimetype(file.getName()));
        Authentication currentAuth = authenticationService.getCurrentAuthentication();
        contentProps.put(ContentModel.PROP_CREATOR, currentAuth == null ? "unknown" : currentAuth.getName());

        // create the node to represent the node
        String assocName = QName.createValidLocalName(file.getName());
        ChildAssociationRef assocRef = this.nodeService.createNode(containerNodeRef, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                ContentModel.TYPE_CONTENT, contentProps);

        NodeRef fileNodeRef = assocRef.getChildRef();

        if (s_logger.isDebugEnabled())
            s_logger.debug("Created file node for file: " + file.getName());

        // apply the titled aspect - title and description
        Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>(5);
        titledProps.put(ContentModel.PROP_TITLE, file.getName());

        titledProps.put(ContentModel.PROP_DESCRIPTION, file.getPath());

        this.nodeService.addAspect(fileNodeRef, ContentModel.ASPECT_TITLED, titledProps);

        if (s_logger.isDebugEnabled())
            s_logger.debug("Added titled aspect with properties: " + titledProps);

        // get a writer for the content and put the file
        ContentWriter writer = contentService.getUpdatingWriter(fileNodeRef);
        try
        {
            writer.putContent(new BufferedInputStream(new FileInputStream(file)));
        }
        catch (ContentIOException e)
        {
            throw new FileImporterException("Failed to load content from "+file.getPath(), e);
        }
        catch (FileNotFoundException e)
        {
            throw new FileImporterException("Failed to load content (file not found) "+file.getPath(), e);
        }

        return fileNodeRef;
    }

    private NodeRef createDirectory(NodeRef parentNodeRef, File file)
    {   
        String qname = QName.createValidLocalName(file.getName());
        ChildAssociationRef assocRef = this.nodeService.createNode(
              parentNodeRef,
              ContentModel.ASSOC_CONTAINS,
              QName.createQName(NamespaceService.ALFRESCO_URI, qname),
              ContentModel.TYPE_FOLDER);
        
        NodeRef nodeRef = assocRef.getChildRef();
        
        // set the name property on the node
        this.nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, file.getName());
        
        if (s_logger.isDebugEnabled())
           s_logger.debug("Created folder node with name: " + file.getName());

        // apply the uifacets aspect - icon, title and description props
        Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(5);
        uiFacetsProps.put(ContentModel.PROP_ICON, "space-icon-default");
        uiFacetsProps.put(ContentModel.PROP_TITLE, file.getName());
        uiFacetsProps.put(ContentModel.PROP_DESCRIPTION, file.getPath());
        this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_UIFACETS, uiFacetsProps);
        
        if (s_logger.isDebugEnabled())
           s_logger.debug("Added uifacets aspect with properties: " + uiFacetsProps);
        
        return nodeRef;
    }

    protected void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    

    protected void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    

    protected void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }
    

    protected void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    
    
}
