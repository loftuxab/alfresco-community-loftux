package org.alfresco.deployment;

import java.io.Serializable;
/**
 * Information about a new deployment.
 * @author mrogers
 *
 */
public class DeploymentTokenImpl implements Serializable, DeploymentToken
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6013271259272921382L;

	private String ticket;
	
	private TargetStatus targetStatus;
	
	public void setTicket(String ticket) 
	{
		this.ticket = ticket;
	}

	public String getTicket() 
	{
		return ticket;
	}

	public TargetStatus getTargetStatus()
	{
		return targetStatus;
		
	}
	
	public void setTargetStatus(TargetStatus info)
	{
		this.targetStatus = info;
	}
}
