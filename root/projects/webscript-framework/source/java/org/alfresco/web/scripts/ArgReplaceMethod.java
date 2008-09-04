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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * Custom FreeMarker Template language method.
 * <p>
 * Given a URL string and N named/value pairs, replace each URL argument with 
 * respective name/value pair (if name matches existing URL argument), or add 
 * name/value pair to URL (if name does not match existing URL argument).
 * <p>
 * Usage: argreplace(url.args, "skipCount", cursor.nextPage, ...)
 *
 * Example: argreplace("?a=0&b=2", "a", 1, "c", 3) returns "?a=1&b=2&c=3"
 * 
 * @author davidc
 */
public final class ArgReplaceMethod implements TemplateMethodModelEx
{
    
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        if (args.size() == 0)
        {
            return "";
        }

        String urlArgs = "";
        Object arg0 = args.get(0);
        if (arg0 instanceof TemplateScalarModel)
        {
            urlArgs = ((TemplateScalarModel)arg0).getAsString();
        }

        if (args.size() == 1)
        {
            return urlArgs;
        }
        
        Map<String, String> replacements = new HashMap<String, String>();
        int i = 1;
        while(i < args.size())
        {
            String name = null;
            String val = null;
            Object argname = args.get(i);
            if (argname instanceof TemplateScalarModel)
            {
                name = ((TemplateScalarModel)argname).getAsString();
                i++;
                if (i < args.size())
                {
                    Object argval = args.get(i);
                    if (argval instanceof TemplateScalarModel)
                    {
                        val = ((TemplateScalarModel)argval).getAsString();
                    }
                    else if (argval instanceof TemplateNumberModel)
                    {
                        val = ((TemplateNumberModel)argval).getAsNumber().toString();
                    }
                    else if (argval instanceof TemplateBooleanModel)
                    {
                        val = Boolean.toString(((TemplateBooleanModel)argval).getAsBoolean());
                    }

                    if (val != null)
                    {
                        replacements.put(name, val);
                    }
                }
            }
            i++;
        }

        if (replacements.size() == 0)
        {
            return urlArgs;
        }
        
        StringBuilder newUrlArgs = new StringBuilder();
        if (urlArgs.length() > 0)
        {
            String[] argPairs = urlArgs.split("&");
            int n = 0;
            for (String argPair : argPairs)
            {
                String[] nameVal = argPair.split("=");
                String name = nameVal[0];
                String val = (nameVal.length > 1) ? nameVal[1] : null;
                String replaceVal = replacements.get(name);
                if (replaceVal != null)
                {
                    val = replaceVal; 
                }
                newUrlArgs.append(name);
                if (val != null)
                {
                    newUrlArgs.append("=");
                    newUrlArgs.append(val);
                    replacements.remove(name);
                }
                n++;
                if (n < argPairs.length || replacements.size() > 0)
                {
                    newUrlArgs.append("&");
                }
            }
        }

        int rs = replacements.entrySet().size();
        int r = 0;
        for (Map.Entry<String, String> replacement : replacements.entrySet())
        {
            newUrlArgs.append(replacement.getKey());
            if (replacement.getValue() != null)
            {
                newUrlArgs.append("=");
                newUrlArgs.append(replacement.getValue());
                r++;
                if (r < rs)
                {
                    newUrlArgs.append("&");
                }
            }
        }
        
        return newUrlArgs;    
    }
}
