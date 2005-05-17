package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;


public class ClassPolicyDelegate<P extends ClassPolicy>
{

    private NodeService nodeService;
    private DictionaryService dictionary;
    private CachedPolicyFactory<ClassBehaviourBinding, P> factory;


    /*package*/ ClassPolicyDelegate(NodeService nodeService, DictionaryService dictionary, Class<P> policyClass, BehaviourIndex<ClassBehaviourBinding> index)
    {
        this.nodeService = nodeService;
        this.dictionary = dictionary;
        this.factory = new CachedPolicyFactory<ClassBehaviourBinding, P>(policyClass, index);
        
        // TODO: Get list of all registered behaviours for policy
        //       thus testing pre-registered behaviours - add getList(policy) to delegate cache
    }
    

    public P get(ClassRef classRef)
    {
        return factory.create(new ClassBehaviourBinding(dictionary, classRef));
    }

    public Collection<P> getList(ClassRef classRef)
    {
        return factory.createList(new ClassBehaviourBinding(dictionary, classRef));
    }
	
	/**
	 * Helper to get the collection of policies that relate to the type and aspects
	 * associtated with the passed node reference
	 * 
	 * @param nodeService  the node service
	 * @param nodeRef	   the node reference
	 * @return			   a collection of the policy behaviours
	 */
	public Collection<P> getList(NodeRef nodeRef)
	{
		Collection<P> result = new ArrayList<P>();
		
		// Get the behaviour for the node's type
		ClassRef classRef = nodeService.getType(nodeRef);
		result.addAll(getList(classRef));
		
		// Get the behaviour for all the aspect types
		Collection<ClassRef> aspects = nodeService.getAspects(nodeRef);
		for (ClassRef aspect : aspects) 
		{
			result.addAll(getList(aspect));
		}
		
		return result;
	}
    
    
    public P get(NodeRef nodeRef)
    {
        return factory.toPolicy(getList(nodeRef));
    }
    
    
}
