package org.alfresco.repo.policy;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;


/**
 * Behaviour binding to a Class (Type or Aspect) in the Content Model.
 * 
 * @author David Caruana
 *
 */
public class ClassBehaviourBinding implements BehaviourBinding
{
    // The dictionary service
    private DictionaryService dictionary;
    
    // The class reference
    private ClassRef classRef; 


    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classRef  the reference of the Class
     */
    /*package*/ ClassBehaviourBinding(DictionaryService dictionary, ClassRef classRef)
    {
        this.dictionary = dictionary;
        this.classRef = classRef;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourBinding#generaliseBinding()
     */
    public BehaviourBinding generaliseBinding()
    {
        BehaviourBinding generalisedBinding = null;
        ClassDefinition classDefinition = dictionary.getClass(classRef);
        ClassDefinition superClassDefinition = classDefinition.getSuperClass();
        if (superClassDefinition != null)
        {
            generalisedBinding = new ClassBehaviourBinding(dictionary, superClassDefinition.getReference());
        }
        return generalisedBinding;
    }
    
    /**
     * Gets the class reference
     * 
     * @return  the class reference
     */
    public ClassRef getClassRef()
    {
        return classRef;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ClassBehaviourBinding))
        {
            return false;
        }
        return classRef.equals(((ClassBehaviourBinding)obj).classRef);
    }

    @Override
    public int hashCode()
    {
        return classRef.hashCode();
    }

    @Override
    public String toString()
    {
        return "ClassBinding[class=" + classRef + "]";
    }
    
    
}
