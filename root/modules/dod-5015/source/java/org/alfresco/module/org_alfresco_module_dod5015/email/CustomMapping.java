package org.alfresco.module.org_alfresco_module_dod5015.email;

public class CustomMapping
{
    private String from;
    private String to;
    
    public CustomMapping() 
    {
        
    }
    
    public void setFrom(String from)
    {
        this.from = from;
    }
    
    public String getFrom()
    {
        return from;
    }
    
    public void setTo(String to)
    {
        this.to = to;
    }
    
    public String getTo()
    {
        return to;
    }
    
    public int hashCode()
    {
        if(from != null && to != null)
        {
            return (from + to).hashCode();
        }
        else
        {
            return 1;
        }
    }

}
