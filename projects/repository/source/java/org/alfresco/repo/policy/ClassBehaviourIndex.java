package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*package*/ class ClassBehaviourIndex implements BehaviourIndex<ClassBehaviourBinding>
{

    private BehaviourMap<ClassBehaviourBinding> classMap = new BehaviourMap<ClassBehaviourBinding>();
    private BehaviourMap<ServiceBehaviourBinding> serviceMap = new BehaviourMap<ServiceBehaviourBinding>();    
    private List<BehaviourChangeListener<ClassBehaviourBinding>> listeners = new ArrayList<BehaviourChangeListener<ClassBehaviourBinding>>();

    
    /*package*/ ClassBehaviourIndex()
    {
        this.classMap.addChangeListener(new BehaviourChangeListener<ClassBehaviourBinding>()
        {
            public void addition(ClassBehaviourBinding binding, Behaviour behaviour)
            {
                for (BehaviourChangeListener<ClassBehaviourBinding> listener : listeners)
                {
                    listener.addition(binding, behaviour);
                }
            }
        });

        this.serviceMap.addChangeListener(new BehaviourChangeListener<ServiceBehaviourBinding>()
        {
            public void addition(ServiceBehaviourBinding binding, Behaviour behaviour)
            {
                for (BehaviourChangeListener<ClassBehaviourBinding> listener : listeners)
                {
                    listener.addition(null, behaviour);
                }
            }
        });
    }

    
    public Collection<BehaviourDefinition> getAll()
    {
        List<BehaviourDefinition> all = new ArrayList<BehaviourDefinition>(classMap.size() + serviceMap.size());
        all.addAll(classMap.getAll());
        all.addAll(serviceMap.getAll());
        return all;
    }
    

    public Collection<BehaviourDefinition> find(ClassBehaviourBinding binding)
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

        // Build complete list of behaviours (class and service level)
        List<BehaviourDefinition> behaviours = new ArrayList<BehaviourDefinition>();
        if (behaviour != null)
        {
            behaviours.add(behaviour);
        }
        behaviours.addAll(serviceMap.getAll());
        return behaviours;
    }


    public void addChangeListener(BehaviourChangeListener<ClassBehaviourBinding> listener)
    {
        listeners.add(listener);
    }

    
    public void putClassBehaviour(BehaviourDefinition<ClassBehaviourBinding> behaviour)
    {
        classMap.put(behaviour);
    }

    
    public void putServiceBehaviour(BehaviourDefinition<ServiceBehaviourBinding> behaviour)
    {
        serviceMap.put(behaviour);
    }


    
}
