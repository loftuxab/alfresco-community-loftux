package org.alfresco.repo.policy;


/**
 * A Behaviour Binding represents the way in which a Behaviour is bound
 * to a Policy i.e. the key.
 * 
 * @author David Caruana
 *
 */
/*package*/ interface BehaviourBinding
{
    /**
     * Gets a generalised form of the Binding.
     * 
     * For example, if the binding key is hierarchical, return the parent
     * key.
     * 
     * @return  the generalised form (or null, if there isn't one)
     */
    BehaviourBinding generaliseBinding();
}
