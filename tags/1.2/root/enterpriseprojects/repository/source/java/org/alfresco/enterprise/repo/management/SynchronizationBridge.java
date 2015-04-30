package org.alfresco.enterprise.repo.management;

import java.util.List;

import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanRegistry;
import org.alfresco.repo.management.subsystems.PropertyBackedBeanUnregisteredEvent;
import org.alfresco.repo.security.authentication.AuthenticatorDeletedEvent;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Class that listens for Authentication subsystems to be de-registered then broadcasts the event in 
 * application scope.  So that the ChainingUserRegistrySynchronizer can hear events from the authentication 
 * subsystems.
 * 
 * @author mrogers
 *
 */
public class SynchronizationBridge implements ApplicationListener<ApplicationEvent>, ApplicationContextAware
{
	private PropertyBackedBeanRegistry registry;
	
	// need to get application context
	private ApplicationContext context;
	
	public void init()
	{
	    PropertyCheck.mandatory(this, "registry", getRegistry());
		getRegistry().addListener(this);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) 
	{
		if (event instanceof PropertyBackedBeanUnregisteredEvent)
	    {
			PropertyBackedBeanUnregisteredEvent pure = (PropertyBackedBeanUnregisteredEvent)event;
		
			if(pure.isPermanent() && pure.getSource() instanceof ChildApplicationContextFactory)
			{
				ChildApplicationContextFactory authenticator = (ChildApplicationContextFactory) pure.getSource();
				
				List<String> ids = authenticator.getId();
				// this is permanent is it an authenticator?
				if("Authentication".equalsIgnoreCase(ids.get(0)))
				{
					// broadcast to application scope	
					context.publishEvent(new AuthenticatorDeletedEvent(ids.get(2)));
				}
			}
	    }
	}

	public void setRegistry(PropertyBackedBeanRegistry registry) {
		this.registry = registry;
	}

	public PropertyBackedBeanRegistry getRegistry() {
		return registry;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException 
    {
		this.context = applicationContext;
	}


}
