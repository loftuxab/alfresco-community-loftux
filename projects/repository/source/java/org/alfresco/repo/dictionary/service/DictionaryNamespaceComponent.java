package org.alfresco.repo.dictionary.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.M2NamespaceURI;
import org.alfresco.repo.dictionary.metamodel.NamespaceDAO;
import org.alfresco.repo.ref.NamespaceException;


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
        M2NamespacePrefix namespacePrefix = namespaceDAO.getPrefix(prefix);
        if (namespacePrefix == null)
        {
            throw new NamespaceException("Prefix '" + prefix + "' has not been registered.");
        }
        return namespacePrefix.getURI().getURI();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.ref.NamespacePrefixResolver#getPrefixes(java.lang.String)
     */
    public Collection<String> getPrefixes(String namespaceURI)
    {
        M2NamespaceURI URI = namespaceDAO.getURI(namespaceURI);
        if (URI == null)
        {
            throw new NamespaceException("URI '" + namespaceURI + "' has not been registered.");
        }
        List<M2NamespacePrefix> namespacePrefixes = URI.getPrefixes();
        List<String> prefixes = new ArrayList<String>(namespacePrefixes.size());
        for (M2NamespacePrefix namespacePrefix : namespacePrefixes)
        {
            prefixes.add(namespacePrefix.getPrefix());
        }
        return Collections.unmodifiableCollection(prefixes);
    }
    
}
