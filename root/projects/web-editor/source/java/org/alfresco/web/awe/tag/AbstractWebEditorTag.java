/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.web.awe.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * Base class for all Web Editor tag implementations.
 * 
 * @author gavinc
 */
public class AbstractWebEditorTag extends TagSupport
{
    private static final long serialVersionUID = 3251970922970982753L;
    
    private static final String PARAM_CONTEXT_PATH = "org.alfresco.web.awe.CONTEXT_PATH";
    private static final String PARAM_DEBUG = "org.alfresco.web.awe.DEBUG";
    
    private static final String DEFAULT_CONTEXT_PATH = "/awe";
    
    private String aweContextPath = null;
    private Boolean debugEnabled = null;
    
    protected static final String KEY_TOOLBAR_LOCATION = "awe_toolbar_location";
    protected static final String KEY_MARKER_ID_PREFIX = "awe_marker_id_prefix";
    protected static final String KEY_EDITABLE_CONTENT = "awe_editable_content";

    /**
     * Returns the context path for the web editor application.
     * <p>
     * This is the value of the <code>org.alfresco.web.awe.CONTEXT_PATH</code> init
     * parameter in web.xml. If the init parameter is not present the
     * default context path of <code>/awe</code> will be returned.
     * </p>
     * 
     * @return The AWE context path
     */
    protected String getWebEditorContextPath()
    {
        if (this.aweContextPath == null)
        {
            String contextPath = this.pageContext.getServletContext().getInitParameter(PARAM_CONTEXT_PATH);
            if (contextPath != null)
            {
                // ensure there is a leading slash
                if (contextPath.startsWith("/") == false)
                {
                    contextPath = "/" + contextPath;
                }
                
                this.aweContextPath = contextPath;
            }
            else
            {
                this.aweContextPath = DEFAULT_CONTEXT_PATH;
            }
        }
        
        return this.aweContextPath;
    }
    
    /**
     * Determines whether debug is enabled for the web editor application
     * <p>
     * This method returns true if the <code>org.alfresco.web.awe.DEBUG</code> init
     * parameter in web.xml is set to <code>true</code>.
     * </p>
     * 
     * @return true if debug is enabled
     */
    protected boolean isDebugEnabled()
    {
        if (this.debugEnabled == null)
        {
            String debug = this.pageContext.getServletContext().getInitParameter(PARAM_DEBUG);
            if (debug != null && debug.equalsIgnoreCase("true"))
            {
                this.debugEnabled = Boolean.TRUE;
            }
            else
            {
                this.debugEnabled = Boolean.FALSE;
            }
        }
        
        return this.debugEnabled;
    }
    
    /**
     * Returns the list of marked content that has been discovered.
     * <p>
     * This list is built up as each markContent tag is encountered.
     * </p>
     * 
     * @return List of MarkedContent objects
     */
    @SuppressWarnings("unchecked")
    protected List<MarkedContent> getMarkedContent()
    {
        List<MarkedContent> markedContent = (List<MarkedContent>)this.pageContext.getRequest().getAttribute(
                    KEY_EDITABLE_CONTENT);
        
        if (markedContent == null)
        {
            markedContent = new ArrayList<MarkedContent>();
            this.pageContext.getRequest().setAttribute(KEY_EDITABLE_CONTENT, markedContent);
        }
        
        return markedContent;
    }

    @Override
    public void release()
    {
        super.release();
        
        this.aweContextPath = null;
        this.debugEnabled = null;
    }
}
