package org.alfresco.repo.importer.view;

import org.alfresco.repo.importer.ImporterProgress;
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
    
    // Importer Progress
    private ImporterProgress progress;
    
    
    /**
     * Construct
     * 
     * @param dictionary
     * @param elementName
     * @param progress
     */
    /*package*/ ElementContext(DictionaryService dictionary, QName elementName, ImporterProgress progress)
    {
        this.dictionary = dictionary;
        this.elementName = elementName;
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
    /*package*/ ImporterProgress getImporterProgress()
    {
        return progress;
    }
    
    /**
     * @return  the dictionary service
     */
    /*package*/ DictionaryService getDictionaryService()
    {
        return dictionary;
    }
}
