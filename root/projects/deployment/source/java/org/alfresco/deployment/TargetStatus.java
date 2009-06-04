package org.alfresco.deployment;

/**
 * Information about a deployment target
 * @author mrogers
 *
 */
public interface TargetStatus 
{
	public String getTargetName();
	
	public String getStoreName();
	
	public int getCurrentVersion() ;

}
