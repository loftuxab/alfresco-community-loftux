package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;

/*package*/ interface ModelQuery
{

    public PropertyTypeDefinition getPropertyType(QName name);

    public TypeDefinition getType(QName name);
    
    public AspectDefinition getAspect(QName name);
    
    public ClassDefinition getClass(QName name);
    
    public PropertyDefinition getProperty(QName name);
}
