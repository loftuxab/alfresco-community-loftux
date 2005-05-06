package org.alfresco.repo.dictionary.metamodel.emf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.M2NamespaceURI;
import org.alfresco.repo.dictionary.metamodel.NamespaceDAO;


/**
 * EMF implementation of Meta Model DAO
 * 
 * @author David Caruana
 */
public class EMFNamespaceDAO implements NamespaceDAO
{
    
    /**
     * Resource that holds Model Definitions
     */
    private Resource resource = null;

    /**
     * Index of URIs
     */
    private Map<String,M2NamespaceURI> uriIndex;
    
    /**
     * Index of Prefixes
     */
    private Map<String,M2NamespacePrefix> prefixIndex;

    
    /**
     * Sets the Resource
     * 
     * @param resource  the emf resource
     */
    public void setResource(EMFResource resource)
    {
        this.resource = resource.getResource();
    }
    

    /**
     * Initialise EMF Namespace DAO
     */
    public void init()
    {
        if (resource == null)
        {
            throw new DictionaryException("EMF Resource has not been provided");
        }
        
        // Initialise Object Index (for lookup)
        initIndex(resource);
    }


    public Collection<String> getURIs()
    {
        return uriIndex == null ? null : uriIndex.keySet();
    }


    public Collection<String> getPrefixes()
    {
        return prefixIndex == null ? null : prefixIndex.keySet();
    }


    public M2NamespaceURI getURI(String uri)
    {
        return uriIndex == null ? null : uriIndex.get(uri);
    }


    public M2NamespacePrefix getPrefix(String prefix)
    {
        return prefixIndex == null ? null : prefixIndex.get(prefix);
    }


    public M2NamespaceURI createURI(String uri)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2NamespaceURI namespaceURI = (M2NamespaceURI)factory.createEMFNamespaceURI();
        namespaceURI.setURI(uri);
        resource.getContents().add(namespaceURI);
        indexObject((EObject)namespaceURI);
        return namespaceURI;
    }


    public M2NamespacePrefix createPrefix(String prefix)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2NamespacePrefix namespacePrefix = (M2NamespacePrefix)factory.createEMFNamespacePrefix();
        namespacePrefix.setPrefix(prefix);
        resource.getContents().add(namespacePrefix);
        indexObject((EObject)namespacePrefix);
        return namespacePrefix;
    }


    //
    // EMF Class and Object Indexes (by QName)
    //
    
    private void initIndex(Resource resource)
    {
        uriIndex = new HashMap<String,M2NamespaceURI>();
        prefixIndex = new HashMap<String,M2NamespacePrefix>();
        
        List<EObject> objects = resource.getContents();
        for (EObject eObject : objects)
        {
            indexObject(eObject);
        }
    }
    
    private void indexObject(EObject object)
    {
        // Index Object
        if (object instanceof M2NamespaceURI)
        {
            M2NamespaceURI uri = (M2NamespaceURI)object;
            uriIndex.put(uri.getURI(), uri);
        }
        else if (object instanceof M2NamespacePrefix)
        {
            M2NamespacePrefix prefix = (M2NamespacePrefix)object;
            prefixIndex.put(prefix.getPrefix(), prefix);
        }
    }
        
}
