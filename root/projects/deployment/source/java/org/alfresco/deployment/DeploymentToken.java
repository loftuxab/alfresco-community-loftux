package org.alfresco.deployment;

import java.io.Serializable;
/**
 * Information about a new deployment.
 * @author mrogers
 *
 */
public interface DeploymentToken
{

	/**
	 * Get the deployment token
	 * @return
	 */
	public String getTicket();


     /**
      * Get the information about the target
      */
	public TargetStatus getTargetStatus();

	
}
