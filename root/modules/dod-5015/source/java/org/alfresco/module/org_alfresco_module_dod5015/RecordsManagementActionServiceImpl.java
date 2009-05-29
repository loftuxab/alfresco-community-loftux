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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management Action Service Implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementActionServiceImpl implements RecordsManagementActionService
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementActionServiceImpl.class);

    /** Registered records management actions */
    private Map<String, RecordsManagementAction> rmActions = new HashMap<String, RecordsManagementAction>();
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService#register(org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAction)
     */
    public void register(RecordsManagementAction rmAction)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Registering rmAction " + rmAction);
        }
        this.rmActions.put(rmAction.getName(), rmAction);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService#executeRecordsManagementAction(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    public void executeRecordsManagementAction(NodeRef filePlanComponent, String name)
    {
        executeRecordsManagementAction(filePlanComponent, name, null);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService#executeRecordsManagementAction(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.util.Map)
     */
    public void executeRecordsManagementAction(NodeRef filePlanComponent, String name, Map<String, Serializable> parameters)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Executing record management action on " + filePlanComponent);
            logger.debug("    actionName = " + name);
            logger.debug("    parameters = " + parameters);
        }
        
        RecordsManagementAction rmAction = this.rmActions.get(name);
        if (rmAction == null)
        {
            StringBuilder msg = new StringBuilder();
            msg.append("The record management action '")
                .append(name)
                .append("' has not been defined");
            if (logger.isWarnEnabled())
            {
                logger.warn(msg.toString());
            }
            throw new AlfrescoRuntimeException(msg.toString());
        }
        
        rmAction.execute(filePlanComponent, parameters);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService#getRecordsManagementActions()
     */
    public List<String> getRecordsManagementActions()
    {
        List<String> result = new ArrayList<String>(this.rmActions.size());
        result.addAll(this.rmActions.keySet());
        return Collections.unmodifiableList(result);
    }
}
