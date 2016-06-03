package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard CAML methods.</p>
 * 
 * @author PavelYur
 */
public enum CAMLMethod
{
    
    NEW ("New"),
    
    UPDATE ("Update"),
    
    DELETE ("Delete");
    
    private final String value;
    
    CAMLMethod(String value)
    {
        this.value = value;
    }    
    
    public static CAMLMethod value(String stringValue)
    {
        for (CAMLMethod camlMethod : CAMLMethod.values())
        {
            if (stringValue.equals(camlMethod.value))
            {
                return camlMethod;
            }
        }        
        return null;        
    }
    
    public String toString()
    {        
        return value;
    }
    

}
