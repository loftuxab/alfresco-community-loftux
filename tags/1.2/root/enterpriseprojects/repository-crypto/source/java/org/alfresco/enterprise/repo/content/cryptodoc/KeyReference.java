package org.alfresco.enterprise.repo.content.cryptodoc;

import java.io.Serializable;


public class KeyReference implements Serializable 
{
	private static final long serialVersionUID = 5639082518038706193L;

	private String alias;
	private String password;

	public String getAlias()
	{
		return this.alias;
	}
	
	public void setAlias(String alias)
	{
		this.alias = alias;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public void setPassword(String password) 
	{
		this.password = password;
	}

	@Override
	public int hashCode()
	{
		return this.alias.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof KeyReference))
		{
			return false;
		}
		return this.alias.equals(((KeyReference)obj).alias);
	}
	
	@Override
	public String toString()
	{
		return "KeyReference [alias=" + alias + ", password=" + password + "]";
	}
}
