package com.activiti.repo.dictionary;

import java.util.List;


/**
 * Read-only definition of a Type
 * 
 * @author David Caruana
 */
public interface TypeDefinition extends ClassDefinition
{

    /**
     * Gets the default list of aspects that are associated with this Type
     * 
     * @return  the default aspects
     */
    public List<ClassRef> getDefaultAspects();
    
    
    /**
     * Gets whether child nodes are orderable
     *
     * TODO: examine this property furher - will we support this? is it in the right place? 
     * 
     * @return  true => orderable, false => not orderable
     */
    public boolean getOrderedChildren();

}
