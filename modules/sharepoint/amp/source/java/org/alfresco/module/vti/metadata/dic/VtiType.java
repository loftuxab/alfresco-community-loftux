
package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard types that is used in alfresco implementation of the
 * frontpage protocol.</p>
 * 
 * @author Michael Shavnev
 */
public enum VtiType
{
    INT ("I"),
    TIME ("T"),
    BOOLEAN ("B"),
    DOUBLE ("D"),
    STRING ("S"),
    VECTOR ("V");

    
    private final String value;
    
    VtiType(String value) 
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
