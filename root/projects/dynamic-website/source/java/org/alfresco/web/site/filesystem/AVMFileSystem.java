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
package org.alfresco.web.site.filesystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.SortedMap;

import javax.servlet.ServletContext;

import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.tools.DataUtil;
import org.springframework.util.FileCopyUtils;

/**
 * The Class AVMFileSystem.
 * 
 * @author muzquiano
 */
public class AVMFileSystem implements IFileSystem
{
    // avmStoreId = ads--admin
    // avmWebappPath = /www/avm_webapps/ROOT
    /**
     * Instantiates a new aVM file system.
     * 
     * @param avmRemote the avm remote
     * @param avmStoreId the avm store id
     * @param avmWebappPath the avm webapp path
     */
    public AVMFileSystem(AVMRemote avmRemote, String avmStoreId,
            String avmWebappPath)
    {
        setAVMRemote(avmRemote);
        setAVMStoreId(avmStoreId);
        setAVMWebappPath(avmWebappPath);
    }

    /**
     * Instantiates a new aVM file system.
     */
    public AVMFileSystem()
    {
    }

    /**
     * Sets the aVM remote.
     * 
     * @param avmRemote the new aVM remote
     */
    protected void setAVMRemote(AVMRemote avmRemote)
    {
        this.avmRemote = avmRemote;
    }

    /**
     * Sets the aVM store id.
     * 
     * @param avmStoreId the new aVM store id
     */
    protected void setAVMStoreId(String avmStoreId)
    {
        this.avmStoreId = avmStoreId;
    }

