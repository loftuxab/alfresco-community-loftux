/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * <p>Class that represent version of document</p>
 *
 * @author PavelYur
 */
public class DocumentVersionBean implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 8732427482027589331L;

    // version id
    private String id;

    // version number
    private String version;
    
    // url of the version
    private String url;
    
    // time when version was created
    private String createdTime;
    
    // name of the user that creates version 
    private String createdBy;
    
    // size in bytes
    private long size;
    
    // comments
    private String comments;

    /**
     * 
     * @return the comments
     */
    public String getComments()
    {
        return comments;
    }

    /**
     * 
     * @param comments the comments to set
     */
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    /**
     * 
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * <p>Sets the version number</p>
     * 
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * 
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * 
     * @return the createdTime
     */
    public String getCreatedTime()
    {
        return createdTime;
    }

    /**
     * 
     * @param createdTime the createdTime to set 
     */
    public void setCreatedTime(String createdTime)
    {
        this.createdTime = createdTime;
    }

    /**
     * 
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * 
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * 
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    /**
     * 
     * @param size the size to set
     */
    public void setSize(long size)
    {
        this.size = size;
    }
    
    /**
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * 
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * <p>Constructor</p>
     * 
     * @param version
     * @param url
     * @param createdTime
     * @param createdBy
     * @param size
     * @param comments
     */
    public DocumentVersionBean(String version, String url, String createdTime, String createdBy, int size, String comments)
    {
        super();
        this.version = version;
        this.url = url;
        this.createdTime = createdTime;
        this.createdBy = createdBy;
        this.size = size;
        this.comments = comments;
    }

    /**
     * default costructor
     */
    public DocumentVersionBean()
    {
    }

    public String toString()
    {
        return MessageFormat.format("[version = {0}, url = ''{1}'', createdTime = {2}, createdBy = {3}, size = {4}, comments = ''{5}'']",
                version, url, createdTime, createdBy, size, comments);
    }

}
