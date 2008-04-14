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


/**
 * Web Script Status (version 2.x)
 * 
 * NOTE: PROVIDED FOR BACKWARDS COMPATIBILITY ONLY - see org.alfresco.web.scripts.Statust
 * 
 * @author davidc
 * @deprecated
 */
public class WebScriptStatus
{
    private Status status;;
    
    /**
     * Construct
     * 
     * @param status
     */
    public WebScriptStatus(Status status)
    {
        this.status = status;
    }
    
    /**
     * @param exception
     */
    public void setException(Throwable exception)
    {
        status.setException(exception);
    }

    /**
     * @return  exception
     */
    public Throwable getException()
    {
        return status.getException();
    }
    
    /**
     * @param message
     */
    public void setMessage(String message)
    {
        status.setMessage(message);
    }

    /**
     * @return  message
     */
    public String getMessage()
    {
        return status.getMessage();
    }

    /**
     * @param redirect  redirect to status code response
     */
    public void setRedirect(boolean redirect)
    {
        status.setRedirect(redirect);
    }

    /**
     * @return redirect to status code response
     */
    public boolean getRedirect()
    {
        return status.getRedirect();
    }

    /**
     * @see javax.servlet.http.HTTPServletResponse
     * 
     * @param code  status code
     */
    public void setCode(int code)
    {
        status.setCode(code);
    }

    /**
     * @return  status code
     */
    public int getCode()
    {
        return status.getCode();
    }

    /**
     * Gets the short name of the status code
     * 
     * @return  status code name
     */
    public String getCodeName()
    {
        return status.getCodeName();
    }
    
    /**
     * Gets the description of the status code
     * 
     * @return  status code description
     */
    public String getCodeDescription()
    {
        return status.getCodeDescription();
    }
    
}
