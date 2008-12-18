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
package org.alfresco.module.vti.method;

import java.io.IOException;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.metadata.dic.VtiConstraint;
import org.alfresco.module.vti.metadata.dic.VtiProperty;
import org.alfresco.module.vti.metadata.dic.VtiType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling OpenService Method
 *
 * @author andreyak
 */
public class OpenServiceMethod extends AbstractVtiMethod
{
    
    private static Log logger = LogFactory.getLog(OpenServiceMethod.class);
            
    private static final String METHOD_NAME = "open service";

    public String getName()
    {
        return METHOD_NAME;
    }

    /**
     * Provides meta-information for a Web site to the client application
     */
    protected void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        if (logger.isDebugEnabled())        
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String alfrescoContext = request.getAlfrescoContextName();
        String realContext = request.getContextPath();
        if (!realContext.equalsIgnoreCase(alfrescoContext))
        {            
            response.getOutputStream().close();
        }
        String service_name = request.getParameter("service_name");
        if (logger.isDebugEnabled())        
        {
            logger.debug("Opening service with name: '" + service_name + "'");
        }
        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("service");
        response.addParameter("service_name=" + (service_name.equals("/") ? "" : service_name));
        response.addParameter("meta_info=");
        response.beginList();
        response.writeMetaDictionary(VtiProperty.SERVICE_CASESENSITIVEURLS, VtiType.INT, VtiConstraint.X, "0");
        response.writeMetaDictionary(VtiProperty.SERVICE_LONGFILENAMES, VtiType.INT, VtiConstraint.X, "1");
        response.writeMetaDictionary(VtiProperty.SERVICE_WELCOMENAMES, VtiType.VECTOR, VtiConstraint.X, "index.html");
        response.writeMetaDictionary(VtiProperty.SERVICE_USERNAME, VtiType.STRING, VtiConstraint.X, vtiHandler.getUserName());
        response.writeMetaDictionary(VtiProperty.SERVICE_SERVERTZ, VtiType.STRING, VtiConstraint.X, vtiHandler.getServertimeZone());
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLSYSTEM, VtiType.STRING, VtiConstraint.R, "lw");
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLVERSION, VtiType.STRING, VtiConstraint.R, "V1");
        response.writeMetaDictionary(VtiProperty.SERVICE_DOCLIBWEBVIEWENABLED, VtiType.INT, VtiConstraint.X, "1");
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLCOOKIE, VtiType.STRING, VtiConstraint.X, "fp_internal");        
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLPROJECT, VtiType.STRING, VtiConstraint.X, "&#60;STS-based Locking&#62;");        
        response.endList();
        response.endList();
        response.endVtiAnswer();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());            
        }
    }
}