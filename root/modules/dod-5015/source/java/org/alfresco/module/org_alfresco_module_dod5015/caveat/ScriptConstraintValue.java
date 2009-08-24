package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.List;
import java.io.Serializable;

public class ScriptConstraintValue implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -4659454215122271811L;
    private String value;
    private List<String>authorityNames;
    
    public void setAuthorityNames(List<String> values)
    {
        this.authorityNames = values;
    }
    public List<String> getAuthorityNames()
    {
        return authorityNames;
    }
    public void setValue(String authorityName)
    {
        this.value = authorityName;
    }
    public String getValue()
    {
        return value;
    }
}
