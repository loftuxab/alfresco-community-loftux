package org.alfresco.module.alfnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.alfresco.web.scripts.servlet.WebScriptServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractRepositoryWebScript extends AbstractWebScript implements ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(AbstractRepositoryWebScript.class);

    /** The application context. */
    private ApplicationContext applicationContext;
    
    // Component dependencies
    protected Repository repository;
    protected ServiceRegistry services;

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
    
    public void setRepository(Repository repository)
    {
        this.repository = repository; 
    }

    public void setServiceRegistry(ServiceRegistry services)
    {
    	this.services = services;
    }
    
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
    
    protected NodeRef getNodeRef(WebScriptRequest req)
    {
        // NOTE: This web script must be executed in a HTTP Servlet environment
        if (!(req instanceof WebScriptServletRequest))
        {
            throw new WebScriptException("Content retrieval must be executed in HTTP Servlet environment");
        }
        HttpServletRequest httpReq = ((WebScriptServletRequest)req).getHttpServletRequest();
        
        // locate the root path
        String path = (String) httpReq.getParameter("path");
        if(path == null)
        {
        	path = "/images";
        }
        if(path.startsWith("/"))
        {
        	path = path.substring(1, path.length());
        }
        
        // build a path elements list
        List<String> pathElements = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while(tokenizer.hasMoreTokens())
        {
        	String childName = (String) tokenizer.nextToken();
        	pathElements.add(childName);
        }
        
        // look up the child
        NodeRef nodeRef = null;
        try
        {
        	NodeRef companyHomeRef = this.repository.getCompanyHome();
        	nodeRef = getFileFolderService().resolveNamePath(companyHomeRef, pathElements).getNodeRef();
        }
        catch(FileNotFoundException fnfe)
        {
        	throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to locate path");
        }        
        if (nodeRef == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to locate path");
        }

        return nodeRef;
    }
        
    protected void output(WebScriptResponse res, NodeRef nodeRef)
    {
    	ContentReader reader = this.services.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
        
        // stream back
        try
        {
            reader.getContent(res.getOutputStream());
        }
        catch (Exception e)
        {
        	throw new WebScriptException("Unable to stream output");
        }
    	
    }
    
    protected ContentReader getContentReader(NodeRef nodeRef)
    {
    	return this.services.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);    	
    }

    protected ContentWriter getContentWriter(NodeRef nodeRef)
    {
    	return this.services.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);    	
    }
    
    protected String getFilename(NodeRef nodeRef)
    {
        return getFileFolderService().getFileInfo(nodeRef).getName();       
    }

    protected String guessMimetype(NodeRef nodeRef)
    {
        String filename = getFilename(nodeRef);
        return getMimetypeService().guessMimetype(filename);
    }    
    
    
}
