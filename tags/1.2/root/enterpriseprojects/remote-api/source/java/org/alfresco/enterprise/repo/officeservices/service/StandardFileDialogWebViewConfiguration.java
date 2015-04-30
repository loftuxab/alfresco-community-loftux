package org.alfresco.enterprise.repo.officeservices.service;

import java.util.regex.Pattern;

import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

public class StandardFileDialogWebViewConfiguration implements FileDialogWebViewConfiguration, InitializingBean
{
    
    private FileDialogWebViewRegistry registry;
    
    private String pathPattern;
    
    private Pattern compiledPattern;
    
    private String templateLocation;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "pathPattern", this.pathPattern);
        PropertyCheck.mandatory(this, "templateLocation", this.templateLocation);
        compiledPattern = Pattern.compile(pathPattern);
    }

    public void register()
    {
        if(registry != null)
        {
            registry.registerConfiguration(this);
        }
    }
    
    @Override
    public boolean appliesTo(String path)
    {
        return compiledPattern.matcher(path).matches();
    }

    @Override
    public String getFreeMarkerTemplateLocation()
    {
        return templateLocation;
    }

    public void setRegistry(FileDialogWebViewRegistry registry)
    {
        this.registry = registry;
    }

    public void setPathPattern(String pathPattern)
    {
        this.pathPattern = pathPattern;
    }

    public void setTemplateLocation(String templateLocation)
    {
        this.templateLocation = templateLocation;
    }

}
