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
 * Represents a URI to Web Script match
 *  
 * @author davidc
 */
public class Match
{
    private String templatePath;
    private Map<String, String> templateVars;
    private String matchPath;
    private WebScript script;
    private Kind kind;

    /**
     * Kind of Match
     */
    public enum Kind
    {
        /** URL request matches on URI only */
        URI,
        /** URL request matches on URI and Method */
        FULL
    };

    /**
     * Construct
     * 
     * @param templateVars
     * @param script
     */
    public Match(String templatePath, Map<String, String> templateVars, String matchPath, WebScript script)
    {
        this.kind = Kind.FULL;
        this.templatePath = templatePath;
        this.templateVars = templateVars;
        this.matchPath = matchPath;
        this.script = script;
    }
    
    /**
     * Construct
     * 
     * @param templatePath
     */
    public Match(String templatePath, Map<String, String> templateVars, String matchPath)
    {
        this.kind = Kind.URI;
        this.templatePath = templatePath;
        this.templateVars = templateVars;
        this.matchPath = matchPath;
    }

    /**
     * Gets the kind of Match
     */
    public Kind getKind()
    {
        return this.kind;
    }
    
    /**
     * Gets the template request URL that matched the Web Script URL Template
     * 
     * @return  matching url template
     */
    public String getTemplate()
    {
        return templatePath;
    }

    /**
     * Gets the template variable substitutions
     * 
     * @return  template variable values (value indexed by name)
     */
    public Map<String, String> getTemplateVars()
    {
        return templateVars;
    }
    
    /**
     * Gets the static (i.e. without tokens) part of the request URL that matched
     * the Web Script URL Template
     * 
     * @return  matching static url path
     */
    public String getPath()
    {
        return matchPath;
    }
    
    /**
     * Gets the matching web script
     * 
     * @return  service (or null, if match kind is URI)
     */
    public WebScript getWebScript()
    {
        return script;
    }

    @Override
    public String toString()
    {
        return templatePath;
    }
    
}
