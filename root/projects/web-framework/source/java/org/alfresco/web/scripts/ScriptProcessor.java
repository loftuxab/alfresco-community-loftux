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

import java.util.Map;


/**
 * Web Script Processor
 * 
 * @author davidc
 */
public interface ScriptProcessor
{

    /**
     * Find a script at the specified path (within registered Web Script stores)
     * 
     * @param path   script path
     * @return  script location (or null, if not found)
     */
    public ScriptContent findScript(String path);

    /**
     * Execute script
     * 
     * @param path  script path
     * @param model  model
     * @return  script result
     * @throws ScriptException
     */
    public Object executeScript(String path, Map<String, Object> model);

    /**
     * Execute script
     *  
     * @param location  script location
     * @param model  model
     * @return  script result
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model);

    /**
     * Unwrap value returned by script
     * 
     * TODO: Remove this method when value conversion is truly hidden within script engine
     * 
     * @param value  value to unwrap
     * @return  unwrapped value
     */
    public Object unwrapValue(Object value);
    
    /**
     * Reset script cache
     */
    public void reset();

}
