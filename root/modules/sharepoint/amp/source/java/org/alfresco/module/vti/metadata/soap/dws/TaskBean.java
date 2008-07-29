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
 * Represents sharepoint task
 * @author AndreyAk
 *
 */
//TODO right now it's a blank stub
// will be support in future
public class TaskBean implements Serializable
{

    private static final long serialVersionUID = -7162094818151530500L;
    
    private String title;
    private String assignedTo;
    private String status;
    private String priority;
    private String dueDate;
    private String body;
    private String created;
    private String author;
    private String modified;
    private String editor;
    private String owshiddenversion;
    private String id;
    

    /**
     * @param title
     * @param assignedTo
     * @param status
     * @param priority
     * @param dueDate
     * @param body
     * @param created
     * @param author
     * @param modified
     * @param editor
     * @param owshiddenversion
     * @param id
     */
    public TaskBean(String title, String assignedTo, String status, String priority, String dueDate, String body, String created, String author, String modified, String editor,
            String owshiddenversion, String id)
    {
        super();
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.body = body;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
        this.owshiddenversion = owshiddenversion;
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the assignedTo
     */
    public String getAssignedTo()
    {
        return assignedTo;
    }

    /**
     * @param assignedTo the assignedTo to set
     */
    public void setAssignedTo(String assignedTo)
    {
        this.assignedTo = assignedTo;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @return the priority
     */
    public String getPriority()
    {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    /**
     * @return the dueDate
     */
    public String getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }

    /**
     * @return the modified
     */
    public String getModified()
    {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(String modified)
    {
        this.modified = modified;
    }

    /**
     * @return the editor
     */
    public String getEditor()
    {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(String editor)
    {
        this.editor = editor;
    }

    /**
     * @return the owshiddenversion
     */
    public String getOwshiddenversion()
    {
        return owshiddenversion;
    }

    /**
     * @param owshiddenversion the owshiddenversion to set
     */
    public void setOwshiddenversion(String owshiddenversion)
    {
        this.owshiddenversion = owshiddenversion;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * @return the created
     */
    public String getCreated()
    {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(String created)
    {
        this.created = created;
    }

    /**
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        
        attributes.put("ows_Title", title);
        attributes.put("ows_AssignedTo", assignedTo);
        attributes.put("ows_Status", status);
        attributes.put("ows_Priority", priority);
        attributes.put("ows_DueDate", dueDate);
        attributes.put("ows_Body", body);
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
