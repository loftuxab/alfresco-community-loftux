package org.alfresco.enterprise.repo.sync.connector.impl;

import java.util.HashSet;
import java.util.Set;

public abstract class KeyProvider
{
    private Set<KeyChangeHandler> callbacks = new HashSet<KeyChangeHandler>(); 

    public void addListener(KeyChangeHandler handler)
    {
        callbacks.add(handler);
    }
    
    public void removeListener(KeyChangeHandler handler)
    {
        callbacks.remove(handler);
    }
    
    public interface KeyChangeHandler 
    {
        /**
         * Notification of a key change.
         */
        void onChangeKey(String newKey);
    }
    
    public abstract String getKey(); 
    
    protected void notifyCallbacks()
    {
        for(KeyChangeHandler handler : callbacks)
        {
            handler.onChangeKey(getKey());
        }
    }
    
}
