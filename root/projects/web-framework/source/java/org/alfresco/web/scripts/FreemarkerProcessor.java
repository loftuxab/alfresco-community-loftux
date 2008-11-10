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

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractProcessor;
import org.alfresco.web.framework.render.ProcessorContext;
import org.alfresco.web.framework.render.RenderContext;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of a renderer that executes a Freemarker template.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class FreemarkerProcessor extends AbstractProcessor
{
    private static final String SCRIPT_RESULTS = "freemarkerRendererScriptResults";
    private PresentationTemplateProcessor templateProcessor;
    private PresentationScriptProcessor scriptProcessor;
    private Store templateStore;
    
    public void init(ApplicationContext applicationContext)
    {
    	this.templateStore.init();
    }
    
    /**
     * Sets the template processor.
     * 
     * @param templateProcessor the new template processor
     */
    public void setTemplateProcessor(PresentationTemplateProcessor templateProcessor)
    {
    	this.templateProcessor = templateProcessor;
    }
    
    /**
     * Gets the template processor.
     * 
     * @return the template processor
     */
    public PresentationTemplateProcessor getTemplateProcessor()
    {
    	return this.templateProcessor;
    }
    
    /**
     * Sets the script processor.
     * 
     * @param scriptProcessor the new script processor
     */
    public void setScriptProcessor(PresentationScriptProcessor scriptProcessor)
    {
    	this.scriptProcessor = scriptProcessor;
    }
    
    /**
     * Gets the script processor.
     * 
     * @return the script processor
     */
    public PresentationScriptProcessor getScriptProcessor()
    {
    	return this.scriptProcessor;
    }
    
    /**
     * Sets the template store.
     * 
     * @param templateStore the new template store
     */
    public void setTemplateStore(Store templateStore)
    {
    	this.templateStore = templateStore;
    }
    
    /**
     * Gets the template store.
     * 
     * @return the template store
     */
    public Store getTemplateStore()
    {
    	return this.templateStore;
    }
    
    public void executeHeader(ProcessorContext pc)
        throws RendererExecutionException
    {
    	RenderContext context = pc.getRenderContext();
    	String uri = this.getProperty(pc, "uri");
        
        // the current format
        String format = context.getFormatId();
        
        /**
         * Attempt to execute the templates associated .head. file, if it has one
         */
        
        // path to the template (switches on format)        
        String templateName = uri + ((format != null && format.length() != 0 && !context.getConfig().getDefaultFormatId().equals(format)) ? ("." + format + ".head.ftl") : ".head.ftl");            
        if (templateProcessor.hasTemplate(templateName))
        {
        	try
        	{
	            // build the model
	            Map<String, Object> model = new HashMap<String, Object>(32);
	            ProcessorModelHelper.populateTemplateModel(context, model);
	            
	            templateProcessor.process(templateName, model, context.getResponse().getWriter());
        	}
        	catch(UnsupportedEncodingException uee)
        	{
        		throw new RendererExecutionException(uee);
        	}
        	catch(IOException ioe)
        	{
        		throw new RendererExecutionException(ioe);
        	}
        }
    }
 
    /**
     * Execute.
     * 
     * @param pc the processor context
     * 
     * @throws RendererExecutionException the renderer execution exception
     */
    public void executeBody(ProcessorContext pc)
            throws RendererExecutionException
    {
    	RenderContext context = pc.getRenderContext();
    	String uri = this.getProperty(pc, "uri");
        
        // the current format
        String format = context.getFormatId();
        
        // Now execute the real template
        String templateName = null;
        try
        {
            // the result model
            Map<String, Object> resultModel = null;
            
            if (context.getObject() instanceof TemplateInstance)
            {
                if (context.hasValue(SCRIPT_RESULTS) == false)
                {
                    // Attempt to execute a .js file for this page template
                    resultModel = new HashMap<String, Object>(8, 1.0f);
                    ScriptContent script = templateStore.getScriptLoader().getScript(uri + ".js");
                    if (script != null)
                    {
                        // build the model
                        Map<String, Object> scriptModel = new HashMap<String, Object>(8);
                        ProcessorModelHelper.populateScriptModel(context, scriptModel);
                        
                        // add in the model object
                        scriptModel.put("model", resultModel);
                        
                        // execute the script
                        scriptProcessor.executeScript(script, scriptModel);
                    }
                    
                    // store the result model in the request context for the next pass
                    // this removes the need to execute the script twice
                    if (context.isPassiveMode())
                    {
                        context.setValue(SCRIPT_RESULTS, (Serializable)resultModel);
                    }
                }
                else
                {
                    // retrieve results from the request context - we already executed a pass
                    resultModel = (Map<String, Object>)context.getValue(SCRIPT_RESULTS);
                    
                    // remove the results from the context - we do not want other templates finding it
                    context.removeValue(SCRIPT_RESULTS);
                }
            }
            
            // Execute the template file itself
            Map<String, Object> templateModel = new HashMap<String, Object>(32);
            ProcessorModelHelper.populateTemplateModel(context, templateModel);
            
            // merge script results model into the template model
            // these may not exist if a .js file was not found
            if (resultModel != null)
            {
                for (Map.Entry<String, Object> entry : resultModel.entrySet())
                {
                    // retrieve script model value and unwrap each java object from script object
                    Object value = entry.getValue();
                    Object templateValue = scriptProcessor.unwrapValue(value);
                    templateModel.put(entry.getKey(), templateValue);
                }
            }
            
            // path to the template (switches on format)
            templateName = uri + ".ftl";
            if (format != null && format.length() != 0 && !context.getConfig().getDefaultFormatId().equals(format))
            {
                templateName = uri + "." + format + ".ftl";
            }
            
            // process the template
            templateProcessor.process(templateName, templateModel, context.getResponse().getWriter());
        }
        catch(Exception ex)
        {
            throw new RendererExecutionException("FreemarkerRenderer failed to process template: " + templateName, ex);
        }
    }
}
