package org.alfresco.repo.dictionary.metamodel.emf;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl;


/**
 * An EMF Resource represents a EMF persistent store.
 * 
 * @author David Caruana
 */
public class EMFResource
{

    /**
     * Default Resource URI
     */
    public static final String DEFAULT_RESOURCEURI = "classpath:/com/alfresco/repo/dictionary/metamodel/emf/dictionary.xml";

    /**
     * Resource URI for Model Definitions
     */
    private String uri = DEFAULT_RESOURCEURI;
    
    /**
     * Resource that holds Model Definitions
     */
    private Resource resource = null;

    /**
     * Sets the Resource URI
     * 
     * @param uri  the resource URI
     */
    public void setURI(String uri)
    {
        this.uri = uri;
    }
    

    /**
     * Initialise EMF Resource
     * 
     * Note: A new Resource is created, even if one already exists.
     */
    public void initCreate()
    {
        initEMF();
        
        // Perform Validation
        URI theURI;
        try
        {
            theURI = URI.createFileURI(uri);
        }
        catch(IllegalArgumentException e)
        {
            throw new DictionaryException("Cannot create the resource " + uri + " as it is not a file.");
        }
        
        // Create EMF Resource
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.setURIConverter(new EMFURIConverterImpl());
        resource = resourceSet.createResource(theURI);
    }


    /**
     * Initialise EMF Resource
     * 
     * Note: Resource must already exist
     */
    public void init()
    {
        initEMF();
        
        // Load EMF Resource
        URI theURI = URI.createURI(uri);
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.setURIConverter(new EMFURIConverterImpl());
        resource = resourceSet.getResource(theURI, true);
    }


    /**
     * Initialise EMF Framework
     */
    private void initEMF()
    {
        // Initialise EMF Package
        EmfPackageImpl.init();

        // Register Resource Factories for classpath protocol and xml
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map protocol = reg.getProtocolToFactoryMap();
        protocol.put("file", new XMIResourceFactoryImpl());
        protocol.put("classpath", new XMIResourceFactoryImpl());
        Map ext = reg.getExtensionToFactoryMap();
        ext.put("xml", new XMIResourceFactoryImpl());
    }

    
    /**
     * Gets the Resource
     * 
     * @return  the persistent resource
     */
    public Resource getResource()
    {
        return resource;
    }
    

    /**
     * Save the Resource
     */
    public void save()
    {
        try
        {
            resource.save(null);
        }
        catch(IOException e)
        {
            throw new DictionaryException("Failed to save EMF Resource " + uri, e);
        }
    }
    
}
