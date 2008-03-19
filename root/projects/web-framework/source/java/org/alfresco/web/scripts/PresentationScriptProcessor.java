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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.scripts.ScriptResourceHelper;
import org.alfresco.scripts.ScriptResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;
import org.springframework.util.FileCopyUtils;


/**
 * Presentation (web tier) Script Processor
 * 
 * @author davidc
 */
public class PresentationScriptProcessor implements ScriptProcessor, ScriptResourceLoader
{
    private static final Log logger = LogFactory.getLog(PresentationScriptProcessor.class);
    private static WrapFactory wrapFactory = new PresentationWrapFactory(); 
    
    private static final String PATH_CLASSPATH = "classpath:";
    
    protected SearchPath searchPath;
    protected ScriptLoader scriptLoader;

    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#findScript(java.lang.String)
     */
    public ScriptContent findScript(String path)
    {
        return scriptLoader.getScript(path);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#executeScript(java.lang.String, java.util.Map)
     */
    public Object executeScript(String path, Map<String, Object> model)
    {
        // locate script within web script stores
        ScriptContent scriptLocation = findScript(path);
        if (scriptLocation == null)
        {
            throw new WebScriptException("Unable to locate script " + path);
        }
        // execute script
        return executeScript(scriptLocation, model);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#executeScript(org.alfresco.web.scripts.ScriptContent, java.util.Map)
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model)
    {
        // TODO: script caching (compiled version)
        
        // TODO: script imports (as RhinoScriptProcessor does)
        
        // read script content from location
        try
        {   
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileCopyUtils.copy(location.getInputStream(), os);  // both streams are closed
            byte[] bytes = os.toByteArray();
            String script = new String(bytes, "UTF-8");
            return executeScriptImpl(
                  ScriptResourceHelper.resolveScriptImports(script, this, logger), model, location.isSecure());
        }
        catch (Throwable e)
        {
            throw new WebScriptException("Failed to load script '" + location.toString() + "': " + e.getMessage(), e);
        }
    }
    
    /**
     * Load a script content from the specific resource path.
     *  
     * @param resource      Script resource to load. Supports either classpath: prefix syntax or a
     *                      resource path within the webscript stores. 
     * 
     * @return the content from the resource, null if not recognised format
     * 
     * @throws AlfrescoRuntimeException on any IO or ContentIO error
     */
    public String loadScriptResource(String resource)
    {
        if (resource.startsWith(PATH_CLASSPATH))
        {
            try
            {
                // load from classpath
                String scriptClasspath = resource.substring(PATH_CLASSPATH.length());
                InputStream stream = getClass().getClassLoader().getResourceAsStream(scriptClasspath);
                if (stream == null)
                {
                    throw new AlfrescoRuntimeException("Unable to load included script classpath resource: " + resource);
                }
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileCopyUtils.copy(stream, os);  // both streams are closed
                byte[] bytes = os.toByteArray();
                // create the string from the byte[] using encoding if necessary
                return new String(bytes, "UTF-8");
            }
            catch (IOException err)
            {
                throw new AlfrescoRuntimeException("Unable to load included script classpath resource: " + resource);
            }
        }
        else
        {
            // locate script within web script stores
            ScriptContent scriptLocation = findScript(resource);
            if (scriptLocation == null)
            {
                throw new WebScriptException("Unable to locate script " + resource);
            }
            try
            {   
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileCopyUtils.copy(scriptLocation.getInputStream(), os);  // both streams are closed
                byte[] bytes = os.toByteArray();
                return new String(bytes, "UTF-8");
            }
            catch (Throwable e)
            {
                throw new WebScriptException(
                        "Failed to load script '" + scriptLocation.toString() + "': " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Execute the supplied script content.
     * 
     * @param script        The script to execute.
     * @param model         Data model containing objects to be added to the root scope.
     * @param secure        True if the script is considered secure and may access java.* libs directly
     * 
     * @return result of the script execution, can be null.
     * 
     * @throws AlfrescoRuntimeException
     */
    private Object executeScriptImpl(String script, Map<String, Object> model, boolean secure)
    {
        // execute script
        long startTime = 0;
        if (logger.isDebugEnabled())
        {
            startTime = System.nanoTime();
        }
        
        Context cx = Context.enter();
        try
        {
            cx.setWrapFactory(wrapFactory);
            Scriptable scope;
            if (!secure)
            {
                scope = cx.initStandardObjects();
                // remove security issue related objects - this ensures the script may not access
                // unsecure java.* libraries or import any other classes for direct access - only
                // the configured root host objects will be available to the script writer
                scope.delete("Packages");
                scope.delete("getClass");
                scope.delete("java");
            }
            else
            {
                // allow access to all libraries and objects, including the importer
                // @see http://www.mozilla.org/rhino/ScriptingJava.html
                scope = new ImporterTopLevel(cx);
            }

            // insert supplied object model into root of the default scope
            if (model != null)
            {
                for (String key : model.keySet())
                {
                    Object obj = model.get(key);
                    ScriptableObject.putProperty(scope, key, obj);
                }
            }
            
            // execute the script
            Object result = cx.evaluateString(scope, script, "AlfrescoScript", 1, null);
            return result;
        }
        catch (Throwable e)
        {
            throw new WebScriptException(e.getMessage(), e);
        }
        finally
        {
            Context.exit();
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.nanoTime();
                logger.debug("Time to execute script: " + (endTime - startTime)/1000000f + "ms");
            }
        }
    }

    /**
     * Convert an object from a script wrapper value to a serializable value valid outside
     * of the Rhino script processor context.
     * 
     * This includes converting JavaScript Array objects to Lists of valid objects.
     * 
     * @param value     Value to convert from script wrapper object to external object value.
     * 
     * @return unwrapped and converted value.
     */
	 public Object unwrapValue(Object value)
	 {
        if (value == null)
        {
            return null;
        }
        else if (value instanceof Wrapper)
        {
            // unwrap a Java object from a JavaScript wrapper
            // recursively call this method to convert the unwrapped value
            value = unwrapValue(((Wrapper)value).unwrap());
        }
        else if (value instanceof ScriptableObject)
        {
            // a scriptable object will probably indicate a multi-value property
            // set using a JavaScript Array object
            ScriptableObject values = (ScriptableObject)value;
            
            if (value instanceof IdScriptableObject)
            {
                if ("Date".equals(((IdScriptableObject)value).getClassName()))
                {
                    Object javaObj = Context.jsToJava(value, Date.class);
                    if (javaObj instanceof Serializable)
                    {
                        value = (Serializable)javaObj;
                    }
                }
                else if (value instanceof NativeArray)
                {
                    // convert JavaScript array of values to a List of objects
                    Object[] propIds = values.getIds();
                    List<Object> propValues = new ArrayList<Object>(propIds.length);
                    for (int i=0; i<propIds.length; i++)
                    {
                        // work on each key in turn
                        Object propId = propIds[i];
                        
                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof Integer)
                        {
                            // get the value out for the specified key
                            Object val = values.get((Integer)propId, values);
                            // recursively call this method to convert the value
                            propValues.add(unwrapValue(val));
                        }
                    }
                    value = propValues;
                }
                else
                {
                    // convert JavaScript map to values to a Map of objects
                    Object[] propIds = values.getIds();
                    Map<String, Object> propValues = new HashMap<String, Object>(propIds.length);
                    for (int i=0; i<propIds.length; i++)
                    {
                        // work on each key in turn
                        Object propId = propIds[i];
                        
                        // we are only interested in keys that indicate a list of values
                        if (propId instanceof String)
                        {
                            // get the value out for the specified key
                            Object val = values.get((String)propId, values);
                            // recursively call this method to convert the value
                            propValues.put((String)propId, unwrapValue(val));
                        }
                    }
                    value = propValues;
                }
            }
        }
        else if (value instanceof Object[])
        {
            // convert back a list Object Java values
            Object[] array = (Object[])value;
            ArrayList<Object> list = new ArrayList<Object>(array.length);
            for (int i=0; i<array.length; i++)
            {
                list.add(unwrapValue(array[i]));
            }
            value = list;
        }
        return value;
	 }

	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.ScriptProcessor#reset()
	 */
    public void reset()
    {
        init();
    }
    

    /**
     * Register script loader from each Web Script Store with Script Processor
     */
    protected void init()
    {
        List<ScriptLoader> loaders = new ArrayList<ScriptLoader>(searchPath.getStores().size());
        for (Store apiStore : searchPath.getStores())
        {
            ScriptLoader loader = apiStore.getScriptLoader();
            if (loader == null)
            {
                throw new WebScriptException("Unable to retrieve script loader for Web Script store " + apiStore.getBasePath());
            }
            loaders.add(loader);
        }
        scriptLoader = new MultiScriptLoader(loaders.toArray(new ScriptLoader[loaders.size()]));
    }

    
    /**
     * Wrap Factory for Rhino Script Engine
     * 
     * @author davidc
     */
    public static class PresentationWrapFactory extends WrapFactory
    {
    	/* (non-Javadoc)
    	 * @see org.mozilla.javascript.WrapFactory#wrapAsJavaObject(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, java.lang.Object, java.lang.Class)
    	 */
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType)
        {
            if (javaObject instanceof Map)
            {
                return new NativeMap(scope, (Map)javaObject);
            }
            return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
        }

    }

}
