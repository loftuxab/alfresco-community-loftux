package org.alfresco.deployment;

import java.io.Serializable;

public class TargetStatusImpl implements TargetStatus, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -451429708095640372L;
	private String targetName;
	private String storeName;
	
	private int currentVersion;
	
	public void setCurrentVersion(int currentVersion) 
	{
		this.currentVersion = currentVersion;
	}

	public int getCurrentVersion() 
	{
		return currentVersion;
	}

	public String getTargetName() 
	{
		return targetName;
	}
	
	public void setTargetName(String name)
	{
		this.targetName = name;	
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreName() {
		return storeName;
	}

}
