package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.List;

public class ScriptConstraint
{
    private String authorityName;
    private List<String>values;
    public void setValues(List<String> values)
    {
        this.values = values;
    }
    public List<String> getValues()
    {
        return values;
    }
    public void setAuthorityName(String authorityName)
    {
        this.authorityName = authorityName;
    }
    public String getAuthorityName()
    {
        return authorityName;
    }

}
