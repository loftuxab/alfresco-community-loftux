package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;

import org.alfresco.config.ConfigException;
import org.springframework.web.context.ServletContextAware;

/**
 * ConfigSource implementation that gets its data via files in a web
 * application.
 * 
 * TODO: Also deal with the source being specified as an init param i.e.
 * param:config.files
 * 
 * @author gavinc
 */
public class WebAppConfigSource extends BaseConfigSource implements ServletContextAware
{
    private ServletContext servletCtx;

    /**
     * @param sources
     *            List of paths to files in a web application
     */
    public WebAppConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletCtx = servletContext;
    }

    /**
     * @see org.alfresco.config.source.BaseConfigSource#getInputStream(java.lang.String)
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = null;

        try
        {
            String fullPath = this.servletCtx.getRealPath(sourceString);
            is = new BufferedInputStream(new FileInputStream(fullPath));
        } catch (IOException ioe)
        {
            throw new ConfigException("Failed to obtain input stream to file: " + sourceString,
                    ioe);
        }

        return is;
    }
}
