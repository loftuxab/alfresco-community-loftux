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
import org.alfresco.module.phpIntegration.lib.Session;
import org.alfresco.module.phpIntegration.lib.SpacesStore;
import org.alfresco.module.phpIntegration.lib.Store;
import org.alfresco.module.phpIntegration.module.BaseQuercusModule;
import org.alfresco.service.cmr.repository.ScriptException;

import com.caucho.quercus.Quercus;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.util.CharBuffer;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.StringWriter;
import com.caucho.vfs.VfsStream;
import com.caucho.vfs.WriteStream;

public class PHPEngine
{
    private Quercus quercus = new Quercus();
    
    public void init()
    {
        // Add the libarary classes 
        // TODO move this config to spring
        registerClass("Session", Session.class);
        registerClass("Node", Node.class);
        registerClass("Store", Store.class);
        registerClass("SpacesStore", SpacesStore.class);
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
}
