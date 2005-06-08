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
    // The feature qualified name (property or association)
    private QName featureQName;
    private QName activeFeatureQName;

    // Wild Card feature match (match all features)
    private static final QName ALL_FEATURES = QName.createQName("", "*");



    private ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classQName, QName featureQName, QName activeFeatureQName)
    {
        super(dictionary, classQName);
        this.featureQName = featureQName;
        this.activeFeatureQName = activeFeatureQName;
    }

    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classQName  the Class qualified name
     * @param featureQName  the Class feature (property or association) qualifed name
     */
    /*package*/ ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classQName, QName featureQName)
    {
        this(dictionary, classQName, featureQName, featureQName);
    }

    
    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classQName  the Class qualified name
     */
    /*package*/ ClassFeatureBehaviourBinding(DictionaryService dictionary, QName classQName)
    {
        this(dictionary, classQName, ALL_FEATURES);
    }
        
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourBinding#generaliseBinding()
     */
    public BehaviourBinding generaliseBinding()
    {
        BehaviourBinding generalisedBinding = null;
        ClassDefinition classDefinition = getDictionary().getClass(getClassQName());
        
        if (activeFeatureQName.equals(ALL_FEATURES))
        {
            QName parentClassName = classDefinition.getParentName();
            if (parentClassName != null)
            {
                generalisedBinding = new ClassFeatureBehaviourBinding(getDictionary(), parentClassName, featureQName, featureQName);
            }
        }
        else
        {
            generalisedBinding = new ClassFeatureBehaviourBinding(getDictionary(), getClassQName(), featureQName, ALL_FEATURES);
        }
        
        return generalisedBinding;
    }
    
    public QName getFeatureQName()
    {
        return activeFeatureQName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ClassFeatureBehaviourBinding))
        {
            return false;
        }
        return getClassQName().equals(((ClassFeatureBehaviourBinding)obj).getClassQName()) &&
               activeFeatureQName.equals(((ClassFeatureBehaviourBinding)obj).activeFeatureQName);
    }

    @Override
    public int hashCode()
    {
        return 37 * getClassQName().hashCode() + activeFeatureQName.hashCode();
    }

    @Override
    public String toString()
    {
        return "ClassFeatureBinding[class=" + getClassQName() + ";feature=" + activeFeatureQName + "]";
    }
    
}
