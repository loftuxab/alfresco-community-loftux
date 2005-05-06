package org.alfresco.repo.dictionary;

import org.alfresco.repo.ref.QName;


/**
 * Class Reference
 * 
 * @author David Caruana
 */
public class ClassRef extends DictionaryRef
{
    private static final long serialVersionUID = 3763098565714457649L;

    
    /**
     * Construct Class Reference for Default Dictionary Store 
     * 
     * @param classQName  class qualified name
     */
    public ClassRef(QName classQName)
    {
        super(classQName);
    }
}
