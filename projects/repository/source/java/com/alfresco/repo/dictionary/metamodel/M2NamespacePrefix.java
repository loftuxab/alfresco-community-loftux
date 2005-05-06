package org.alfresco.repo.dictionary.metamodel;


/**
 * Namespace Prefix Definition
 * 
 * @author David Caruana
 */
public interface M2NamespacePrefix
{
    /**
     * Gets the Namespace Prefix
     * 
     * @return  the namespace prefix
     */
    public String getPrefix();
    
    /**
     * Sets the Namespace Prefix
     *  
     * @param uri  the namespace prefix
     */
    public void setPrefix(String prefix);
    
    /**
     * Gets the associated Namespace URI
     */
    public M2NamespaceURI getURI();

    /**
     * Sets the associated Namespace URI
     */
    public void setURI(M2NamespaceURI uri);
    
}
