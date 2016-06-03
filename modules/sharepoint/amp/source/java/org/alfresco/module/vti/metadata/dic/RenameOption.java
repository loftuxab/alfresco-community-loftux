package org.alfresco.module.vti.metadata.dic;

import java.util.EnumSet;



/**
 * <p>Define the behaviors of a rename operation.</p>
 * 
 * @author Michael Shavnev
 *
 */
public enum RenameOption 
{
    /**
     * The client MUST send "none" if it does not want to specify any of the options given 
     * by a RENAME-OPTION-VAL.
     */
    none,

    /**
     * Creates the parent directory if it does not already exist.
     */
    createdir,
    
    /**
     * Requests that servers, implementing <b>link fixup</b>, fix the linked files other than those moved.
     */
    findbacklinks,
    
    /**
     * Do not perform link fixup on links in moved documents.
     */
    nochangeall,
    
    /**
     * Simulates the move of a directory rather than a file.
     */
    patchprefix;

    
    public static EnumSet<RenameOption> getOptions(String stringValues)
    {
        EnumSet<RenameOption> enumSet = null;
        
        if (stringValues == null || stringValues.trim().length() == 0) 
        {
            enumSet = EnumSet.of(RenameOption.none);
        } 
        else 
        {
            String[] values = stringValues.split(",");
            enumSet = EnumSet.noneOf(RenameOption.class);
            
            for (String value : values)
            {
                enumSet.add(valueOf(value));
            }
        }

        return enumSet;
    }
    
    
}
