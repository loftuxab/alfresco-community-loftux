package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;


/*package*/ class ClassBehaviourIndex<P extends ClassPolicy> implements BehaviourIndex<ClassRef, P>
{

    private DictionaryService dictionary;
    private BehaviourMap<ClassRef, P> classMap = new BehaviourMap<ClassRef, P>();
    private BehaviourMap<Object, P> serviceMap = new BehaviourMap<Object, P>();    
    private List<BehaviourChangeListener<ClassRef, P>> listeners = new ArrayList<BehaviourChangeListener<ClassRef, P>>();

    
    /*package*/ ClassBehaviourIndex(DictionaryService dictionary)
    {
        this.dictionary = dictionary;

        this.classMap.addChangeListener(new BehaviourChangeListener<ClassRef, P>()
        {
            public void addition(ClassRef binding, Behaviour<? extends P> behaviour)
            {
                for (BehaviourChangeListener<ClassRef, P> listener : listeners)
                {
                    listener.addition(binding, behaviour);
                }
            }
        });

        this.serviceMap.addChangeListener(new BehaviourChangeListener<Object, P>()
        {
            public void addition(Object binding, Behaviour<? extends P> behaviour)
            {
                for (BehaviourChangeListener<ClassRef, P> listener : listeners)
                {
                    listener.addition(null, behaviour);
                }
            }
        });
    }

    
    public Collection<BehaviourDefinition<? extends Object, ? extends P>> getAll()
    {
        List<BehaviourDefinition<? extends Object, ? extends P>> all = new ArrayList<BehaviourDefinition<? extends Object, ? extends P>>(classMap.size() + serviceMap.size());
        all.addAll(classMap.getAll());
        all.addAll(serviceMap.getAll());
        return all;
    }
    

    public Collection<BehaviourDefinition<? extends Object, ? extends P>> find(ClassRef key)
    {
        // Find class behaviour by scanning up the class hierarchy
        BehaviourDefinition<ClassRef, ? extends P> behaviour = null;        
        ClassDefinition classDefinition = dictionary.getClass(key);
        while(classDefinition != null)
        {
            behaviour = classMap.get(classDefinition.getReference());
            if (behaviour != null)
            {
                break;
            }
            classDefinition = classDefinition.getSuperClass();
        }

        // Build complete list of behaviours (class and service level)
        List<BehaviourDefinition<? extends Object, ? extends P>> behaviours = new ArrayList<BehaviourDefinition<? extends Object, ? extends P>>();
        if (behaviour != null)
        {
            behaviours.add(behaviour);
        }
        behaviours.addAll(serviceMap.getAll());
        return behaviours;
    }


    public void addChangeListener(BehaviourChangeListener<ClassRef, P> listener)
    {
        listeners.add(listener);
    }

    
    public void putClassBehaviour(BehaviourDefinition<ClassRef, ? extends P> behaviour)
    {
        ClassRef classRef = behaviour.getBinding();
        ClassDefinition definition = dictionary.getClass(classRef);
        if (definition == null)
        {
            throw new IllegalArgumentException("Cannot bind to class " + classRef + " as it is not defined");
        }
        classMap.put(classRef, behaviour);
    }

    
    public void putServiceBehaviour(BehaviourDefinition<Object, ? extends P> behaviour)
    {
        serviceMap.put(behaviour.getBinding(), behaviour);
    }


    
}
