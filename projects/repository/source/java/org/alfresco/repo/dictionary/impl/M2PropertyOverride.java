package org.alfresco.repo.dictionary.impl;

public class M2PropertyOverride
{

    private String name;
    private Boolean isMandatory;
    private String defaultValue;
    
    
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
    
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }
}
