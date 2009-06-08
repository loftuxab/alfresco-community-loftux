package org.alfresco.deployment.impl.server;

import java.util.Map;

import org.alfresco.deployment.DeploymentTarget;

public interface DeploymentTargetRegistry 
{
	/**
	 * Register a new deployment target.  If an entry with the old name 
	 * already exists then the new value replaces the old value. 
	 * @param name the name of the target
	 * @param target the implementation of the target
	 */
	public void registerTarget(String name, DeploymentTarget target);

	
	/**
	 * Unregister a deployment target
	 * @param name the name of the target
	 */
	public void unregisterTarget(String name);

	
	/**
	 * Get the targets for this deployment engine.
	 * @return the targets for this deployment engine
	 */
	public Map<String, DeploymentTarget> getTargets(); 



}
