/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.view;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Stream an asset for the view
 * @author Chris Lack
 *
 */
public class StreamedAssetView extends AbstractView
{
	private static final Log log = LogFactory.getLog(StreamedAssetView.class.getName());

	private InputStream stream;

	/**
	 * Construct the view with the image details
	 * @param stream the stream of data which represents the image
	 * @param mimeType the mime type of the image
	 */
	public StreamedAssetView(InputStream stream, String mimeType)
	{
	    this.stream = stream;
	    setContentType(mimeType);
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            							   HttpServletRequest request, HttpServletResponse response) throws Exception
	{
	    ServletOutputStream out = null;
	
	    if (stream == null)
	    {
	        log.debug("Asset contents are not available!");
	        return;
	    }
	
	    try
	    {
	    	// Write the InputStream to the servlet OutputStream
	        out = response.getOutputStream();
	        BufferedOutputStream bufOut = new BufferedOutputStream(out);
	        response.setContentType(getContentType());
	        byte[] buf = new byte[2048];
	        int count;
            while ((count = stream.read(buf)) != -1)
            {
            	bufOut.write(buf, 0, count);
            }
            bufOut.flush();
	    }
	    catch (IOException ex)
	    {
	        log.error("Unable to stream asset data!", ex);
	    }
	    finally
	    {
	        if (out != null) out = null;
	    }
	}
}


