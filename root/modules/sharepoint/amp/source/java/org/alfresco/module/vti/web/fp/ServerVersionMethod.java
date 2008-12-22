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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling ServerVersion Method
 *
 * @author Michael Shavnev
 */
public class ServerVersionMethod extends AbstractMethod
{
    private static final int major = 6;
    private static final int minor = 0;
    private static final int phase = 2;
    private static final int increment = 8117;    

    public static final String version = major + "." + minor + "." + phase + "." + increment;
    
    private static Log logger = LogFactory.getLog(ServerVersionMethod.class);
    
    public String getName()
    {
        return "server version";
    }

    /**
     * Returns the exact version of Microsoft Windows SharePoint Services
     * that are emulated on a Web server
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMehtodException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        response.beginVtiAnswer(getName(), version);
        response.beginList(getName());
        response.addParameter("major ver=" + major);
        response.addParameter("minor ver=" + minor);
        response.addParameter("phase ver=" + phase);
        response.addParameter("ver incr=" + increment);
        response.endList();
        response.addParameter("source control=1");
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

}
