package org.alfresco.repo.importer.view;

import java.util.Properties;

import org.alfresco.repo.importer.Progress;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;


/**
 * Maintains state about the currently imported element.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ElementContext
{
    // Dictionary Service
    private DictionaryService dictionary;
    
    // Element Name
    private QName elementName;
    
    // Configuration Properties
    private Properties configuration;
    
    // Importer Progress
    private Progress progress;
    
    
    /**
     * Construct
     * 
     * @param dictionary
     * @param elementName
     * @param progress
     */
    /*package*/ ElementContext(DictionaryService dictionary, QName elementName, Properties configuration, Progress progress)
    {
        this.dictionary = dictionary;
        this.elementName = elementName;
        this.configuration = configuration;
        this.progress = progress;
    }
    
    /**
     * @return  the element name
     */
    /*package*/ QName getElementName()
    {
        return elementName;
    }
    
    /**
     * @return  the importer progress
     */
    /*package*/ Progress getImporterProgress()
    {
        return progress;
    }

    /**
     * @return  the configuration
     */
    /*package*/ Properties getConfiguration()
    {
        return configuration;
    }
    
    /**
     * @return  the dictionary service
     */
    /*package*/ DictionaryService getDictionaryService()
    {
        return dictionary;
    }
}
