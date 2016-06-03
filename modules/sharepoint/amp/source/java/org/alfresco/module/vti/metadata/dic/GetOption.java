package org.alfresco.module.vti.metadata.dic;

import java.util.EnumSet;

/**
 * <p>Used to define the behavior of retrieving documents operations.</p>
 *
 * @author Michael Shavnev
 *
 */
public enum GetOption 
{
    /**
     * Do not check out the file
     */
    none,

    /**
     * Check out the file exclusively
     */
    chkoutExclusive,

    /**
     * Check out the file non-exclusively
     */
    chkoutNonExclusive;

    
    public static EnumSet<GetOption> getOptions(String stringValues)
    {
        EnumSet<GetOption> enumSet = null;
        
        if (stringValues == null || stringValues.trim().length() == 0) 
        {
            enumSet = EnumSet.of(GetOption.none);
        } 
        else 
        {
            String[] values = stringValues.split(",");
            enumSet = EnumSet.noneOf(GetOption.class);
            
            for (String value : values)
            {
                enumSet.add(valueOf(value));
            }
        }

        return enumSet;
    }
    
}