package org.alfresco.repo.dictionary;

import java.util.List;
import java.util.Properties;


/**
 * Property override definition
 * 
 * @author David Caruana
 *
 */
public class M2PropertyOverride
{
    private String name;
    private Boolean isMandatory;
    private Boolean isMandatoryEnforced;
    private String defaultValue;
    private List<M2Constraint> constraints;
    private Properties configProperties = new Properties();
    
    /*package*/ M2PropertyOverride()
    {
    }

    
    public String getName()
    {
        return name;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public Boolean isMandatory()
    {
        return isMandatory;
    }

    
    public void setMandatory(Boolean isMandatory)
    {
        this.isMandatory = isMandatory;
    }
    
    public Boolean isMandatoryEnforced()
    {
        return isMandatoryEnforced;
    }
    
    public String getDefaultValue()
    {
        if (defaultValue != null && M2Class.PROPERTY_PLACEHOLDER.matcher(defaultValue).matches())
        {
            String key = defaultValue.substring(defaultValue.indexOf("${") + 2, defaultValue.indexOf("}"));
            String value = defaultValue.substring(defaultValue.indexOf("|") + 1);
            
            return configProperties.getProperty(key, value);
        }
        else
        {
            return defaultValue;
        }
    }
    
    
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public List<M2Constraint> getConstraints()
    {
        return constraints;
    }    
    
    public void setConfigProperties(Properties configProperties)
    {
        this.configProperties = configProperties;
    }
}