    /**
     * Sets the aVM webapp path.
     * 
     * @param avmWebappPath the new aVM webapp path
     */
    protected void setAVMWebappPath(String avmWebappPath)
    {
        this.avmWebappPath = avmWebappPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getRoot()
     */
    public IDirectory getRoot()
    {
        IFile file = getFile("/");
        if (file instanceof IDirectory)
            return ((IDirectory) file);
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getAbsolutePath(org.alfresco.web.site.filesystem.IFile)
     */
    public String getAbsolutePath(IFile file)
    {
        return getAVMPath(file.getPath());
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getInputStream(org.alfresco.web.site.filesystem.IFile)
     */
    public InputStream getInputStream(IFile file) throws Exception
    {
        String avmPath = getAVMPath(file.getPath());
        return this.avmRemote.getFileInputStream(-1, avmPath);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getOutputStream(org.alfresco.web.site.filesystem.IFile)
     */
    public OutputStream getOutputStream(IFile file) throws Exception
    {
        String avmPath = getAVMPath(file.getPath());
        return this.avmRemote.getFileOutputStream(avmPath);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getFile(java.lang.String)
     */
    public IFile getFile(String path)
    {
        IFile file = null;

        String avmPath = getAVMPath(path);
        AVMNodeDescriptor descriptor = getDescriptor(avmPath);
        if (descriptor != null)
        {
            if (descriptor.isDirectory())
                file = new AVMDirectory(this, descriptor, path);
            else
                file = new AVMFile(this, descriptor, path);
            return file;
        }
        return file;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getFile(java.lang.String, java.lang.String)
     */
    public IFile getFile(String path, String name)
    {
        return getFile(path + "/" + name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getFiles(java.lang.String)
     */
    public IFile[] getFiles(String path)
    {
        String avmPath = getAVMPath(path);
        AVMNodeDescriptor[] descriptors = getDescriptors(avmPath);
        if (descriptors == null)
            return null;
        IFile[] array = new IFile[descriptors.length];
        for (int i = 0; i < descriptors.length; i++)
        {
            String newPath = path + "/" + descriptors[i].getName();
            if (descriptors[i].isDirectory())
                array[i] = new AVMDirectory(this, descriptors[i], newPath);
            else
                array[i] = new AVMFile(this, descriptors[i], newPath);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#getParent(java.lang.String)
     */
    public IDirectory getParent(String path)
    {
        if (path == null)
            return null;
        if (path.length() > 1)
        {
            int i = path.lastIndexOf("/");
            String parentPath = path.substring(0, i);
            IDirectory dir = (IDirectory) getFile(parentPath);
            return dir;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#createFile(java.lang.String)
     */
    public IFile createFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return createFile(parentPath, fileName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#createFile(java.lang.String, java.lang.String)
     */
    public IFile createFile(String directoryPath, String fileName)
    {
        String avmDirectoryPath = getAVMPath(directoryPath);
        try
        {
            // TODO: This is a bad approach
            // It appears that the AVM API does post processing of the content
            // that
            // is written in order to determine the content type. Thus, we have
            // to put something into the file.
            // At the moment, we just write some test content so that the AVM
            // API determines this to be XML.
            // TODO: Change the way this is done.
            String defaultContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            defaultContent += "<ads:test xmlns:ads='http://www.alfresco.org/wsf/2.0/ads' xmlns:alf='http://www.alfresco.org' xmlns:chiba='http://chiba.sourceforge.net/xforms' xmlns:xs='http://www.w3.org/2001/XMLSchema'>";
            defaultContent += "</ads:test>";

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                    defaultContent.getBytes());
            OutputStream os = this.avmRemote.createFile(avmDirectoryPath,
                    fileName);
            FileCopyUtils.copy(bais, os);
            DataUtil.copyStream(bais, os);
        }
        catch (Exception ex)
        {
            System.out.println("ERROR DURING CREATE FILE!");
            ex.printStackTrace();
        }
        return this.getFile(directoryPath, fileName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#deleteFile(java.lang.String)
     */
    public boolean deleteFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return deleteFile(parentPath, fileName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.IFileSystem#deleteFile(java.lang.String, java.lang.String)
     */
    public boolean deleteFile(String directoryPath, String fileName)
    {
        String avmDirectoryPath = getAVMPath(directoryPath);
        this.avmRemote.removeNode(avmDirectoryPath, fileName);
        return true;
    }

    /**
     * Gets the descriptor.
     * 
     * @param avmPath the avm path
     * 
     * @return the descriptor
     */
    protected AVMNodeDescriptor getDescriptor(String avmPath)
    {
        try
        {
            AVMNodeDescriptor descriptor = this.avmRemote.lookup(-1, avmPath);
            return descriptor;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the descriptors.
     * 
     * @param avmPath the avm path
     * 
     * @return the descriptors
     */
    protected AVMNodeDescriptor[] getDescriptors(String avmPath)
    {
        SortedMap<String, AVMNodeDescriptor> map = this.avmRemote.getDirectoryListing(
                -1, avmPath);
        AVMNodeDescriptor[] descriptors = new AVMNodeDescriptor[map.size()];

        int i = 0;
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            AVMNodeDescriptor value = (AVMNodeDescriptor) map.get(key);
            descriptors[i] = value;
            i++;
        }
        return descriptors;
    }

    /** The avm remote. */
    protected AVMRemote avmRemote;
    
    /** The avm store id. */
    protected String avmStoreId;
    
    /** The avm webapp path. */
    protected String avmWebappPath;
    
    /** The servlet context. */
    protected ServletContext servletContext;

    /*
     * public String getAVMPath(String relativePath) { return this.avmWebappPath +
     * "/" + relativePath; }
     */
    // this returns
    // /www/avm_webapps/ROOT/data/site/site-configuration.xml
    /*
     * public String getAVMPath(String relativePath) { try { String realPath =
     * this.servletContext.getRealPath(relativePath); String mountPoint =
     * AVMFileDirContext.getAVMFileDirMountPoint(); JNDIPath jndiPath = new
     * JNDIPath(mountPoint, realPath); String avmPath = jndiPath.getAvmPath();
     * System.out.println("AVM PATH IS: " + avmPath); //System.out.println("AVM
     * PATH IS: " + avmPath); //ads--admin:/www/avm_webapps/ROOT/data/nodes
     * //System.out.println("MOUNT POINT IS: " + mountPoint); //v:\
     * //System.out.println("REAL PATH IS: " + realPath);
     * //v:\ads--admin\VERSION\v-1\DATA\www\avm_webapps\ROOT\data\nodes return
     * avmPath; } catch(Exception ex) { ex.printStackTrace(); } return null; } //
     * this returns: //
     * ads--admin:/www/avm_webapps/ROOT/data/site/site-configuration.xml
     */

    /**
     * Gets the aVM path.
     * 
     * @param relativePath the relative path
     * 
     * @return the aVM path
     */
    public String getAVMPath(String relativePath)
    {
        return this.avmStoreId + ":" + this.avmWebappPath + "/" + relativePath;
    }

    /**
     * Gets the aVM remote.
     * 
     * @return the aVM remote
     */
    public AVMRemote getAVMRemote()
    {
        return this.avmRemote;
    }

    /**
     * Gets the aVM store id.
     * 
     * @return the aVM store id
     */
    public String getAVMStoreId()
    {
        return this.avmStoreId;
    }

    /**
     * Gets the aVM webapp path.
     * 
     * @return the aVM webapp path
     */
    public String getAVMWebappPath()
    {
        return this.avmWebappPath;
    }

    /*
     * else {
     *  }
     * 
     * String avmPath = context.getAvmPath(relativePath);
     * 
     * String relativeFilePath = relativePath + "/" + name; String avmFilePath =
     * context.getAvmPath(relativeFilePath);
     * 
     * boolean success = false;
     *  // test to see if there is already a file there AVMNodeDescriptor
     * nodeDescriptor = null; try { nodeDescriptor = getDescriptor(context,
     * relativeFilePath); } catch(Exception ex) { }
     * 
     * if(nodeDescriptor == null) { try { java.io.ByteArrayInputStream bais =
     * new java.io.ByteArrayInputStream(xml.getBytes());
     * FileCopyUtils.copy(bais, context.getAVMRemote().createFile(avmPath,
     * name)); bais.close(); success = true; } catch(Exception ex) {
     * ex.printStackTrace(); } } else { try { OutputStream os =
     * context.getAVMRemote().getFileOutputStream(avmFilePath);
     * java.io.ByteArrayInputStream bais = new
     * java.io.ByteArrayInputStream(xml.getBytes()); FileCopyUtils.copy(bais,
     * os); success = true; } catch(Exception ex) { ex.printStackTrace(); } }
     * 
     * 
     * 
     * 
     * AVMNodeDescriptor config = getDescriptor(context, path);
     * FormDataFunctions f = new FormDataFunctions(context.getAVMRemote()); doc =
     * f.parseXMLDocument(config.getPath());
     * 
     * 
     * 
     * AVMNodeDescriptor config = getDescriptor(context, relativePath);
     * InputStream is = context.getAVMRemote().getFileInputStream(config);
     * 
     */

}
