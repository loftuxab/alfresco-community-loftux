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
package org.alfresco.web.scripts.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.Content;
import org.alfresco.util.InputStreamContent;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


/**
 * Form Data
 * 
 * @author davidc
 */
public class FormData implements Serializable
{
    private static final long serialVersionUID = 1832644544828452385L;

    private Scriptable scope;
    private HttpServletRequest req;
    private ServletFileUpload upload;
    private Map<String, FormField> fields = null;
    private Map<String, String> parameters = null;
    private Map<String, Content> files = null;
    private String encoding = null;
   
    /**
     * Construct
     * 
     * @param req
     */
    public FormData(HttpServletRequest req)
    {
        this.req = req;
    }

    /**
     * Determine if multi-part form data has been provided
     * 
     * @return  true => multi-part
     */
    public boolean getIsMultiPart()
    {
        return upload.isMultipartContent(req);
    }

    /**
     * Determine if form data has specified field
     * 
     * @param name  field to look for
     * @return  true => form data contains field
     */
    public boolean hasField(String name)
    {
        Map<String, FormField> fields = getFieldsMap();
        return fields.containsKey(name);
    }

    /**
     * Gets the Form fields
     * 
     * @return  array of FormField
     */
    public Scriptable getFields()
    {
        Map<String, FormField> fieldsMap = getFieldsMap();
        Object[] fields = new Object[fieldsMap.values().size()];
        fieldsMap.values().toArray(fields);
        return Context.getCurrentContext().newArray(this.scope, fields);        
    }

    /**
     * Gets parameters encoded in the form data
     * 
     * @return  map (name, value) of parameters
     */
    /*package*/ Map<String, String> getParameters()
    {
        return getParametersMap();
    }
    
    /**
     * Gets files encoded in form data
     * 
     * @return  map (name, ScriptContent) of files
     */
    /*package*/ Map<String, Content> getFiles()
    {
        return getFilesMap();
    }
    
    /**
     * Helper to parse servlet request form data
     * 
     * @return  map of all form fields
     */
    private Map<String, FormField> getFieldsMap()
    {
        // NOTE: This class is not thread safe - it is expected to be constructed on each thread.
        if (fields == null)
        {
            FileItemFactory factory = new DiskFileItemFactory();
            upload = new ServletFileUpload(factory);
            encoding = req.getCharacterEncoding();
            upload.setHeaderEncoding(encoding);
            try
            {
                List<FileItem> fileItems = upload.parseRequest(req);
                fields = new HashMap<String, FormField>();
                for (FileItem fileItem : fileItems)
                {
                    FormField formField = new FormField(fileItem);
                    fields.put(fileItem.getFieldName(), formField);
                }
            }
            catch(FileUploadException e)
            {
                // NOTE: assume no files can be located
                fields = Collections.emptyMap();
            }
        }
        return fields;
    }
 
    /**
     * Helper to parse servlet request form data
     * 
     * @return  map of all form parameters
     */
    private Map<String, String> getParametersMap()
    {
        if (parameters == null)
        {
            Map<String, FormField> fields = getFieldsMap();
            parameters = new HashMap<String, String>();
            for (Map.Entry<String, FormField> entry : fields.entrySet())
            {
                FormField field = entry.getValue();
                if (!field.getIsFile())
                {
                    parameters.put(entry.getKey(), field.getValue());
                }
            }
        }
        return parameters;
    }
    
    /**
     * Helper to parse servlet request form data
     * 
     * @return  map of all form files
     */
    private Map<String, Content> getFilesMap()
    {
        if (files == null)
        {
            Map<String, FormField> fields = getFieldsMap();
            files = new HashMap<String, Content>();
            for (Map.Entry<String, FormField> entry : fields.entrySet())
            {
                FormField field = entry.getValue();
                if (field.getIsFile())
                {
                    files.put(entry.getKey(), field.getContent());
                }
            }           
        }
        return files;
    }
    

    /**
     * Form Field
     * 
     * @author davidc
     */
    public class FormField implements Serializable
    {
        private static final long serialVersionUID = -6061565518843862346L;
        private FileItem file;

        /**
         * Construct
         * 
         * @param file
         */
        public FormField(FileItem file)
        {
            this.file = file;
        }
        
        /**
         * @return  field name
         */
        public String getName()
        {
            return file.getFieldName();
        }
        
        /**
         * @return  true => field represents a file
         */
        public boolean getIsFile()
        {
            return !file.isFormField();
        }
        
        /**
         * @return  field value (for file, attempts conversion to string)
         */
        public String getValue()
        {
            try
            {
                return (file.isFormField() && encoding != null) ? file.getString(encoding) : file.getString();
            }
            catch (UnsupportedEncodingException e)
            {
                throw new AlfrescoRuntimeException("Unable to decode form field", e);
            }
        }
        
        /**
         * @return  field as content
         */
        public Content getContent()
        {
            try
            {
                return new InputStreamContent(file.getInputStream(), getMimetype(), null);
            }
            catch(IOException e)
            {
                return null;
            }
        }

        /**
         * @return  mimetype
         */
        public String getMimetype()
        {
            return file.getContentType();
        }

        /**
         * @return  filename (only for file fields, otherwise null)
         */
        public String getFilename()
        {
            // workaround a bug in IE where the full path is returned
            return FilenameUtils.getName(file.getName());
        }
    }
    
}
