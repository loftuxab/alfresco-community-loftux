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
 * 
 */

package org.alfresco.module.phpIntegration;


import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

import org.alfresco.module.phpIntegration.lib.Node;
import org.alfresco.module.phpIntegration.lib.Repository;
import org.alfresco.module.phpIntegration.lib.ScriptObject;
import org.alfresco.module.phpIntegration.lib.Session;
import org.alfresco.module.phpIntegration.lib.SpacesStore;
import org.alfresco.module.phpIntegration.lib.Store;
import org.alfresco.module.phpIntegration.module.BaseQuercusModule;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ScriptException;

import com.caucho.quercus.Quercus;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.program.JavaClassDef;
import com.caucho.util.CharBuffer;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.StringWriter;
import com.caucho.vfs.VfsStream;
import com.caucho.vfs.WriteStream;

public class PHPEngine
{
    public static final String KEY_SERVICE_REGISTRY = "ServiceRegistry";
    
    private Quercus quercus = new Quercus();    
    
    private ServiceRegistry serviceRegistry;    
    
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    public void init()
    {
        // Add the libarary classes 
        // TODO move this config to spring
        registerClass("Repository", Repository.class);
        registerClass("Session", Session.class);
        registerClass("Node", Node.class);
        registerClass("Store", Store.class);
        registerClass("SpacesStore", SpacesStore.class);
        
        // Add the service registry as a special value
        this.quercus.setSpecial(KEY_SERVICE_REGISTRY, this.serviceRegistry);
    }
    
    public void registerModule(BaseQuercusModule module)
    {
        this.quercus.addModule(module);    
        module.initialiseModule(this.quercus.findModule(module.getClass().getName()));
    }
    
    public void registerClass(String name, Class clazz)
    {
        this.quercus.addJavaClass(name, clazz);
    }
    
    public Object executeScript(InputStream is, Writer out, Map<String, Object> model)
    {
        try
        {
            // Create the string writer
            StringWriter writer = new StringWriter(new CharBuffer(1024));
            writer.openWrite();
            
            // Parse the page
            VfsStream stream = new VfsStream(is, null);        
            QuercusPage page = this.quercus.parse(new ReadStream(stream));
            
            // Execute the page
            WriteStream ws = new WriteStream(writer);
            Env env = new Env(this.quercus, page, ws, null, null);    
            
            // Map the contents of the passed model into global variables
            if (model != null)
            {
                for (Map.Entry<String, Object> entry : model.entrySet())
                {
                    setGlobalValue(env, entry.getKey(), entry.getValue());
                }                
            }
            
            // Add the session as a global variable
            Repository repository = new Repository(env);
            setGlobalValue(env, "_REPOSITORY", repository);
            
            // Execute the page
            Value value = page.executeTop(env);
            
            // Make sure we flush becuase otherwise the result does not get written
            ws.flush();
           
            // Write to output
            String result = ((StringWriter)ws.getSource()).getString();            
            if (out != null)
            {
                out.write(result);
            }            
            
            // Return the result
            return value.toJavaObject();
        }
        catch (Exception exception)
        {
            throw new ScriptException("Error executing script.", exception);
        }
    }
    
    private void setGlobalValue(Env env, String name, Object value)
    {
        if (value instanceof String)
        {
            env.setGlobalValue(name, StringValue.create(value));
        }
        // TODO handle other native types ...
        else if (value instanceof ScriptObject)
        {
            JavaClassDef def = this.quercus.getModuleContext().getJavaClassDefinition(((ScriptObject)value).getScriptObjectName());
            System.out.println("Adding global: " + name);
            env.setGlobalValue(name, new JavaValue(env, value, def));
        }
        else
        {
            JavaClassDef def = this.quercus.getModuleContext().getJavaClassDefinition(value.getClass().toString());
            env.setGlobalValue(name, new JavaValue(env, value, def));
        }
    }
}
