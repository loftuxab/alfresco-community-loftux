package org.alfresco.enterprise.repo.sync.connector.impl;

/**
 * Property based key - does not change
 * @author mrogers
 */
public class KeyProviderProperty extends KeyProvider
{
    private String key ="123456";

    @Override
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
     
}
