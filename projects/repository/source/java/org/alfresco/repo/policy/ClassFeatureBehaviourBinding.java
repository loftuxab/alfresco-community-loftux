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
/*package*/ class ClassFeatureBehaviourBinding extends ClassBehaviourBinding
{
    // The feature reference (property or association)
    private QName featureRef;
    private QName activeFeatureRef;

    // Wild Card feature match (match all features)
    private static final QName ALL_FEATURES = QName.createQName("", "*");



    private ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classRef, QName featureRef, QName activeFeatureRef)
    {
        super(dictionary, classRef);
        this.featureRef = featureRef;
        this.activeFeatureRef = activeFeatureRef;
    }

    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classRef  the reference of the Class
     * @param featureRef  the reference of the Class feature (property or association)
     */
    /*package*/ ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classRef, QName featureRef)
    {
        this(dictionary, classRef, featureRef, featureRef);
    }

    
    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classRef  the reference of the Class
     */
    /*package*/ ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classRef)
    {
        this(dictionary, classRef, ALL_FEATURES);
    }
        
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourBinding#generaliseBinding()
     */
    public BehaviourBinding generaliseBinding()
    {
        BehaviourBinding generalisedBinding = null;
        ClassDefinition classDefinition = getDictionary().getClass(getClassRef());
        
        if (activeFeatureRef.equals(ALL_FEATURES))
        {
            QName parentClassName = classDefinition.getParentName();
            if (parentClassName != null)
            {
                generalisedBinding = new ClassFeatureBehaviourBinding(getDictionary(), parentClassName, featureRef, featureRef);
            }
        }
        else
        {
            generalisedBinding = new ClassFeatureBehaviourBinding(getDictionary(), getClassRef(), featureRef, ALL_FEATURES);
        }
        
        return generalisedBinding;
    }
    
    public QName getFeatureRef()
    {
        return activeFeatureRef;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ClassFeatureBehaviourBinding))
        {
            return false;
        }
        return getClassRef().equals(((ClassFeatureBehaviourBinding)obj).getClassRef()) &&
               activeFeatureRef.equals(((ClassFeatureBehaviourBinding)obj).activeFeatureRef);
    }

    @Override
    public int hashCode()
    {
        return 37 * getClassRef().hashCode() + activeFeatureRef.hashCode();
    }

    @Override
    public String toString()
    {
        return "ClassFeatureBinding[class=" + getClassRef() + ";feature=" + activeFeatureRef + "]";
    }
    
}
