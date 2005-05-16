package org.alfresco.repo.policy;


public interface Behaviour
{

    public <T> T getInterface(Class<T> policy);
    
    public String getDescription();
    
}
