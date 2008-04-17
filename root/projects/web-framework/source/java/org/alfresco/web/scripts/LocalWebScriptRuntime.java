/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author muzquiano
 */
public class LocalWebScriptRuntime
	extends AbstractRuntime
{
	private LocalWebScriptContext context;
	private String encoding;
	private ByteArrayOutputStream baOut;

    public LocalWebScriptRuntime(RuntimeContainer container, LocalWebScriptContext context, String encoding) 
	{
		super(container);

		this.context = context;
		this.encoding = encoding;        
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.web.scripts.Runtime#getName()
	 */
	public String getName()
	{
		return "Web Script Runtime";
	}

	@Override
	protected String getScriptUrl()
	{
		return context.RequestURI;
	}

	@Override
	protected WebScriptRequest createRequest(Match match)
	{
		// this includes all elements of the xml
		Map properties = context.modelObject.getProperties();
		String scriptUrl = context.scriptUrl;
        
		return new LocalWebScriptRequest(this, scriptUrl, match, properties);
	}

	@Override
	protected LocalWebScriptResponse createResponse()
	{
		try
		{
			baOut = new ByteArrayOutputStream(4096);
			BufferedWriter wrOut = new BufferedWriter(
					encoding == null ? new OutputStreamWriter(baOut) : new OutputStreamWriter(baOut, encoding));
			
			return new LocalWebScriptResponse(this, context, wrOut, baOut);
		}
		catch (UnsupportedEncodingException err)
		{
			throw new AlfrescoRuntimeException("Unsupported encoding.", err);
		}
	}

	@Override
	protected String getScriptMethod()
	{
		return "GET";
	}

	public Reader getResponseReader()
	{
		try
		{
			if (baOut == null)
			{
				return null;
			}
			else
			{
				return new BufferedReader(new InputStreamReader(
						encoding == null ? new ByteArrayInputStream(baOut.toByteArray()) :
							new ByteArrayInputStream(baOut.toByteArray()), encoding));
			}
		}
		catch (UnsupportedEncodingException err)
		{
			throw new AlfrescoRuntimeException("Unsupported encoding.", err);
		}
	}

	@Override
	protected Authenticator createAuthenticator()
	{
		return null;
	}
}
