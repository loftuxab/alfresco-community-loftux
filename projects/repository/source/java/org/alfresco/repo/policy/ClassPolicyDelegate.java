package org.alfresco.repo.policy;

import java.util.Collection;

import org.alfresco.repo.dictionary.ClassRef;

public class ClassPolicyDelegate<P extends ClassPolicy>
{

    private PolicyDelegateCache<ClassRef, ClassPolicy, P> delegateCache;


    /*package*/ ClassPolicyDelegate(Class policyClass, BehaviourIndex<ClassRef, ClassPolicy> query)
    {
        delegateCache = new PolicyDelegateCache<ClassRef, ClassPolicy, P>(policyClass, query);
        
        // TODO: Get list of all registered behaviours for policy
        //       thus testing pre-registered behaviours - add getList(policy) to delegate cache
    }
    

    public P get(ClassRef classRef)
    {
        return delegateCache.get(classRef);
    }

    public Collection<P> getList(ClassRef classRef)
    {
        return delegateCache.getList(classRef);
    }
    
}
