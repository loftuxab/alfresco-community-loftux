/*
 * Copyright (C) 2005 Alfresco, Inc.
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

package org.alfresco.module.phpIntegration;

import java.io.InputStream;
import java.util.Map;

import org.alfresco.module.phpIntegration.module.BaseQuercusModule;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.ScriptException;
import org.alfresco.service.cmr.repository.ScriptImplementation;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.namespace.QName;

import com.caucho.quercus.env.StringInputStream;

/**
 * 
 * @author Roy Wetherall
 *
 */
public class PHPScriptService extends BasePHPProcessor implements ScriptService
{
    
    public Object executeScript(String scriptClasspath, Map<String, Object> model) throws ScriptException
    {
        return executeScript(new ClasspathScriptLocation(scriptClasspath), model);
    }

    public Object executeScript(NodeRef scriptRef, QName contentProp, Map<String, Object> model) throws ScriptException
    {
        return null;
    }

    public Object executeScript(ScriptLocation scriptLocation, Map<String, Object> model) throws ScriptException
    {
        return executeScriptImpl(scriptLocation.getInputStream(), model);
    }

    public Object executeScriptString(String script, Map<String, Object> model) throws ScriptException
    {
        return executeScriptImpl(new StringInputStream(script), model);
    }
    
    private Object executeScriptImpl(InputStream is, Map<String, Object> model)
    {
        // Execute the script and return the result
        return this.phpEngine.executeScript(is, null, model);
    }

    public void registerScript(ScriptImplementation script)
    {
        if (script instanceof BaseQuercusModule)
        {
            BaseQuercusModule module = (BaseQuercusModule)script;
            this.phpEngine.registerModule(module);
        }
    }

}
