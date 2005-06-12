package org.alfresco.repo.policy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.ref.QName;

/**
 * Delegate for a Class-level Policy.  Provides access to Policy Interface
 * implementations which invoke the appropriate bound behaviours.
 *  
 * @author David Caruana
 *
 * @param <P>  the policy interface
 */
public class ClassPolicyDelegate<P extends ClassPolicy>
{
    private DictionaryService dictionary;
    private CachedPolicyFactory<ClassBehaviourBinding, P> factory;


    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param policyClass  the policy interface class
     * @param index  the behaviour index to query against
     */
    /*package*/ ClassPolicyDelegate(DictionaryService dictionary, Class<P> policyClass, BehaviourIndex<ClassBehaviourBinding> index)
    {
        // Get list of all pre-registered behaviours for the policy and
        // ensure they are valid.
        Collection<BehaviourDefinition> definitions = index.getAll();
        for (BehaviourDefinition definition : definitions)
        {
            definition.getBehaviour().getInterface(policyClass);
        }

        // Rely on cached implementation of policy factory
        // Note: Could also use PolicyFactory (without caching)
        this.factory = new CachedPolicyFactory<ClassBehaviourBinding, P>(policyClass, index);
        this.dictionary = dictionary;
    }
    

    /**
     * Gets the Policy implementation for the specified Class
     * 
     * When multiple behaviours are bound to the policy for the class, an
     * aggregate policy implementation is returned which invokes each policy
     * in turn.
     * 
     * @param classQName  the class qualified name
     * @return  the policy
     */
    public P get(QName classQName)
    {
        ClassDefinition classDefinition = dictionary.getClass(classQName);
        if (classDefinition == null)
        {
            throw new IllegalArgumentException("Class " + classQName + " has not been defined in the data dictionary");
        }
        return factory.create(new ClassBehaviourBinding(dictionary, classQName));
    }

    
    /**
     * Gets the collection of Policy implementations for the specified Class
     * 
     * @param classQName  the class qualified name
     * @return  the collection of policies
     */
    public Collection<P> getList(QName classQName)
    {
        ClassDefinition classDefinition = dictionary.getClass(classQName);
        if (classDefinition == null)
        {
            throw new IllegalArgumentException("Class " + classQName + " has not been defined in the data dictionary");
        }
        return factory.createList(new ClassBehaviourBinding(dictionary, classQName));
    }
    
    /**
     * Gets the policy implementation for the given classes.  The single <tt>Policy</tt>
     * will be a wrapper of multiple appropriate policies.
     * 
     * @param classQNames the class qualified names
     * @return Returns the policy
     */
    public P get(Set<QName> classQNames)
    {
        return factory.toPolicy(getList(classQNames));
    }

    /**
     * Gets the collection of <tt>Policy</tt> implementations for the given classes
     * 
     * @param classQNames the class qualified names
     * @return Returns the collection of policies
     */
    public Collection<P> getList(Set<QName> classQNames)
    {
        Collection<P> policies = new HashSet<P>();
        for (QName classQName : classQNames)
        {
            P policy = factory.create(new ClassBehaviourBinding(dictionary, classQName));
            policies.add(policy);
        }
        return policies;
    }
}
