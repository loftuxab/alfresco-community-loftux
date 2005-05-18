package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Class (Type/Aspect) oriented index of bound behaviours
 * 
 * Note: Uses Class hierarchy to derive bindings.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ClassBehaviourIndex implements BehaviourIndex<ClassBehaviourBinding>
{
    // Lock
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Map of class bindings  
    private BehaviourMap<ClassBehaviourBinding> classMap = new BehaviourMap<ClassBehaviourBinding>();
    
    // Map of service bindings
    private BehaviourMap<ServiceBehaviourBinding> serviceMap = new BehaviourMap<ServiceBehaviourBinding>();
    
    // List of registered observers
    private List<BehaviourChangeObserver<ClassBehaviourBinding>> observers = new ArrayList<BehaviourChangeObserver<ClassBehaviourBinding>>();


    /**
     * Construct.
     */
    /*package*/ ClassBehaviourIndex()
    {
        // Observe class binding changes and propagate to our own observers 
        this.classMap.addChangeObserver(new BehaviourChangeObserver<ClassBehaviourBinding>()
        {
            public void addition(ClassBehaviourBinding binding, Behaviour behaviour)
            {
                for (BehaviourChangeObserver<ClassBehaviourBinding> listener : observers)
                {
                    listener.addition(binding, behaviour);
                }
            }
        });

        // Observe service binding changes and propagate to our own observers
        this.serviceMap.addChangeObserver(new BehaviourChangeObserver<ServiceBehaviourBinding>()
        {
            public void addition(ServiceBehaviourBinding binding, Behaviour behaviour)
            {
                for (BehaviourChangeObserver<ClassBehaviourBinding> listener : observers)
                {
                    // Note: Don't specify class ref as service-level bindings affect all classes
                    listener.addition(null, behaviour);
                }
            }
        });
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourIndex#getAll()
     */
    public Collection<BehaviourDefinition> getAll()
    {
        lock.readLock().lock();
        
        try
        {
            List<BehaviourDefinition> all = new ArrayList<BehaviourDefinition>(classMap.size() + serviceMap.size());
            all.addAll(classMap.getAll());
            all.addAll(serviceMap.getAll());
            return all;
        }
        finally
        {
            lock.readLock().unlock();
        }
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourIndex#find()
     */
    public Collection<BehaviourDefinition> find(ClassBehaviourBinding binding)
    {
        lock.readLock().lock();
        
        try
        {
            // Find class behaviour by scanning up the class hierarchy
            BehaviourDefinition behaviour = null;
            while(behaviour == null && binding != null)
            {
                behaviour = classMap.get(binding);
                if (behaviour == null)
                {
                    binding = (ClassBehaviourBinding)binding.generaliseBinding();
                }
            }
    
            // Append all service-level behaviours
            List<BehaviourDefinition> behaviours = new ArrayList<BehaviourDefinition>();
            if (behaviour != null)
            {
                behaviours.add(behaviour);
            }
            behaviours.addAll(serviceMap.getAll());
            return behaviours;
        }
        finally
        {
            lock.readLock().unlock();
        }
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourIndex#find()
     */
    public void addChangeObserver(BehaviourChangeObserver<ClassBehaviourBinding> observer)
    {
        observers.add(observer);
    }


    /**
     * Binds a Class Behaviour into this index
     * 
     * @param behaviour  the class bound behaviour
     */
    public void putClassBehaviour(BehaviourDefinition<ClassBehaviourBinding> behaviour)
    {
        lock.writeLock().lock();
        try
        {
            classMap.put(behaviour);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    
    /**
     * Binds a Service Behaviour into this index
     * 
     * @param behaviour  the service bound behaviour
     */
    public void putServiceBehaviour(BehaviourDefinition<ServiceBehaviourBinding> behaviour)
    {
        lock.writeLock().lock();
        try
        {
            serviceMap.put(behaviour);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

}
