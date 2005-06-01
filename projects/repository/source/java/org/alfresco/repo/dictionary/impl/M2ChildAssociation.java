package org.alfresco.repo.dictionary.impl;

public class M2ChildAssociation extends M2ClassAssociation
{

    private String requiredChildName = null;
    private boolean allowDuplicateChildName = true;
    
    
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
        return allowDuplicateChildName;
    }
    
    public void setAllowDuplicateChildName(boolean allowDuplicateChildName)
    {
        this.allowDuplicateChildName = allowDuplicateChildName;
    }



}
