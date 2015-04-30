/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.util.List;

import org.alfresco.repo.dictionary.DynamicModelPolicies;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2Type;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Dynamic class monitor connects tennant specific Dynamic Classes to the 
 * SyncChangeMonitor.  So when a tennant specific aspect or property is defined then
 * it will be synced.
 * <p>
 * At the moment there is no functionality to delete.
 * 
 * @author mrogers
 *
 */

public class DynamicClassMonitor implements DynamicModelPolicies.OnLoadDynamicModel
{
    // Logging support
    private static Log logger = LogFactory.getLog(DynamicClassMonitor.class);
    
	private SyncChangeMonitor syncChangeMonitor;
	
	private PolicyComponent policyComponent;
	
	private NamespaceService namespaceService;


	
	public void init()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("init called");
		}
		PropertyCheck.mandatory(this, "policyComponent", getPolicyComponent());
		PropertyCheck.mandatory(this, "namespaceService", getNamespaceService());
		PropertyCheck.mandatory(this, "syncChangeMonitor", syncChangeMonitor);
		
		getPolicyComponent().bindClassBehaviour(DynamicModelPolicies.OnLoadDynamicModel.QNAME, 
				this, 
				 new JavaBehaviour(this, 
						 "onLoadDynamicModel", 
						 Behaviour.NotificationFrequency.EVERY_EVENT));
	}
	
	@Override
	public void onLoadDynamicModel(M2Model model, NodeRef nodeRef) 
	{	
		if(logger.isDebugEnabled())
		{
			logger.debug("dynamic model loaded for nodeRef:" + nodeRef);
		}
 	    List<M2Aspect> aspects = model.getAspects();
        for(M2Aspect aspect : aspects)
     	{
 		    QName aspectQName = QName.createQName(aspect.getName(), namespaceService);
 		    syncChangeMonitor.addCustomAspectToTrack(aspectQName);
		    if(logger.isDebugEnabled())
		    {
		       logger.debug("dynamic custom aspect " + aspectQName); 
		    }
 		    List<M2Property> properties = aspect.getProperties();
 		    for(M2Property property : properties)
 		    {
 		       QName propertyQName = QName.createQName(property.getName(), namespaceService);
 		       syncChangeMonitor.addCustomPropertyToTrack(propertyQName);
 		       if(logger.isDebugEnabled())
 		       {
 		    	   logger.debug("dynamic custom aspect property" + propertyQName); 
 		       }
 		    }
        }
 	 
 	    List<M2Type> types = model.getTypes();
 	    for(M2Type type : types)
 	    {
 		    List<M2Property> properties = type.getProperties(); 
 		 
 		    for(M2Property property : properties)
 		    { 
 			    QName propertyQName = QName.createQName(property.getName(), namespaceService);
 			    syncChangeMonitor.addCustomPropertyToTrack(propertyQName);
  		       if(logger.isDebugEnabled())
  		       {
  		    	   logger.debug("dynamic custom property" + propertyQName); 
  		       }
 		    }
 	     }	
	}

	public NamespaceService getNamespaceService() 
	{
		return namespaceService;
	}

	public void setNamespaceService(NamespaceService namespaceService) 
	{
		this.namespaceService = namespaceService;
	}
	
	public SyncChangeMonitor getSyncChangeMonitor() 
	{
		return syncChangeMonitor;
	}

	public void setSyncChangeMonitor(SyncChangeMonitor syncChangeMonitor) 
	{
		this.syncChangeMonitor = syncChangeMonitor;
	}

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
}
