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
package org.alfresco.module.vti.metadata.soap.dws;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.module.vti.metadata.soap.SoapUtils;

/**
 * Represents sharepoint link
 * @author AndreyAk
 *
 */
//TODO right now it's a blank stub; will be support in future
public class LinkBean implements Serializable
{

    private static final long serialVersionUID = -7781309737681728753L;
    
    private String url;
    private String comments;
    private String created;
    private String author;
    private String modified;
    private String editor;
    private int owshiddenversion;
    private int id;
    
    public LinkBean(String url, String comments, String created, String author, String modified, String editor, int owshiddenversion, int id)
    {
        super();
        this.url = url;
        this.comments = comments;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
        this.owshiddenversion = owshiddenversion;
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated(String created)
    {
        this.created = created;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getModified()
    {
        return modified;
    }

    public void setModified(String modified)
    {
        this.modified = modified;
    }

    public String getEditor()
    {
        return editor;
    }

    public void setEditor(String editor)
    {
        this.editor = editor;
    }

    public int getOwshiddenversion()
    {
        return owshiddenversion;
    }

    public void setOwshiddenversion(int owshiddenversion)
    {
        this.owshiddenversion = owshiddenversion;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        
        attributes.put("ows_URL", url + ", " + url);
        attributes.put("ows_Comments", comments);
        attributes.put("ows_Created", created);
        attributes.put("ows_Author", author);
        attributes.put("ows_Modified", modified);
        attributes.put("ows_Editor", editor);
        attributes.put("ows_owshiddenversion", owshiddenversion);
        attributes.put("ows_ID", id);
        attributes.put("xmlns:z", "#RowsetSchema");
        result.append(SoapUtils.singleTag("z:row", attributes));
        return result.toString();        
    }
    
}
