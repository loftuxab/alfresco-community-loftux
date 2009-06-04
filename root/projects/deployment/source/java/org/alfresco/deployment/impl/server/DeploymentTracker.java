package org.alfresco.deployment.impl.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * Class to track a deployment.
 * 
 * Contains a target
 * 
 * And a set of outputTokens.
 * 
 * @author mrogers
 *
 */
public class DeploymentTracker
{
	private DeploymentTarget target;
	
    /**
     * Timestamp of last time this deployment was talked to.
     */
    private Date lastActivity;
    
    private Date startDeployment;
    
    private Date endDeployment;
    
    /**
     * The state of this deployment with regards to the transaction.
     */
    private DeploymentState fState;
    
	private Set<String> tokens = Collections.synchronizedSet(new HashSet<String>());
	
	public DeploymentTracker(DeploymentTarget target)
	{
		lastActivity = new Date();
		startDeployment = new Date();
		this.target = target;
	}
	
	DeploymentTarget getTarget()
	{
		return target;
	}
	
	public void addToken(String token)
	{
		lastActivity = new Date();
		tokens.add(token);
	}
	
	public void removeToken(String token)
	{
		lastActivity = new Date();
		tokens.remove(token);
	}
	
	public Set<String> getTokens()
	{
		return tokens;
	}
	
	public void updateLastAccess()
	{
		lastActivity = new Date();
	}
}
