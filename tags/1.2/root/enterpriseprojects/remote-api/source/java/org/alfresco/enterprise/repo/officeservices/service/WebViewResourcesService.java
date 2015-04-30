package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WebViewResourcesService extends HttpServlet
{

    private static final long serialVersionUID = 528147352679187571L;

    private static Map<String, byte[]> resourcesMap = new HashMap<String, byte[]>();

    private static final ReadWriteLock resourceMapLock = new ReentrantReadWriteLock();
    
    private WebApplicationContext applicationContext;

    @Override
    public void init() throws ServletException
    {
        super.init();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(applicationContext == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String resourcePath = req.getPathInfo();
        byte[] resource = null;
        try
        {
            resourceMapLock.readLock().lock();
            resource = resourcesMap.get(resourcePath);
        }
        finally
        {
            resourceMapLock.readLock().unlock();
        }
        if (resource == null)
        {
            resource = cacheResource(resourcePath);
        }
        if (resource == null)
        {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("404: Object Not Found");
        }
        else
        {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getOutputStream().write(resource);
        }
    }

    private byte[] cacheResource(String resourceLocation) throws IOException
    {
        // normalize the path
        if(resourceLocation == null)
        {
            return null;
        }
        resourceLocation = StringUtils.cleanPath(resourceLocation);
        if(!resourceLocation.startsWith("/"))
        {
            resourceLocation = "/" + resourceLocation;
        }
        
        // Prevent access to protected resources
        if(resourceLocation.toUpperCase().startsWith("/WEB-INF/") || resourceLocation.startsWith("/../") || (resourceLocation.indexOf(':') > 0))
        {
            return null;
        }
        
        // Limit access to specific folders
        if(!(resourceLocation.startsWith("/images/")||resourceLocation.startsWith("/css/")||resourceLocation.startsWith("/js/")))
        {
            return null;
        }
        
        Resource resource = applicationContext.getResource(resourceLocation);

        if (!resource.exists())
        {
            if (resourceLocation.endsWith(BrowsingService.IMAGE_POSTFIX) && resourceLocation.startsWith("/"+BrowsingService.IMAGE_PREFIX))
            {
                resource = applicationContext.getResource("/"+BrowsingService.DEFAULT_IMAGE);
                if(!resource.exists())
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        InputStream input = resource.getInputStream();
      
        byte[] result = new byte[input.available()];
        input.read(result);

        try
        {
            resourceMapLock.writeLock().lock();
            resourcesMap.put(resourceLocation, result);
        }
        finally
        {
            resourceMapLock.writeLock().unlock();
        }

        return result;
    }

}
