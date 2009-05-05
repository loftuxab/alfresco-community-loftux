/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;

/**
 *
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementScript extends BaseProcessorExtension implements Scopeable, RecordsManagementModel 
{
   /** Scriptable scope object */
	private Scriptable scope;
	
	/** The service registry */
	private ServiceRegistry services;
	
	private RecordsManagementService rmService;
    
    /** Value converter */
    private ValueConverter valueConverter = new ValueConverter();
    	
	/**
	 * Set the service registry
	 * 
	 * @param services	the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) 
	{
		this.services = services;
	}
	
	/**
	 * Set the records management service
	 * 
	 * @param rmService    records management service
	 */
	public void setRecordsManagementService(RecordsManagementService rmService)
	{
	    this.rmService = rmService;
	}
	
	/**
	 * Set the scope
	 * 
	 * @param scope	the script scope
	 */
	public void setScope(Scriptable scope) 
	{
		this.scope = scope;
	}
	
	/**
	 * Determines whether a node is a record or not
	 * 
	 * @param node
	 * @return
	 */
	public boolean isRecord(ScriptNode node)
	{
	   return this.services.getNodeService().hasAspect(node.getNodeRef(), RecordsManagementModel.ASPECT_RECORD); 
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public ScriptRecord makeRecord(ScriptNode node)
	{
	    NodeRef nodeRef = node.getNodeRef();
	    this.services.getNodeService().addAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD, null);
	    return new ScriptRecord(nodeRef, this.services, this.scope);
	}
	
	/**
	 * Casts a normal node into a record assuming that the node has the record aspect.  Returns 
	 * null if the records aspect is not present.
	 * 
	 * @param node
	 * @return ScriptRecord    the record object, null if the passed node is not a record
	 */
	public ScriptRecord getRecord(ScriptNode node)
	{
	    ScriptRecord record = null;
	    NodeRef nodeRef = node.getNodeRef();
	    
	    if (this.services.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD) == true)
	    {
	        record = new ScriptRecord(nodeRef, this.services, this.scope);
	    }
	    
	    return record;
	}
	
	/**
	 * 
	 * @param recordCategory
	 * @return
	 */
	public String generateRecordId(ScriptNode recordCategory)
	{
	    return this.rmService.generateRecordId(recordCategory.getNodeRef());
	}
}
