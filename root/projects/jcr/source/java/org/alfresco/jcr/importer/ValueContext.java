package org.alfresco.jcr.importer;

import org.alfresco.repo.importer.view.ElementContext;
import org.alfresco.service.namespace.QName;

public class ValueContext extends ElementContext
{

    private PropertyContext property;
    
    public ValueContext(QName elementName, PropertyContext property)
    {
        super(elementName, property.getDictionaryService(), property.getImporter());
        this.property = property;
    }

    public PropertyContext getProperty()
    {
        return property;
    }
    
}
