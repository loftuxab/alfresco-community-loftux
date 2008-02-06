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
package org.alfresco.web.scripts.atom;

import java.io.IOException;
import java.util.List;

import org.apache.abdera.model.Base;
import org.apache.abdera.writer.Writer;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;


/**
 * Custom FreeMarker Template language method.
 * <p>
 * Render atom (feed or entry) to xml.
 * <p>
 * Usage: writeAtom(Feed [,format])
 *        writeAtom(Entry [,format])
 * 
 * Where:
 *        format => prettyxml (write pretty atom xml)
 *                  json (write json)
 * 
 * @author davidc
 */
public final class AtomWriterMethod implements TemplateMethodModelEx
{
    private AbderaService abdera;

    /**
     * Construct
     * 
     * @param abdera
     */
    public AtomWriterMethod(AbderaService abdera)
    {
        this.abdera = abdera;
    }

    /* (non-Javadoc)
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        Object result = "";
        
        if (args.size() != 0)
        {
            // retrieve atom element to write
            Object bean = null;
            Object arg0 = args.get(0);
            if (arg0 instanceof BeanModel)
            {
                bean = ((BeanModel)arg0).getWrappedObject();
            }
            
            if (bean != null && bean instanceof Base)
            {
                // retrieve appropriate writer
                String format = null;
                if (args.size() > 1)
                {
                    Object arg1 = args.get(1);
                    if (arg1 instanceof TemplateScalarModel)
                    {
                        format = ((TemplateScalarModel)arg1).getAsString();
                    }
                }
                Writer writer = abdera.getWriter(format == null ? AbderaService.DEFAULT_WRITER : format);
                if (writer == null)
                {
                    throw new TemplateModelException("Atom writer '" + format + "' does not exist");
                }
                
                try
                {
                    // now write
                    result = writer.write((Base)bean);
                }
                catch (IOException e)
                {
                    throw new TemplateModelException("Failed to serialize " + bean, e);
                }
            }
        }
        
        return result;
    }
    
}
