package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;


/**
 * Association Reference.
 * 
 * @author David Caruana
 */
public class AssociationRef extends DictionaryRef
{

    private static final long serialVersionUID = 3763099643869149496L;

    /**
     * Owning Class Reference
     */
    private ClassRef classRef;

    
    /**
     * Construct Association Reference
     * 
     * @param classRef  owning class reference
     * @param associationName  association name
     */
    public AssociationRef(ClassRef classRef, String associationName)
    {
        super(QName.createQName(classRef.getQName().getNamespaceURI(), classRef.getQName().getLocalName() + NAME_SEPARATOR + associationName));
        this.classRef = classRef;
    }


    /**
     * Gets the owning Class Reference
     * 
     * @return  class reference
     */
    public ClassRef getClassRef()
    {
        return classRef;
    }

}
