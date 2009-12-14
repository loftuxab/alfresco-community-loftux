/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.deployment.impl.server;

import org.alfresco.deployment.DeploymentTarget;
import org.springframework.extensions.surf.util.PropertyCheck;

/**
 * Utility class to register a deployment target with the deployment engine.
 */
public class DeploymentTargetRegistrationBean 
{
	private String name;
	
	private DeploymentTargetRegistry registry;
	
	private DeploymentTarget target;
	
	/**
	 * Register the deployment target with the deployment target registry
	 */
	public void register()
	{
		PropertyCheck.mandatory(this, "name", name);
		PropertyCheck.mandatory(this, "registry", registry);
		PropertyCheck.mandatory(this, "target", target);
		
		/**
		 * Go ahead and do the registration
		 */
		registry.registerTarget(name, target);
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setRegistry(DeploymentTargetRegistry registry) 
	{
		this.registry = registry;
	}
	
	public DeploymentTargetRegistry getRegistry() 
	{
		return registry;
	}
	public void setTarget(DeploymentTarget target) 
	{
		this.target = target;
	}
	public DeploymentTarget getTarget() 
	{
		return target;
	}
}
