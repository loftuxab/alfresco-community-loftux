package org.alfresco.module.alfnetwork;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractRepositoryWebScript extends AbstractWebScript
	implements ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(AbstractRepositoryWebScript.class);
	
       
    /** The application context. */
    private ApplicationContext applicationContext;

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Gets the application context.
     * 
     * @return the application context
     */
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    

    // temporary: for 2.2
    protected ServiceRegistry services = this.getServiceRegistry();

    // restore the following for 2.9 / 3.0
    /*
    protected Repository repository;
    public void setRepository(Repository repository)
    {
        this.repository = repository; 
    }

    public void setServiceRegistry(ServiceRegistry services)
    {
    	this.services = services;
    }
    */
    
    protected NodeService getNodeService()
    {
    	return services.getNodeService();
    }
    
    protected ContentService getContentService()
    {
    	return services.getContentService();
    }
    
    protected MimetypeService getMimetypeService()
    {
    	return services.getMimetypeService();
    }
    
    protected FileFolderService getFileFolderService()
    {
    	return services.getFileFolderService();
    }
}