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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.filesys.server.filesys.AccessDeniedException;
import org.alfresco.filesys.server.filesys.FileAttribute;
import org.alfresco.filesys.server.filesys.FileInfo;
import org.alfresco.filesys.server.filesys.FileOpenParams;
import org.alfresco.filesys.server.filesys.NetworkFile;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.RandomAccessContent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.Content;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * First-pass implementation of the <tt>NetworkFile</tt> for direct interaction
 * with the channel repository.
 * 
 * @author Derek Hulley
 */
public class ContentNetworkFile extends NetworkFile
{
    private static final Log logger = LogFactory.getLog(ContentNetworkFile.class);
    
    private ServiceRegistry serviceRegistry;
    private CifsHelper cifsHelper;
    private NodeRef nodeRef;
    /** keeps track of the read/write access */
    private FileChannel channel;
    /** the original content opened */
    private Content content;
    /** keeps track of any writes */
    private boolean modified;
    
    /**
     * Helper method to create a {@link NetworkFile network file} given a node reference.
     * 
     * @param serviceRegistry
     * @param filePathCache used to speed up repeated searches
     * @param nodeRef the node representing the file or directory
     * @param params the parameters dictating the path and other attributes with which the file is being accessed
     * @return Returns a new instance of the network file
     */
    public static ContentNetworkFile createFile(
            ServiceRegistry serviceRegistry,
            CifsHelper cifsHelper,
            NodeRef nodeRef,
            FileOpenParams params)
    {
        String path = params.getPath();
        
        // Check write access
        // TODO: Check access writes and compare to write requirements
        
        // create the file
        ContentNetworkFile netFile = new ContentNetworkFile(serviceRegistry, cifsHelper, nodeRef, path);
        // set relevant parameters
        if (params.isReadOnlyAccess())
        {
            netFile.setGrantedAccess(NetworkFile.READONLY);
        }
        else
        {
            netFile.setGrantedAccess(NetworkFile.READWRITE);
        }
        
        // check the type
        FileInfo fileInfo;
        try
        {
            fileInfo = cifsHelper.getFileInformation(nodeRef, "", true);
        }
        catch (FileNotFoundException e)
        {
            throw new AlfrescoRuntimeException("File not found when creating network file: " + nodeRef, e);
        }
        if (fileInfo.isDirectory())
        {
            netFile.setAttributes(FileAttribute.Directory);
        }
        else
        {
            // Set the current size
            
            netFile.setFileSize(fileInfo.getSize());
        }
        
        // Set the file timestamps
        
        if ( fileInfo.hasCreationDateTime())
            netFile.setCreationDate( fileInfo.getCreationDateTime());
        
        if ( fileInfo.hasModifyDateTime())
            netFile.setModifyDate(fileInfo.getModifyDateTime());
        
        if ( fileInfo.hasAccessDateTime())
            netFile.setAccessDate(fileInfo.getAccessDateTime());
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Created network file: \n" +
                    "   node: " + nodeRef + "\n" +
                    "   param: " + params + "\n" +
                    "   netfile: " + netFile);
        }
        return netFile;
    }

    private ContentNetworkFile(ServiceRegistry serviceRegistry, CifsHelper cifsHelper, NodeRef nodeRef, String name)
    {
        super(name);
        setFullName(name);
        this.serviceRegistry = serviceRegistry;
        this.cifsHelper = cifsHelper;
        this.nodeRef = nodeRef;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append("ContentNetworkFile:")
          .append("[ node=").append(nodeRef)
          .append(", channel=").append(channel)
          .append(", writable=").append(isWritable())
          .append(", content=").append(content)
          .append(", modified=").append(modified)
          .append("]");
        return sb.toString();
    }
    
    /**
     * @return Returns the node reference representing this file
     */
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    /**
     * @return Returns true if the channel should be writable
     * 
     * @see NetworkFile#getGrantedAccess()
     * @see NetworkFile#READONLY
     * @see NetworkFile#WRITEONLY
     * @see NetworkFile#READWRITE
     */
    private boolean isWritable()
    {
        // check that we are allowed to write
        int access = getGrantedAccess();
        return (access == NetworkFile.READWRITE || access == NetworkFile.WRITEONLY);
    }
    
    /**
     * Opens the channel for reading or writing depending on the access mode.
     * <p>
     * If the channel is already open, it is left.
     * 
     * @param write true if the channel must be writable
     * @throws AccessDeniedException if this network file is read only
     * @throws AlfrescoRuntimeException if this network file represents a directory
     *
     * @see NetworkFile#getGrantedAccess()
     * @see NetworkFile#READONLY
     * @see NetworkFile#WRITEONLY
     * @see NetworkFile#READWRITE
     */
    private synchronized void openContent(boolean write) throws AccessDeniedException, AlfrescoRuntimeException
    {
        if (isDirectory())
        {
            throw new AlfrescoRuntimeException("Unable to open channel for a directory network file: " + this);
        }
        else if (channel != null)
        {
            // already have channel open
            return;
        }
        
        // we need to create the channel
        ContentService contentService = serviceRegistry.getContentService();
        
        if (write && !isWritable())
        {
            throw new AccessDeniedException("The network file was created for read-only: " + this);
        }

        content = null;
        if (write)
        {
            content = contentService.getWriter(nodeRef);
        }
        else
        {
            content = contentService.getReader(nodeRef);
            if (content == null)  // no content on node
            {
                // give it an empty file
                content = contentService.getTempWriter();
            }
        }
        // wrap the channel accessor, if required
        if (!(content instanceof RandomAccessContent))
        {
            // TODO: create a temp, random access file and put a FileContentWriter on it
            //       barf for now
            throw new AlfrescoRuntimeException("Can only use a store that supplies randomly accessible channel");
        }
        RandomAccessContent randAccessContent = (RandomAccessContent) content;
        // get the channel - we can only make this call once
        channel = randAccessContent.getChannel();
    }

    @Override
    public synchronized void closeFile() throws IOException
    {
        if (isDirectory())              // ignore if this is a directory
        {
            return;
        }
        else if (channel == null)       // ignore if the channel hasn't been opened
        {
            return;
        }
        else if (modified)              // file was modified
        {
            // write properties
            NodeService nodeService = serviceRegistry.getNodeService();
            nodeService.setProperty(nodeRef, ContentModel.PROP_CONTENT_URL, content.getContentUrl());
            nodeService.setProperty(nodeRef, ContentModel.PROP_SIZE, channel.size());
            // close it
            channel.close();
            channel = null;
        }
        else
        {
            // close it - it was not modified
            channel.close();
            channel = null;
        }
    }

    @Override
    public synchronized void truncateFile(long size) throws IOException
    {
        // open the channel for writing
        openContent(true);
        // truncate the channel
        channel.truncate(size);
        // set modification flag
        modified = true;
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Truncated channel: " +
                    "   net file: " + this + "\n" +
                    "   size: " + size);
        }
    }

    @Override
    public synchronized void writeFile(byte[] buffer, int length, int position, long fileOffset) throws IOException
    {
        // open the channel for writing
        openContent(true);
        // write to the channel
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, position, length);
        int count = channel.write(byteBuffer, fileOffset);
        // set modification flag
        modified = true;
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Wrote to channel: " +
                    "   net file: " + this + "\n" +
                    "   written: " + count);
        }
    }

    @Override
    public synchronized int readFile(byte[] buffer, int length, int position, long fileOffset) throws IOException
    {
        // open the channel for reading, open for read/write access if that is how the file was opened
        openContent(getGrantedAccess() == NetworkFile.READWRITE);
        
        // read from the channel
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, position, length);
        int count = channel.read(byteBuffer, fileOffset);
        if (count < 0)
        {
            count = 0;  // doesn't obey the same rules, i.e. just returns the bytes read
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Read from channel: " +
                    "   net file: " + this + "\n" +
                    "   read: " + count);
        }
        return count;
    }
    
    @Override
    public synchronized void openFile(boolean createFlag) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized long seekFile(long pos, int typ) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void flushFile() throws IOException
    {
        // open the channel for writing
        openContent(true);
        // flush the channel - metadata flushing is not important
        channel.force(false);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Flushed channel: " +
                    "   net file: " + this);
        }
    }
}
