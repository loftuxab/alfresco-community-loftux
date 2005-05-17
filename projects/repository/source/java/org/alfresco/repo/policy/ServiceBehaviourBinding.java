package org.alfresco.repo.policy;

public class ServiceBehaviourBinding implements BehaviourBinding
{

    private Object service;
    
    /*package*/ ServiceBehaviourBinding(Object service)
    {
        this.service = service;
    }
    
    public BehaviourBinding generaliseBinding()
    {
        return null;
    }

    public Object getService()
    {
        return service;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ServiceBehaviourBinding))
        {
            return false;
        }
        return service.equals(((ServiceBehaviourBinding)obj).service);
    }

    @Override
    public int hashCode()
    {
        return service.hashCode();
    }

}
