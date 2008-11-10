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

import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.RequestContext;

/**
 * @author muzquiano
 */
public class DeclarativeWebStudioWizardWebScript extends DeclarativeWebStudioWebScript
{
    private static final String ROOT_SCOPE_WIZARD = "wizard";

    /**
     * Instantiates a new declarative web studio wizard web script.
     */
    public DeclarativeWebStudioWizardWebScript()
    {
    	super();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebStudioWebScript#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse, java.util.Map)
     */
    protected Map<String, Object> createScriptParameters(WebScriptRequest req,
            WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = super.createScriptParameters(req, res,
                customParams);
        RequestContext context = getRequestContext(req);
        if (context != null)
        {
            ScriptWizard scriptWizard = new ScriptWizard(context);
            scriptWizard.setModel(params);
            params.put(ROOT_SCOPE_WIZARD, scriptWizard);
        }
        return params;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractWebScript#executeScript(org.alfresco.web.scripts.ScriptContent, java.util.Map)
     */
    @Override
    protected void executeScript(ScriptContent location,
            Map<String, Object> model)
    {
        // get the wizard object
        ScriptWizard wizard = (ScriptWizard) model.get(ROOT_SCOPE_WIZARD);

        // call the wizard's init method
        try
        {
            wizard.init();
        }
        catch (Throwable err)
        {
        	err.printStackTrace();
            throw new AlfrescoRuntimeException("Error during wizard initialisation: " + err.getMessage(), err);
        }

        // get the new script that we should execute
        String currentPageId = wizard.getCurrentPageId();
        boolean isStart = wizard.isCurrentPageStart();
        // boolean isEnd = wizard.isCurrentPageEnd();
        if (currentPageId != null && !isStart)
        {
            String newScriptPath = this.getDescription().getId() + "-" + currentPageId + ".js";
            ScriptContent theScript = getContainer().getScriptProcessor().findScript(
                    newScriptPath);
            super.executeScript(theScript, model);
        }
        else
        {
            // run the script as per usual
            super.executeScript(location, model);
        }

        // automatically call the wizard's finalize method
        wizard.finalize();
    }
}
