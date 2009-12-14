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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.vti.web.scripts;

import java.io.IOException;
import java.io.Writer;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.alfresco.web.scripts.json.JSONWriter;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;


/**
 * WebScript responsible for returning interesting Vti Server runtime parameters.
 * 
 * @author Mike Hatfield
 */
public class VtiServerWebScript extends AbstractWebScript
{
	private int vtiServerPort = 0;
	
    public void setPort(int vtiServerPort)
    {
        this.vtiServerPort = vtiServerPort;
    }
	
    /**
     * Execute the webscript and return the cached JavaScript response
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        Writer writer = new StringBuilderWriter(8192);
        JSONWriter out = new JSONWriter(writer);
        try
        {
            out.startObject();
            out.writeValue("port", this.vtiServerPort);
            out.endObject();
        }
        catch (IOException jsonErr)
        {
            throw new WebScriptException("Error building response.", jsonErr);
        }

        res.getWriter().write(writer.toString());
        res.getWriter().flush();
        res.getWriter().close();
    }
}
