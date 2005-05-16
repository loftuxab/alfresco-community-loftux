package org.alfresco.repo.policy;


public interface Behaviour<P extends Policy>
{

    public <T extends P> T getInterface(Class<T> policy);
    
    public String getDescription();
    
}
