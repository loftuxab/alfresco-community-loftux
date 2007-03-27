/*
 * Copyright (C) 2005 Alfresco, Inc.
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

package org.alfresco.module.phpIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.TemplateProcessor;

public class PHPTemplateProcessor extends BasePHPProcessor implements TemplateProcessor
{
    private NodeService nodeService;
    private ContentService contentService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void process(String template, Object model, Writer out)
    {
        InputStream is = null;
        if (template.indexOf(StoreRef.URI_FILLER) != -1)
        {
            NodeRef ref = new NodeRef(template);
            if (this.nodeService.exists(ref) == true)
            {
                ContentReader contentReader = this.contentService.getReader(ref, ContentModel.PROP_CONTENT);
                if (contentReader != null)
                {
                    is = contentReader.getContentInputStream();
                }
                else
                {
                    throw new AlfrescoRuntimeException("The script (" + template + ") has not content.");
                }
            }
            else
            {
                throw new AlfrescoRuntimeException("Invalid node reference passed to PHP template processor. (" + template + ")");
            }
        }
        else
        {
            is = getClass().getClassLoader().getResourceAsStream(template);
        }

        try
        {
            this.phpEngine.executeScript(is, out, null);
        }
        finally
        {
            try { is.close(); } catch (IOException e) {e.printStackTrace();};
        }
    }

    public void processString(String template, Object model, Writer out)
    {
        // TODO implement this ...
    }
}
