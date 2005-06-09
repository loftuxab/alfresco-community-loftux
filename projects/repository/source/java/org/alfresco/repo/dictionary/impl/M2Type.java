package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Type Definition
 * 
 * @author David Caruana
 *
 */
public class M2Type extends M2Class
{
    private List<String> mandatoryAspects = new ArrayList<String>();
    
    /*package*/ M2Type()
    {
        super();
    }
    
   
    public void addMandatoryAspect(String name)
    {
        mandatoryAspects.add(name);
    }
    
    
    public void removeMandatoryAspect(String name)
    {
        mandatoryAspects.remove(name);
    }
    

    public List<String> getMandatoryAspects()
    {
        return Collections.unmodifiableList(mandatoryAspects);
    }    

}
