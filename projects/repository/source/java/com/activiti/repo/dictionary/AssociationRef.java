package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;

public class AssociationRef extends DictionaryRef
{

    private static final long serialVersionUID = 3763099643869149496L;

    private ClassRef classRef;

    
    public AssociationRef(ClassRef classRef, String associationName)
    {
        super(QName.createQName(classRef.getQName().getNamespaceURI(), classRef.getQName().getLocalName() + NAME_SEPARATOR + associationName));
        this.classRef = classRef;
    }

    
    public ClassRef getClassRef()
    {
        return classRef;
    }

}
