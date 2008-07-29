/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.module.vti.metadata.soap.versions;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Class that represent version of document
 *
 * @author PavelYur
 */
public class DocumentVersionBean implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 8732427482027589331L;

    private String version;
    private String url;
    private String createdTime;
    private String createdBy;
    private long size;
    private String comments;

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime(String createdTime)
    {
        this.createdTime = createdTime;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

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

    public DocumentVersionBean()
    {

    }

    public String toString()
    {
        return MessageFormat.format("[version = {0}, url = ''{1}'', createdTime = {2}, createdBy = {3}, size = {4}, comments = ''{5}'']",
                version, url, createdTime, createdBy, size, comments);
    }

}
