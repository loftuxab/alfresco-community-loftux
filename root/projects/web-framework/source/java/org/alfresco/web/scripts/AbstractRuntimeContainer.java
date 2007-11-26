package org.alfresco.web.scripts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;



public abstract class AbstractRuntimeContainer
    implements RuntimeContainer, ApplicationListener, ApplicationContextAware
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbstractRuntimeContainer.class);
    
    private ApplicationContext applicationContext = null;
    private String name = "<undefined>";
    private Registry registry;
    private FormatRegistry formatRegistry;
    private ScriptProcessor scriptProcessor;
    private TemplateProcessor templateProcessor;
    
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    public String getName()
    {
        return name;
    }

    
    
    public Map<String, Object> getScriptParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("server", getDescription());
        return Collections.unmodifiableMap(params);
    }

    public Map<String, Object> getTemplateParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("server", getDescription());
        return Collections.unmodifiableMap(params);
    }


    public void setFormatRegistry(FormatRegistry formatRegistry)
    {
        this.formatRegistry = formatRegistry;
    }
    
    
    public FormatRegistry getFormatRegistry()
    {
        return formatRegistry;
    }

    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }
    
    public Registry getRegistry()
    {
        return registry;
    }

    public void setScriptProcessor(ScriptProcessor scriptProcessor)
    {
        this.scriptProcessor = scriptProcessor;
    }
    
    public ScriptProcessor getScriptProcessor()
    {
        return scriptProcessor;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor)
    {
        this.templateProcessor = templateProcessor;
    }
    
    public TemplateProcessor getTemplateProcessor()
    {
        return templateProcessor;
    }

    public void reset() 
    {
        long startTime = System.currentTimeMillis();
        try
        {
            scriptProcessor.reset();
            templateProcessor.reset();
            registry.reset();
        }
        finally
        {
            if (logger.isInfoEnabled())
                logger.info("Initialised " + getName() + " Web Script Container (in " + (System.currentTimeMillis() - startTime) + "ms)");
        }        
    }

        
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(applicationContext))
            {
                reset();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the Application Context
     * 
     * @return  application context
     */
    protected ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    
}
