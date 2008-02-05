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

import javax.servlet.http.HttpServletResponse;

import org.alfresco.i18n.I18NUtil;


/**
 * Web Script Status
 *
 * Records the outcome of a Web Script.
 * 
 * @author davidc
 */
public class Status
{
    private Throwable exception = null;
    private int code = HttpServletResponse.SC_OK;
    private String message = "";
    private boolean redirect = false;

   
    /**
     * @param exception
     */
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    /**
     * @return  exception
     */
    public Throwable getException()
    {
        return exception;
    }
    
    /**
     * @param message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return  message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param redirect  redirect to status code response
     */
    public void setRedirect(boolean redirect)
    {
        this.redirect = redirect;
    }

    /**
     * @return redirect to status code response
     */
    public boolean getRedirect()
    {
        return redirect;
    }

    /**
     * @see javax.servlet.http.HTTPServletResponse
     * 
     * @param code  status code
     */
    public void setCode(int code)
    {
        this.code = code;
    }

    /**
     * @return  status code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Gets the short name of the status code
     * 
     * @return  status code name
     */
    public String getCodeName()
    {
        return I18NUtil.getMessage("webscript.code." + code + ".name");
    }
    
    /**
     * Gets the description of the status code
     * 
     * @return  status code description
     */
    public String getCodeDescription()
    {
        return I18NUtil.getMessage("webscript.code." + code + ".description");
    }
}
