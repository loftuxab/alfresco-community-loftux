/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.alfresco.module.vti.metadata.dic.VtiConstraint;
import org.alfresco.module.vti.metadata.dic.VtiProperty;
import org.alfresco.module.vti.metadata.dic.VtiType;


/**
 * VtiFpResponse is wrapper for HttpServletResponse. It provides specific methods 
 * which allow to generate response for Frontpage extension protocol. 
 * 
 * @author Michael Shavnev
 *
 */
public class VtiFpResponse extends HttpServletResponseWrapper
{
    private static final String HEADER = "<html><head><title>vermeer RPC packet</title></head>\n<body>\n";
    private static final String FOOTER = "</body>\n</html>\n";
    private static final String LIST_OPEN_TAG_LF = "<ul>\n";
    private static final String LIST_CLOSE_TAG_LF = "</ul>\n";
    private static final String LIST_ITEM_TAG = "<li>";
    private static final String PARAMETER_TAG = "<p>";

    private static final char LF = '\n';

    private int nestedLevel;

    
    /**
     * Constructor
     * 
     * @param response HttpServletResponse 
     */
    public VtiFpResponse(HttpServletResponse response)
    {
        super(response);
        this.nestedLevel = 0;
    }

    /**
     * Begins vermeer packet with header
     *
     */
    public void beginPacket() throws IOException
    {
        getOutputStream().write(HEADER.getBytes());
    }

    /**
     * Ends vermeer packet with footer
     *
     */
    public void endPacket() throws IOException
    {
        if (nestedLevel != 0)
            throw new IllegalStateException("nestedLevel must be 0");

        nestedLevel = 0;

        getOutputStream().write(FOOTER.getBytes());
    }

    /**
     * Begins list in root of packet or in other list
     *
     * @param listName name of list
     */
    public void beginList(String listName) throws IOException
    {
        addParameter(listName + "=");
        beginList();
    }

    /**
     * Begins anonymous list
     *
     */
    public void beginList() throws IOException
    {
        getOutputStream().write(LIST_OPEN_TAG_LF.getBytes());
        nestedLevel++;
    }

    /**
     * Ends current list
     *
     */
    public void endList() throws IOException
    {
        if (nestedLevel == 0)
            throw new IllegalStateException("nestedLevel == 0");

        nestedLevel--;

        getOutputStream().write(LIST_CLOSE_TAG_LF.getBytes());
    }

    /**
     * Adds parameter in root of packet or in list
     *
     * @param value parameter value
     */
    public void addParameter(String value) throws IOException
    {
        if (nestedLevel == 0)
        {
            getOutputStream().write(PARAMETER_TAG.getBytes());
        }
        else
        {
            getOutputStream().write(LIST_ITEM_TAG.getBytes());
        }
        getOutputStream().write(value.getBytes());
        getOutputStream().write(LF);
    }

    /**
     * Add parameter
     * 
     * @param key param key
     * @param value param value
     */
    public void addParameter(String key, String value) throws IOException
    {
        addParameter(key + "=" + value);
    }

    //
    // High level operations for encoding vti answer in vermeer packet
    //

    /**
     * Begins vti response with header and method name and version
     *
     * @param methodName method name
     * @param version version string 
     */
    public void beginVtiAnswer(String methodName, String version) throws IOException
    {
        beginPacket();
        addParameter("method", methodName + ":" + version);
    }

    /**
     * Ends vti answer with vermeer packet footer
     */
    public void endVtiAnswer() throws IOException
    {
        endPacket();
    }

    // ===================================================================================================================

    public void writeMetaDictionary(VtiProperty property, VtiType type, VtiConstraint constraint, String value) throws IOException
    {
        if (value != null && value.trim().length() > 0)
        {
            getOutputStream().write((LIST_ITEM_TAG + property + LF).getBytes());
            getOutputStream().write((LIST_ITEM_TAG + type + constraint + "|" + value + LF).getBytes());
        }
    }
}
