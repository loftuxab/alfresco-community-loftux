package org.alfresco.repo.dictionary.metamodel;

import java.util.List;


/**
 * Namespace URI Definition
 * 
 * @author David Caruana
 */
public interface M2NamespaceURI
{
    /**
     * Gets the Namespace URI
     * 
     * @return  the namespace URI
     */
    public String getURI();
    
    /**
     * Sets the Namespace URI
     *  
     * @param uri  the namespace URI
     */
    public void setURI(String uri);
    
    /**
     * Gets the prefixes registered for this URI
     * 
     * @return  the prefixes
     */
    List<M2NamespacePrefix> getPrefixes();
    
}
