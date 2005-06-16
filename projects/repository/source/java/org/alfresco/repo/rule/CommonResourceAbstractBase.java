/**
 * Created on Jun 17, 2005
 */
package org.alfresco.repo.rule;

import java.io.IOException;
import java.util.Properties;

import org.alfresco.service.cmr.rule.RuleServiceException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.io.Resource;

/**
 * Common resouce abstract base class.
 * 
 * @author Roy Wetherall
 */
public abstract class CommonResourceAbstractBase implements BeanNameAware
{
    /**
     * The bean name
     */
    protected String name;

    /**
     * The properties object
     */
    protected Properties properties;

    /**
     * Set the bean name
     * 
     * @param name
     *            the bean name
     */
    public void setBeanName(String name)
    {
        this.name = name;
    }

    /**
     * Set the resource
     * 
     * @param resource
     *            the resource object
     */
    public void setResource(Resource resource)
    {
        try
        {
            this.properties = new Properties();
            this.properties.load(resource.getInputStream());
        } 
        catch (IOException exception)
        {
            throw new RuleServiceException("Unable to load resource file: " + resource.getFilename(), exception);
        }
    }

}
