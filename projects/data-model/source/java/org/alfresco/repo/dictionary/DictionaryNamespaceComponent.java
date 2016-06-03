package org.alfresco.repo.dictionary;

import java.util.Collection;

import org.alfresco.service.namespace.NamespaceService;


/**
 * Data Dictionary Namespace Service Implementation
 * 
 * @author David Caruana
 */
public class DictionaryNamespaceComponent implements NamespaceService
{
    /**
     * Namespace DAO
     */
    private NamespaceDAO namespaceDAO;


    /**
     * Sets the Namespace DAO
     * 
     * @param namespaceDAO  namespace DAO
     */
    public void setNamespaceDAO(NamespaceDAO namespaceDAO)
    {
        this.namespaceDAO = namespaceDAO;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.NamespaceService#getURIs()
     */
    public Collection<String> getURIs()
    {
        return namespaceDAO.getURIs();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.NamespaceService#getPrefixes()
     */
    public Collection<String> getPrefixes()
    {
        return namespaceDAO.getPrefixes();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.ref.NamespacePrefixResolver#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix)
    {
        return namespaceDAO.getNamespaceURI(prefix);
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.ref.NamespacePrefixResolver#getPrefixes(java.lang.String)
     */
    public Collection<String> getPrefixes(String namespaceURI)
    {
        return namespaceDAO.getPrefixes(namespaceURI);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.service.namespace.NamespaceService#registerNamespace(java.lang.String, java.lang.String)
     */
    public void registerNamespace(String prefix, String uri)
    {
        // TODO:
        throw new UnsupportedOperationException();        
    }


    /* (non-Javadoc)
     * @see org.alfresco.service.namespace.NamespaceService#registerNamespace(java.lang.String, java.lang.String)
     */
    public void unregisterNamespace(String prefix)
    {
        // TODO:
        throw new UnsupportedOperationException();        
    }
}
