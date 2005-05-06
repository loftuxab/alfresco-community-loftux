package org.alfresco.repo.dictionary;

import java.util.List;

/**
 * Read-only definition of a Type
 * 
 * @author David Caruana
 */
public interface TypeDefinition extends ClassDefinition
{
    /**
     * @return  the default aspects associated with this type
     */
    public List<AspectDefinition> getDefaultAspects();
    
    /**
     * TODO: examine this property furher - will we support this? is it in the right place? 
     * 
     * @return  true => orderable, false => not orderable
     */
    public boolean getOrderedChildren();
}
