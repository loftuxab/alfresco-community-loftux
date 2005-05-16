package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;


/*package*/ class ClassBehaviourIndex implements BehaviourIndex<ClassRef>
{

    private DictionaryService dictionary;
    private BehaviourMap<ClassRef> classMap = new BehaviourMap<ClassRef>();
    private BehaviourMap<Object> serviceMap = new BehaviourMap<Object>();    
    private List<BehaviourChangeListener<ClassRef>> listeners = new ArrayList<BehaviourChangeListener<ClassRef>>();

    
    /*package*/ ClassBehaviourIndex(DictionaryService dictionary)
    {
        this.dictionary = dictionary;

        this.classMap.addChangeListener(new BehaviourChangeListener<ClassRef>()
        {
            public void addition(ClassRef binding, Behaviour behaviour)
            {
                for (BehaviourChangeListener<ClassRef> listener : listeners)
                {
                    listener.addition(binding, behaviour);
                }
            }
        });

        this.serviceMap.addChangeListener(new BehaviourChangeListener<Object>()
        {
            public void addition(Object binding, Behaviour behaviour)
            {
                for (BehaviourChangeListener<ClassRef> listener : listeners)
                {
                    listener.addition(null, behaviour);
                }
            }
        });
    }

    
    public Collection<BehaviourDefinition<? extends Object>> getAll()
    {
        List<BehaviourDefinition<? extends Object>> all = new ArrayList<BehaviourDefinition<? extends Object>>(classMap.size() + serviceMap.size());
        all.addAll(classMap.getAll());
        all.addAll(serviceMap.getAll());
        return all;
    }
    

    public Collection<BehaviourDefinition<? extends Object>> find(ClassRef key)
    {
        // Find class behaviour by scanning up the class hierarchy
        BehaviourDefinition<ClassRef> behaviour = null;        
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
        List<BehaviourDefinition<? extends Object>> behaviours = new ArrayList<BehaviourDefinition<? extends Object>>();
        if (behaviour != null)
        {
            behaviours.add(behaviour);
        }
        behaviours.addAll(serviceMap.getAll());
        return behaviours;
    }


    public void addChangeListener(BehaviourChangeListener<ClassRef> listener)
    {
        listeners.add(listener);
    }

    
    public void putClassBehaviour(BehaviourDefinition<ClassRef> behaviour)
    {
        ClassRef classRef = behaviour.getBinding();
        ClassDefinition definition = dictionary.getClass(classRef);
        if (definition == null)
        {
            throw new IllegalArgumentException("Cannot bind to class " + classRef + " as it is not defined");
        }
        classMap.put(classRef, behaviour);
    }

    
    public void putServiceBehaviour(BehaviourDefinition<Object> behaviour)
    {
        serviceMap.put(behaviour.getBinding(), behaviour);
    }


    
}
