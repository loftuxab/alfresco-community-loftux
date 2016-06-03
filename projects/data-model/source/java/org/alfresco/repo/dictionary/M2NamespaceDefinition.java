package org.alfresco.repo.dictionary;

import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.NamespaceDefinition;


/**
 * Namespace Definition.
 * 
 *
 */
public class M2NamespaceDefinition implements NamespaceDefinition
{
    ModelDefinition model = null;
    private String uri = null;
    private String prefix = null;
   
    
    /*package*/ M2NamespaceDefinition(ModelDefinition model, String uri, String prefix)
    {
        this.model = model;
        this.uri = uri;
        this.prefix = prefix;
    }

    public ModelDefinition getModel()
    {
        return model;
    }
    
    public String getUri()
    {
        return uri;
    }

    public String getPrefix()
    {
        return prefix;
    }
}
