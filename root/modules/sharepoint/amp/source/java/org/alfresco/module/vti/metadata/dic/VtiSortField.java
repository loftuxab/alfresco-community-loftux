package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard sorting fields.</p>
 * 
 * @author PavelYur
 */
public enum VtiSortField
{
    /**
     * sort by name
     */
    NAME ("BaseName"),                     

    /**
     * sort by type
     */
    TYPE ("DocIcon"),                      

    /**
     * sort by modifier
     */
    MODIFIEDBY ("Editor"),                 

    /**
     * sort by last modified date
     */
    MODIFIED ("Last_x0020_Modified"),      

    /**
     * sort by checked out username
     */
    CHECKEDOUTTO ("CheckedOutTitle");      

    private String value;
    
    VtiSortField(String value)
    {
        this.value = value;
        
    }    
    
    public String toString()
    {        
        return value;
    }
    
    public static VtiSortField value(String stringValue)
    {
        VtiSortField[] values = values();
        for (VtiSortField value:values)
        {
            if (stringValue.equals(value.value))
                return value;
        }
        return null;
    }
    
}
