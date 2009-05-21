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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementScript extends BaseProcessorExtension implements Scopeable, RecordsManagementModel 
{
    private static final String RECORD_PROPERTIES = "recordProperties";
    private static final String RECORD_FOLDER = "recordFolder";

    private static Log logger = LogFactory.getLog(RecordsManagementScript.class);

   /** Scriptable scope object */
	private Scriptable scope;
	
	/** The service registry */
	private ServiceRegistry services;
	
	/** Records management service */
	private RecordsManagementActionService rmService;
    	
	/**
	 * Set the service registry
	 * 
	 * @param services	the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) 
	{
		this.services = services;
	}
	
	public NodeService getNodeService()
	{
	    return this.services.getNodeService();
	}
	
	/**
	 * Set the records management service
	 * 
	 * @param rmService    records management service
	 */
	public void setRecordsManagementActionService(RecordsManagementActionService rmService)
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
	
	public void executeRecordAction(ScriptNode node, String actionName)
	{
	    rmService.executeRecordAction(node.getNodeRef(), actionName, null);
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
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("makeRecord: " + node);
	    }
	    
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
	
	public boolean isRecordFolder(ScriptNode node)
    {
	   QName nodeType = services.getNodeService().getType(node.getNodeRef()); 
       return this.services.getDictionaryService().isSubClass(nodeType, RecordsManagementModel.TYPE_RECORD_FOLDER); 
    }
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public ScriptRecordFolder getRecordFolder(ScriptNode node)
	{
	    ScriptRecordFolder recordFolder = null;
	    NodeRef nodeRef = node.getNodeRef();
	    QName nodeType = this.services.getNodeService().getType(nodeRef);
	    if (this.services.getDictionaryService().isSubClass(nodeType, RecordsManagementModel.TYPE_RECORD_FOLDER) == true)
	    {
	        recordFolder = new ScriptRecordFolder(nodeRef, services, scope);
	    }
	    
	    return recordFolder;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public ScriptRecordCategory getRecordCategory(ScriptNode node)
	{
	    // Get the record categories for this node
	    NodeRef nodeRef = node.getNodeRef();
	    List<NodeRef> recordCategories = new ArrayList<NodeRef>(1);
	    getRecordCategory(nodeRef, recordCategories);
	    
	    // Create the record category script node
	    return new ScriptRecordCategory(scope, services, recordCategories);
	}
	
	public void executeRecordAction(String filePlanComponent, String name,
	        JSONObject parameters)
	{
	    NodeRef node = new NodeRef(filePlanComponent);
	    
	    //TODO NEIL Are all actions on pre-existing nodes? Assume yes.
	    if (this.services.getNodeService().exists(node) == false)
	    {
	        throw new InvalidNodeRefException(node.toString(), node);
	    }
	    
	    // I'm declaring paramsMap as the concrete type HashMap because it
	    // implements Serializable whereas Map does not.
	    // This comment applies to a number of Collection declarations below.
	    HashMap<String, Serializable> paramsMap = new HashMap<String, Serializable>();
	    if (parameters != null)
	    {
	        //TODO NEIL How much should this code know about the structure of the JSON?
	        //          To get started, I'm going to hardcode in a bit.
	        //TODO Fix this up after demo
	        String recordFolder;
            try
            {
                recordFolder = parameters.getString(RECORD_FOLDER);
                paramsMap.put(RECORD_FOLDER, recordFolder);

                JSONObject recProps = parameters.getJSONObject(RECORD_PROPERTIES);

                HashMap<String, Serializable> propsMap = new HashMap<String, Serializable>();
                for (Iterator iter = recProps.keys(); iter.hasNext(); )
                {
                    String nextKey = (String)iter.next();
                    Object object = recProps.get(nextKey);
                    
                    //TODO NEIL I'm assuming that props are either String values or JSONArrays.
                    //TODO      Reconsider after the demo.
                    Serializable nextValue;
                    if (object instanceof JSONArray)
                    {
                        JSONArray array = (JSONArray)object;
                        
                        ArrayList<String> arrayValues = new ArrayList<String>(array.length());
                        for (int i = 0; i < array.length(); i++)
                        {
                            arrayValues.add((String)array.get(i));
                        }
                        nextValue = arrayValues;
                    }
                    else
                    {
                        nextValue = (Serializable)object;
                    }
                    
                    propsMap.put(nextKey, nextValue);
                }
                paramsMap.put(RECORD_PROPERTIES, propsMap);
            } catch (JSONException e)
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn(e);
                    throw new AlfrescoRuntimeException("Error executing record action " + name, e);
                }
            }
	    }
	    
	    if (logger.isDebugEnabled())
	    {
	        StringBuilder buf = new StringBuilder();
	        buf.append("executeRecordAction ")
	            .append(node)
	            .append(" ")
	            .append(name)
	            .append(" ")
	            .append(paramsMap);
	        logger.debug(buf.toString());
	    }
	    
	    this.rmService.executeRecordAction(node, name, paramsMap);
	}

	/**
	 * 
	 * @param nodeRef
	 * @param recordCategories
	 */
	private void getRecordCategory(NodeRef nodeRef, List<NodeRef> recordCategories)
	{
	    if (nodeRef != null)
	    {
    	    if (RecordsManagementModel.TYPE_RECORD_CATEGORY.equals(this.services.getNodeService().getType(nodeRef)) == true)
    	    {
    	        recordCategories.add(nodeRef);
    	    }
    	    else
    	    {
    	        List<ChildAssociationRef> assocs = this.services.getNodeService().getParentAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
    	        for (ChildAssociationRef assoc : assocs)
                {
                    NodeRef parent = assoc.getParentRef();
                    getRecordCategory(parent, recordCategories);
                }
    	    }
	    }
	}
	
	/** ============== TEMP METHODS ADDED FOR PROTOTYPE ============= */
    
    public String[] getCustomRMAspects(Map<String, Serializable> properties)
    {
        List<String> aspects = new ArrayList<String>(10);
        for (String propName : properties.keySet())
        {
            QName propQName = QName.createQName(propName);            
            PropertyDefinition propDef = services.getDictionaryService().getProperty(propQName);
            ClassDefinition containerClass = propDef.getContainerClass();
            if (containerClass.isAspect() == true &&
                    services.getDictionaryService().isSubClass(containerClass.getName(), RecordsManagementModel.ASPECT_CUSTOM_RM_DATA) == true)
            {
                aspects.add(containerClass.getName().toString());
            }
        }
        
        return aspects.toArray(new String[aspects.size()]);
    }
}
