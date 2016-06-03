package org.alfresco.repo.dictionary;

import java.util.List;

/**
 * Definition of a named value that can be used for property injection.
 * 
 * @author Derek Hulley
 */
public class M2NamedValue
{
    private String name;
    private String simpleValue = null;
    private List<String> listValue = null;
    
    /*package*/ M2NamedValue()
    {
    }


    @Override
    public String toString()
    {
        return (name + "=" + (simpleValue == null ? listValue : simpleValue));
    }

    public String getName()
    {
        return name;
    }
    
    /**
     * @return Returns the raw, unconverted value
     */
    public String getSimpleValue()
    {
        return simpleValue;
    }
    
    /**
     * @return Returns the list of raw, unconverted values
     */
    public List<String> getListValue()
    {
        return listValue;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setSimpleValue(String simpleValue)
    {
        this.simpleValue = simpleValue;
    }
    
    public void setListValue(List<String> listValue)
    {
        this.listValue = listValue;
    }
    
    public boolean hasSimpleValue()
    {
        return (this.simpleValue != null);
    }
    
    public boolean hasListValue()
    {
        return (this.listValue != null);
    }

}
