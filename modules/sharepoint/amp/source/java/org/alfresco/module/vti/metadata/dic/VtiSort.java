package org.alfresco.module.vti.metadata.dic;


/**
 * <p>Enum of the standard sorting types.</p>
 * 
 * @author PavelYur
 */
public enum VtiSort
{
    ASC ("Asc"),
    
    DESC ("Desc");
    
    
    private final String value;
    
    VtiSort(String value)
    {
        this.value = value;
    }
    
    public static VtiSort value(String stringValue)
    {
        VtiSort[] values = values();
        for (VtiSort value:values)
        {
            if (stringValue.equals(value.value))
                return value;
        }
        return null;
    }
    
    public String toString ()
    {
        return value;
    }
}
