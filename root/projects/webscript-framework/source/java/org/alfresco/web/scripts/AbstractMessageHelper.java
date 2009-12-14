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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author Kevin Roast
 * 
 * Base class for returning an I18N message string for a WebScript.
 * <p>
 * Returns an I18N message resolved for the current locale and specified message ID.
 * <p>
 * Firstly the service resource for the parent WebScript will be used for the lookup,
 * followed by the global webscripts.properties resource bundle. 
 */
public class AbstractMessageHelper
{
    private WebScript webscript;
    
    
    /**
     * Constructor
     * 
     * @param webscript     The WebScript to lookup resources against first
     */
    public AbstractMessageHelper(WebScript webscript)
    {
        if (webscript == null)
        {
            throw new IllegalArgumentException("WebScript must be provided to constructor.");
        }
        this.webscript = webscript;
    }
    
    
    /**
     * Get an I18Ned message.
     * 
     * @param id        The message Id
     * @param args      The optional list of message arguments
     * 
     * @return resolved message string or the original ID if unable to find
     */
    protected final String resolveMessage(String id, Object... args)
    {
        String result = null;
        
        // lookup msg resource in webscript specific bundle
        ResourceBundle resources = webscript.getResources();
        if (resources != null)
        {
            try
            {
                result = resources.getString(id);
            }
            catch (MissingResourceException mre)
            {
                // key not present
            }
        }
        
        // if not found, try global bundles
        if (result == null)
        {
            result = I18NUtil.getMessage(id);
        }
        
        if (args.length == 0)
        {
            // for no args, just return found msg or the id on failure
            if (result == null)
            {
            	result = id;
            }
        }
        else
        {
            // for supplied msg args, format msg or return id on failure
            if (result != null)
            {
                result = MessageFormat.format(result, args);
            }
            else
            {
                result = id;
            }
        }
        
        return result;
    }
}
