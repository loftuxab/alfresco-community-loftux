package org.alfresco.module.org_alfresco_module_dod5015.caveat;

public class RMConstraintInfo
{    
    private String name;
    private String title;
    private boolean caseSensitive;
    private String[] allowedValues;
    
    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getTitle()
    {
        return title;
    }
    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }
    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }
    public void setAllowedValues(String[] values)
    {
        this.allowedValues = values;
    }
    public String[] getAllowedValues()
    {
        return allowedValues;
    }
    

    
}
