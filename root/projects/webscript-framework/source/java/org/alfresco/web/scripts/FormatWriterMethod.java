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
package org.alfresco.web.scripts;

import java.util.List;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;


/**
 * Custom FreeMarker Template language method.
 * <p>
 * Render object to mimetype of web script template.  If object cannot be serialized to
 * mimetype then no output is written.
 * <p>
 * Usage: formatwrite(object)
 * 
 * Where:
 *        object => object to write
 * 
 * @author davidc
 */
public final class FormatWriterMethod implements TemplateMethodModelEx
{
    private FormatRegistry formatRegistry;
    private String mimetype;

    /**
     * Construct
     * 
     * @param abdera
     */
    public FormatWriterMethod(FormatRegistry formatRegistry, String format)
    {
        this.formatRegistry = formatRegistry;
        this.mimetype = formatRegistry.getMimeType(null, format);
    }

    /* (non-Javadoc)
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        String result = "";
        if (args.size() != 0)
        {
            // retrieve object to serialize
            Object object = null;
            Object arg0 = args.get(0);
            if (arg0 instanceof BeanModel)
            {
                object = ((BeanModel)arg0).getWrappedObject();
            }
            
            if (object != null)
            {
                FormatWriter<Object> writer = formatRegistry.getWriter(object, mimetype);
                if (writer != null)
                {
                    // NOTE: For now, streaming directly to freemarker writer i.e. not relying on
                    //       result to return serialized form
                    writer.write(object, Environment.getCurrentEnvironment().getOut());
                }
            }
        }
        return result;
    }
    
}
