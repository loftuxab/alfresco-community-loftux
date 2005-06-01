package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
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
     * @param classRef  the class reference
     * @return  the policy
     */
    public P get(QName classRef)
    {
        ClassDefinition classDefinition = dictionary.getClass(classRef);
        if (classDefinition == null)
        {
            throw new IllegalArgumentException("Class " + classRef + " has not been defined in the data dictionary");
        }
        return factory.create(new ClassBehaviourBinding(dictionary, classRef));
    }

    
    /**
     * Gets the collection of Policy implementations for the specified Class
     * 
     * @param classRef  the class reference
     * @return  the collection of policies
     */
    public Collection<P> getList(QName classRef)
    {
        ClassDefinition classDefinition = dictionary.getClass(classRef);
        if (classDefinition == null)
        {
            throw new IllegalArgumentException("Class " + classRef + " has not been defined in the data dictionary");
        }
        return factory.createList(new ClassBehaviourBinding(dictionary, classRef));
    }

    
    /**
     * Gets the Policy implementation for the specified Node
     * 
     * All behaviours bound to the Node's class and aspects are aggregated.
     * 
     * @param nodeRef the node reference
     * @return the collection of policies
     */
    public P get(NodeService nodeService, NodeRef nodeRef)
    {
        return factory.toPolicy(getList(nodeService, nodeRef));
    }


    /**
     * Gets the collection of Policy implementations for the specified Node
     * 
     * All behaviours bound to the Node's class and aspects are returned.
     * 
     * @param nodeRef  the node reference
     * @return the collection of policies
     */
	public Collection<P> getList(NodeService nodeService, NodeRef nodeRef)
	{
		Collection<P> result = new ArrayList<P>();
		
		// Get the behaviour for the node's type
		QName classRef = nodeService.getType(nodeRef);
		result.addAll(getList(classRef));
		
		// Get the behaviour for all the aspect types
		Collection<QName> aspects = nodeService.getAspects(nodeRef);
		for (QName aspect : aspects) 
		{
			result.addAll(getList(aspect));
		}
		
		return result;
	}
    
}
