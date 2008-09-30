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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.util.List;

import org.mozilla.javascript.Scriptable;

/**
 * Helper to resolve an I18N message for JS scripts.
 * 
 * @author Kevin Roast
 */
public final class ScriptMessage extends AbstractMessageHelper
{
    /**
     * Constructor
     * 
     * @param webscript
     */
    public ScriptMessage(WebScript webscript)
    {
        super(webscript);
    }
    
    
    /**
     * Get an I18N message
     * 
     * @param id    Message Id
     * 
     * @return resolved message
     */
    public String get(String id)
    {
        String result = null;
        
        if (id != null && id.length() != 0)
        {
            result = resolveMessage(id);
        }
        
        return (result != null ? result : "");
    }
    
    /**
     * Get an I18N message with the given message args
     * 
     * @param id    Message Id
     * @param args  Message args
     * 
     * @return resolved message
     */
    public String get(String id, Scriptable args)
    {
        String result = null;
        
        if (id != null && id.length() != 0)
        {
            Object params = new ScriptValueConverter().unwrapValue(args);
            if (params instanceof List)
            {
                result = resolveMessage(id, ((List)params).toArray());
            }
            else
            {
                result = resolveMessage(id);
            }
        }
        
        return (result != null ? result : "");
    }
}
