package com.activiti.repo.domain;

import java.io.Serializable;

import com.activiti.util.EqualsHelper;

/**
 * Compound key for persistence of {@link com.activiti.repo.domain.Node}
 * 
 * @author derekh
 */
public class NodeKey implements Serializable
{
    private static final long serialVersionUID = 3258695403221300023L;
    
    private String guid;
    private String protocol;
	private String identifier;

    public NodeKey()
    {
    }
	
	public NodeKey(StoreKey storeKey, String guid)
	{
		setGuid(guid);
		setProtocol(storeKey.getProtocol());
		setIdentifier(storeKey.getIdentifier());
	}
	
	public NodeKey(String protocol, String identifier, String guid)
	{
		this();
		setGuid(guid);
		setProtocol(protocol);
		setIdentifier(identifier);
	}
	
	public String toString()
	{
		return ("NodeKey[" +
				" id=" + guid +
				", protocol=" + protocol +
				", identifier=" + identifier +
				"]");
	}
    
    public int hashCode()
    {
        return (this.protocol.hashCode() + this.identifier.hashCode() + this.guid.hashCode());
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (!(obj instanceof NodeKey))
        {
            return false;
        }
        NodeKey that = (NodeKey) obj;
        return (EqualsHelper.nullSafeEquals(this.protocol, that.protocol) &&
                EqualsHelper.nullSafeEquals(this.identifier, that.identifier) &&
                EqualsHelper.nullSafeEquals(this.guid, that.guid));
    }
    
    public String getGuid()
    {
        return guid;
    }
    
    public void setGuid(String id)
    {
        this.guid = id;
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
