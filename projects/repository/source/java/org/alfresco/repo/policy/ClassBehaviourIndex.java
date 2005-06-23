/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
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
/*package*/ class ClassBehaviourIndex<B extends ClassBehaviourBinding> implements BehaviourIndex<B>
{
    // Lock
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Map of class bindings  
    private BehaviourMap<B> classMap = new BehaviourMap<B>();
    
    // Map of service bindings
    private BehaviourMap<ServiceBehaviourBinding> serviceMap = new BehaviourMap<ServiceBehaviourBinding>();
    
    // List of registered observers
    private List<BehaviourChangeObserver<B>> observers = new ArrayList<BehaviourChangeObserver<B>>();


    /**
     * Construct.
     */
    /*package*/ ClassBehaviourIndex()
    {
        // Observe class binding changes and propagate to our own observers 
        this.classMap.addChangeObserver(new BehaviourChangeObserver<B>()
        {
            public void addition(B binding, Behaviour behaviour)
            {
                for (BehaviourChangeObserver<B> listener : observers)
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
                for (BehaviourChangeObserver<B> listener : observers)
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
    public Collection<BehaviourDefinition> find(B binding)
    {
        lock.readLock().lock();
        
        try
        {
            List<BehaviourDefinition> behaviours = new ArrayList<BehaviourDefinition>();

            // Find class behaviour by scanning up the class hierarchy
            BehaviourDefinition behaviour = null;
            while(behaviour == null && binding != null)
            {
                behaviour = classMap.get(binding);
                if (behaviour == null)
                {
                    binding = (B)binding.generaliseBinding();
                }
            }
            if (behaviour != null)
            {
                behaviours.add(behaviour);
            }
            
            // Append all service-level behaviours
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
    public void addChangeObserver(BehaviourChangeObserver<B> observer)
    {
        observers.add(observer);
    }


    /**
     * Binds a Class Behaviour into this index
     * 
     * @param behaviour  the class bound behaviour
     */
    public void putClassBehaviour(BehaviourDefinition<B> behaviour)
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
