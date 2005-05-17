package org.alfresco.repo.policy;


import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;

public class ClassBehaviourBinding implements BehaviourBinding
{
    private DictionaryService dictionary;
    private ClassRef classRef; 


    /*package*/ ClassBehaviourBinding(DictionaryService dictionary, ClassRef classRef)
    {
        this.dictionary = dictionary;
        this.classRef = classRef;
    }
    
    
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
    
    
    
    
}
