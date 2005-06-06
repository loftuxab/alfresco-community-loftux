package org.alfresco.repo.policy;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.ref.QName;


/**
 * Behaviour binding to a Class (Type or Aspect) in the Content Model.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ClassBehaviourBinding implements BehaviourBinding
{
    // The dictionary service
    private DictionaryService dictionary;
    
    // The class reference
    private QName classRef; 


    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classRef  the reference of the Class
     */
    /*package*/ ClassBehaviourBinding(DictionaryService dictionary, QName classRef)
    {
        this.dictionary = dictionary;
        this.classRef = classRef;
    }

    /*package*/ DictionaryService getDictionary()
    {
        return dictionary;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourBinding#generaliseBinding()
     */
    public BehaviourBinding generaliseBinding()
    {
        BehaviourBinding generalisedBinding = null;
        ClassDefinition classDefinition = dictionary.getClass(classRef);
        QName parentClassName = classDefinition.getParentName();
        if (parentClassName != null)
        {
            generalisedBinding = new ClassBehaviourBinding(dictionary, parentClassName);
        }
        return generalisedBinding;
    }
    
    /**
     * Gets the class reference
     * 
     * @return  the class reference
     */
    public QName getClassRef()
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
