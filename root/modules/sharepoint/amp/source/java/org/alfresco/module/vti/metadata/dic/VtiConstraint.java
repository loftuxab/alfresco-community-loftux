
package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard constraints for the parameters.</p>
 * 
 * @author Michael Shavnev
 */
public enum VtiConstraint
{
    /**
     * Read-only
     */
    R ("R"),  
    
    /**
     * Write
     */
    W ("W"),  
    
    /**
     * Ignore
     */
    X ("X");  
    
    private final String value;
    
    VtiConstraint(String value) 
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
