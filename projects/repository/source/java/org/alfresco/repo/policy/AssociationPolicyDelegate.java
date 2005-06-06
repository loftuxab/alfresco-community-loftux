package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;


/**
 * Delegate for a Class Feature-level (Property and Association) Policies.  Provides 
 * access to Policy Interface implementations which invoke the appropriate bound behaviours.
 *  
 * @author David Caruana
 *
 * @param <P>  the policy interface
 */
public class AssociationPolicyDelegate<P extends AssociationPolicy>
{
    private DictionaryService dictionary;
    private CachedPolicyFactory<ClassFeatureBehaviourBinding, P> factory;


    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param policyClass  the policy interface class
     * @param index  the behaviour index to query against
     */
    AssociationPolicyDelegate(DictionaryService dictionary, Class<P> policyClass, BehaviourIndex<ClassFeatureBehaviourBinding> index)
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
        this.factory = new CachedPolicyFactory<ClassFeatureBehaviourBinding, P>(policyClass, index);
        this.dictionary = dictionary;
    }
    

    /**
     * Gets the Policy implementation for the specified Class and Propery
     * 
     * When multiple behaviours are bound to the policy for the class feature, an
     * aggregate policy implementation is returned which invokes each policy
     * in turn.
     * 
     * @param classRef  the class reference
     * @param assocRef  the association reference
     * @return  the policy
     */
    public P get(QName classRef, QName assocRef)
    {
        AssociationDefinition assocDef = dictionary.getAssociation(assocRef);
        if (assocDef == null)
        {
            throw new IllegalArgumentException("Association" + assocDef + " has not been defined in the data dictionary");
        }
        return factory.create(new ClassFeatureBehaviourBinding(dictionary, classRef, assocRef));
    }

    
    /**
     * Gets the collection of Policy implementations for the specified Class and Property
     * 
     * @param classRef  the class reference
     * @param assocRef  the association reference
     * @return  the collection of policies
     */
    public Collection<P> getList(QName classRef, QName assocRef)
    {
        AssociationDefinition assocDef = dictionary.getAssociation(assocRef);
        if (assocDef == null)
        {
            throw new IllegalArgumentException("Association" + assocDef + " has not been defined in the data dictionary");
        }
        return factory.createList(new ClassFeatureBehaviourBinding(dictionary, classRef, assocRef));
    }

    
    /**
     * Gets the Policy implementation for the specified Node and Property
     * 
     * All behaviours bound to the Node's class and aspects are aggregated.
     * 
     * @param nodeRef the node reference
     * @param assocRef  the property reference
     * @return the collection of policies
     */
    public P get(NodeService nodeService, NodeRef nodeRef, QName assocRef)
    {
        return factory.toPolicy(getList(nodeService, nodeRef, assocRef));
    }


    /**
     * Gets the collection of Policy implementations for the specified Node and Property
     * 
     * All behaviours bound to the Node's class and aspects are returned.
     * 
     * @param nodeRef  the node reference
     * @param assocRef  the association reference
     * @return the collection of policies
     */
	public Collection<P> getList(NodeService nodeService, NodeRef nodeRef, QName assocRef)
	{
		Collection<P> result = new ArrayList<P>();
		
		// Get the behaviour for the node's type
		QName classRef = nodeService.getType(nodeRef);
		result.addAll(getList(classRef, assocRef));
		
		// Get the behaviour for all the aspect types
		Collection<QName> aspects = nodeService.getAspects(nodeRef);
		for (QName aspect : aspects) 
		{
			result.addAll(getList(aspect, assocRef));
		}
		
		return result;
	}
    
    
}
