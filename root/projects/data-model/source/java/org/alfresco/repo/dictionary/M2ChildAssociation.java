package org.alfresco.repo.dictionary;


/**
 * Child Association definition.
 * 
 * @author David Caruana
 *
 */
public class M2ChildAssociation extends M2ClassAssociation
{
    private String requiredChildName = null;
    private Boolean allowDuplicateChildName = null;
    private Boolean propagateTimestamps = null;
    
    
    /*package*/ M2ChildAssociation()
    {
    }
    
    
    /*package*/ M2ChildAssociation(String name)
    {
        super(name);
    }
    

    public String getRequiredChildName()
    {
        return requiredChildName;
    }
    
    
    public void setRequiredChildName(String requiredChildName)
    {
        this.requiredChildName = requiredChildName;
    }
    
    
    public boolean allowDuplicateChildName()
    {
        return allowDuplicateChildName == null ? true : allowDuplicateChildName;
    }
    
    
    public void setAllowDuplicateChildName(boolean allowDuplicateChildName)
    {
        this.allowDuplicateChildName = allowDuplicateChildName;
    }

    public boolean isPropagateTimestamps()
    {
        return propagateTimestamps == null ? false : propagateTimestamps;
    }
    
    public void setPropagateTimestamps(boolean propagateTimestamps)
    {
        this.propagateTimestamps = propagateTimestamps;
    }
}
