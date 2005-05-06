package org.alfresco.repo.dictionary;

import org.alfresco.repo.ref.QName;

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
    
    private String associationName;

    /**
     * Construct Association Reference
     * 
     * @param classRef  owning class reference
     * @param associationName  association name - this is not the fully qualified name as
     *      the association is qualified by its defining class/aspect
     */
    public AssociationRef(ClassRef classRef, String associationName)
    {
        super(QName.createQName(classRef.getQName().getNamespaceURI(), associationName));
        this.classRef = classRef;
        this.associationName = associationName;
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

    /**
     * @return Returns the simple association name, i.e. not the fully qualified name
     * 
     * @see DictionaryRef#getQName()
     */
    public String getAssociationName()
    {
        return associationName;
    }
}
