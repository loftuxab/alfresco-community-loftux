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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;

/**
 * This helper class handles I/O for the Records Management custom model.
 */
public class CustomModelUtil
{
    public static final String CUSTOM_MODEL_PREFIX = "rmc";
    protected static final String RMA_RECORD = "rma:record";
    protected static final String RMC_CUSTOM_PROPS = CUSTOM_MODEL_PREFIX + ":customProperties";
    protected static final String RMC_CUSTOM_ASSOCS = CUSTOM_MODEL_PREFIX + ":customAssocs";

    private ContentService contentService;

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public M2Model readCustomContentModel()
    {
        ContentReader reader = this.contentService.getReader(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF,
                                                             ContentModel.TYPE_CONTENT);
        
        if (reader.exists() == false) {throw new AlfrescoRuntimeException("RM CustomModel has no content.");}
        
        InputStream contentIn = null;
        M2Model deserializedModel = null;
        try
        {
            contentIn = reader.getContentInputStream();
            deserializedModel = M2Model.createModel(contentIn);
        }
        finally
        {
            try
            {
                if (contentIn != null) contentIn.close();
            }
            catch (IOException ignored)
            {
                // Intentionally empty.`
            }
        }
        return deserializedModel;
    }

    public void writeCustomContentModel(M2Model deserializedModel)
    {
        ContentWriter writer = this.contentService.getWriter(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF,
                                                             ContentModel.TYPE_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_XML);
        writer.setEncoding("UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        deserializedModel.toXML(baos);
        
        final String updatedModelXml = baos.toString();
        
        writer.putContent(updatedModelXml);
        
        // TODO Is it my responsibility to close the writer stream?
    }
}
