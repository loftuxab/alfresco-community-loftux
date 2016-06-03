package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard workspace types.</p>
 * 
 * @author PavelYur
 */
public enum WorkspaceType
{
    /**
     * document work space
     */
    DWS ("DWS"),   
    
    /**
     * meeting work space
     */
    MWS ("MWS"),   
    
    /**
     * sharepoint site
     */
    SPS ("SPS"),   
    
    /**
     * empty type
     */
    EMPTY ("");    
    
   private final String value;
    
   WorkspaceType(String value) 
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
