package com.activiti.repo.domain;

import java.io.Serializable;

import com.activiti.util.EqualsHelper;

/**
 * Compound key for persistence of {@link com.activiti.repo.domain.Store}
 * 
 * @author Derek Hulley
 */
public class StoreKey implements Serializable
{
    private static final long serialVersionUID = 3618140052220096569L;

    private String protocol;
    private String identifier;
	
	public StoreKey()
	{
	}
	
	public StoreKey(String protocol, String identifier)
	{
		setProtocol(protocol);
		setIdentifier(identifier);
	}
	
	public String toString()
	{
		return ("StoreKey[" +
				" protocol=" + protocol +
				", identifier=" + identifier +
				"]");
	}
    
    public int hashCode()
    {
        return (this.protocol.hashCode() + this.identifier.hashCode());
    }
	
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (!(obj instanceof StoreKey))
		{
			return false;
		}
		StoreKey that = (StoreKey) obj;
		return (EqualsHelper.nullSafeEquals(this.protocol, that.protocol) &&
                EqualsHelper.nullSafeEquals(this.identifier, that.identifier));
	}
    
    public String getProtocol()
    {
        return protocol;
    }
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getIdentifier()
    {
        return identifier;
    }
    
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
}
