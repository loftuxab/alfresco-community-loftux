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
 package org.alfresco.wcm.client.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;

public abstract class HeaderHelper 
{
    private ThreadLocal<SimpleDateFormat> httpDateFormat = new ThreadLocal<SimpleDateFormat>() 
    {
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        }
    };  	     

    /**
     * This base implementation simply returns true to indicate that the request should be re-rendered.
     * Override in a subclass as necessary
     * @param asset
     * @param request
     * @param response
     * @return boolean true if browser has old copy and so content should be rendered
     * @throws IOException 
     * @throws ParseException 
     */
	public boolean setHeaders(Asset asset, HttpServletRequest request, HttpServletResponse response) 
	{
	    return true;
	}
	
    public final String getHttpDate(Date date)
    {
        return dateFormatter().format(date);
    }

    public final Date getDateFromHttpDate(String date) throws ParseException
    {
        return dateFormatter().parse(date);
    }
    
    /**
     * Get a date formatter for the thread as SimpleDateFormat is not thread-safe
     * @return
     */
    public final SimpleDateFormat dateFormatter() 
    {
    	return httpDateFormat.get();
    }	
}
