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
 * Represents  document in sharepoint workspace
 * @author AndreyAk
 *
 */
public class DocumentBean implements Serializable
{

    private static final long serialVersionUID = 7409836094969517436L;
    
    private String id;
    private String progID;
    private String fileRef;
    private String objType;
    private String created;
    private String author;
    private String modified;
    private String editor;
    
    
    /**
     * @param id
     * @param progID
     * @param fileRef
     * @param objType
     * @param created
     * @param author
     * @param modified
     * @param editor
     */
    public DocumentBean(String id, String progID, String fileRef, String objType, String created, String author, String modified, String editor)
    {
        super();
        this.id = id;
        this.progID = progID;
        this.fileRef = fileRef;
        this.objType = objType;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
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
     * @return the progID
     */
    public String getProgID()
    {
        return progID;
    }

    /**
     * @param progID the progID to set
     */
    public void setProgID(String progID)
    {
        this.progID = progID;
    }

    /**
     * @return the fileRef
     */
    public String getFileRef()
    {
        return fileRef;
    }

    /**
     * @param fileRef the fileRef to set
     */
    public void setFileRef(String fileRef)
    {
        this.fileRef = fileRef;
    }

    /**
     * @return the fSObjType
     */
    public String getObjType()
    {
        return objType;
    }

    /**
     * @param objType the fSObjType to set
     */
    public void setObjType(String objType)
    {
        this.objType = objType;
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
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        
        attributes.put("ows_FileRef", fileRef);
        attributes.put("ows_FSObjType", objType);
        attributes.put("ows_Created", created);
        attributes.put("ows_Author", author);
        attributes.put("ows_Modified", modified);
        attributes.put("ows_Editor", editor);
        attributes.put("ows_ID", id);
        attributes.put("ows_ProgID", progID);
        attributes.put("xmlns:z", "#RowsetSchema");
        result.append(SoapUtils.singleTag("z:row", attributes));
        return result.toString();
    }
}
