package org.alfresco.module.vti.handler.alfresco;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.InitializingBean;

/**
 * The default implementation of {@link UrlHelper}.
 * 
 * @author Matt Ward
 */
public class DefaultUrlHelper implements UrlHelper, InitializingBean
{
    private LocalHostNameProvider localHostNameProvider;
    private String externalProtocol;
    private String externalHost;
    private int externalPort;
    private String externalContextPath;
    private String baseURL;
    private String hostURL;
    
    protected String makeExternalHostURL()
    {
        URI uri;
        try
        {
            uri = new URI(externalProtocol, null, externalHost, externalPort, null, null, null);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Unable to construct valid Sharepoint URL.", e);
        }
    
        return uri.toString();
    }
    
    @Override
    public String getExternalURL(String pathWithinContext)
    {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(externalContextPath);
        if (pathWithinContext != null && pathWithinContext.length() > 0)
        {
            if (!pathWithinContext.startsWith("/"))
            {
                addTrailingSlash(pathBuilder);
            }
            pathBuilder.append(pathWithinContext);
        }
        removeTrailingSlash(pathBuilder);
        final String path = pathBuilder.toString();
        
        URI uri;
        try
        {
            uri = new URI(externalProtocol, null, externalHost, externalPort, path, null, null);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Unable to construct valid Sharepoint URL.", e);
        }
    
        return uri.toString();
    }
    
    /**
     * Remove a trailing slash from the path, if present.
     * 
     * @param path
     */
    private void removeTrailingSlash(StringBuilder path)
    {
        if (path.charAt(path.length() - 1) == '/')
        {
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * Append a trailing slash to a path if one is not already present.
     * 
     * @param path
     */
    private void addTrailingSlash(StringBuilder path)
    {
        if (path.charAt(path.length() - 1) != '/')
        {
            path.append('/');
        }
    }

    @Override
    public String getExternalBaseURL()
    {   
        return baseURL;
    }

    @Override
    public String getExternalURLHostOnly()
    {
        return hostURL;
    }
    
    
    public void setLocalHostNameProvider(LocalHostNameProvider localHostNameProvider)
    {
        this.localHostNameProvider = localHostNameProvider;
    }

    public void setExternalProtocol(String externalProtocol)
    {
        this.externalProtocol = externalProtocol;
    }

    public void setExternalHost(String externalHost)
    {
        this.externalHost = externalHost;
    }

    public void setExternalPort(int externalPort)
    {
        this.externalPort = externalPort;
    }

    public void setExternalContextPath(String externalContextPath)
    {
        this.externalContextPath = externalContextPath;
    }

    /**
     * Must be called after setting properties and before using provided services.
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        // Substitue special ${localname} token if necessary.
        externalHost = localHostNameProvider.subsituteHost(externalHost);
        baseURL = getExternalURL("");
        hostURL = makeExternalHostURL();
    }
}
