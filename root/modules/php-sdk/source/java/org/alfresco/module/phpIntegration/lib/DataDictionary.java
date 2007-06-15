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
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;

/**
 * The PHP dictionary service API.
 * 
 * @author Roy Wetherall
 */
public class DataDictionary implements ScriptObject
{
    /** Script extension name */
    private static final String SCRIPT_OBJECT_NAME = "DataDictionary";
    
    /** The session */
    private Session session;
    
    /** The dictionary service */
    private DictionaryService dictionaryService;
    
    /**
     * Constructor
     * 
     * @param session   the session
     */
    public DataDictionary(Session session)
    {
        this.session = session;
        this.dictionaryService = this.session.getServiceRegistry().getDictionaryService();
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }

    /**
     * Determines whether one class is a sub type of an other.  Returns true if it is, false otherwise.
     * 
     * @param clazz         the class to test
     * @param subTypeOf     test whether the class is a sub-type of this class
     * @return boolean      true if it is a sub-class, false otherwise
     */
    public boolean isSubTypeOf(String clazz, String subTypeOf)
    {
        // Convert to full names if required
        clazz = this.session.getNamespaceMap().getFullName(clazz);
        subTypeOf = this.session.getNamespaceMap().getFullName(subTypeOf);
        
        // Create the QNames for the passes classes
        QName className = QName.createQName(clazz);
        QName ofClassName = QName.createQName(subTypeOf);
        
        // Return the result
        return this.dictionaryService.isSubClass(className, ofClassName);
    }
    
}
