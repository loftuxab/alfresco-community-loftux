/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to represent a Records Management audit entry.
 * 
 * @author Gavin Cornwell
 */
public final class RecordsManagementAuditEntry
{
    private final Date timestamp;
    private final String userName;
    private final String fullName;
    private final String userRole;
    private final NodeRef nodeRef;
    private final String nodeName;
    private final String nodeType;
    private final String event;
    private final String identifier;
    private final String path;
    private final Map<QName, Serializable> beforeProperties;
    private final Map<QName, Serializable> afterProperties;
 
    /**
     * Default constructor
     */
    public RecordsManagementAuditEntry(Date timestamp, 
                String userName, String fullName, String userRole, 
                NodeRef nodeRef, String nodeName, String nodeType, 
                String event, String identifier, String path,
                Map<QName, Serializable> beforeProperties,
                Map<QName, Serializable> afterProperties)
    {
        ParameterCheck.mandatory("timestamp", timestamp);
        ParameterCheck.mandatory("userName", userName);
        
        this.timestamp = timestamp;
        this.userName = userName;
        this.userRole = userRole;
        this.fullName = fullName;
        this.nodeRef = nodeRef;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.event = event;
        this.identifier = identifier;
        this.path = path;
        this.beforeProperties = beforeProperties;
        this.afterProperties = afterProperties;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(")
          .append("timestamp=").append(timestamp)
          .append(", userName=").append(userName)
          .append(", userRole=").append(userRole)
          .append(", fullName=").append(fullName)
          .append(", nodeRef=").append(nodeRef)
          .append(", nodeName=").append(nodeName)
          .append(", event=").append(event)
          .append(", identifier=").append(identifier)
          .append(", path=").append(path)
          .append(", beforeProperties=").append(beforeProperties)
          .append(", afterProperties=").append(afterProperties)
          .append(")");
        return sb.toString();
    }
    
    /**
     * 
     * @return The state of this audit entry as a JSON string
     */
    public String toJSONString()
    {
        try
        {
            JSONObject entry = new JSONObject();
            
            entry.put("timestamp", getTimestampString());
            entry.put("userName", this.userName);
            entry.put("userRole", this.userRole == null ? "": this.userRole);
            entry.put("fullName", this.fullName == null ? "": this.fullName);
            entry.put("nodeRef", this.nodeRef == null ? "": this.nodeRef);
            entry.put("nodeName", this.nodeName == null ? "": this.nodeName);
            entry.put("nodeType", this.nodeType == null ? "": this.nodeType);
            entry.put("event", this.event == null ? "": this.event);
            entry.put("identifier", this.identifier == null ? "": this.identifier);
            entry.put("path", this.path == null ? "": this.path);
        
            JSONArray changedValues = new JSONArray();
            
            if (this.beforeProperties != null && this.afterProperties != null)
            {
                // create an entry for each property that existed before
                for (QName valueName : this.beforeProperties.keySet())
                {
                    JSONObject changedValue = new JSONObject();
                    changedValue.put("name", valueName);
                    changedValue.put("previous", this.beforeProperties.get(valueName));
                    
                    Serializable newValue = this.afterProperties.get(valueName);
                    changedValue.put("new", newValue == null ? "" : newValue.toString());
                    
                    changedValues.put(changedValue);
                }
                
                // create an entry for each property that exists after but has
                // not already been added
                for (QName valueName : this.afterProperties.keySet())
                {
                    if (!this.beforeProperties.containsKey(valueName))
                    {
                        JSONObject changedValue = new JSONObject();
                        changedValue.put("name", valueName);
                        changedValue.put("previous", "");
                        
                        Serializable newValue = this.afterProperties.get(valueName);
                        changedValue.put("new", newValue == null ? "" : newValue.toString());
                        
                        changedValues.put(changedValue);
                    }
                }
            }
            
            entry.put("changedValues", changedValues);
            
            return entry.toString();
        }
        catch (JSONException je)
        {
            return "{}";
        }
    }

    /**
     * 
     * @return The date of the audit entry
     */
    public Date getTimestamp()
    {
        return this.timestamp;
    }
    
    /**
     * 
     * @return The date of the audit entry as an ISO8601 formatted String
     */
    public String getTimestampString()
    {
        return ISO8601DateFormat.format(this.timestamp);
    }

    /**
     * 
     * @return The username of the user that caused the audit log entry to be created
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * 
     * @return The full name of the user that caused the audit log entry to be created
     */
    public String getFullName()
    {
        return this.fullName;
    }

    /**
     * 
     * @return The role of the user that caused the audit log entry to be created
     */
    public String getUserRole()
    {
        return this.userRole;
    }

    /**
     * 
     * @return The NodeRef of the node the audit log entry is for
     */
    public NodeRef getNodeRef()
    {
        return this.nodeRef;
    }

    /**
     * 
     * @return The name of the node the audit log entry is for
     */
    public String getNodeName()
    {
        return this.nodeName;
    }
    
    /**
     * 
     * @return The type of the node the audit log entry is for
     */
    public String getNodeType()
    {
        return this.nodeType;
    }

    /**
     * 
     * @return The human readable description of the reason for the audit log 
     *         entry i.e. metadata updated, record declared
     */
    public String getEvent()
    {
        return this.event;
    }

    /**
     * An identifier for the item being audited, for example for a record
     * it will be the unique record identifier, for a user it would be the 
     * username etc.
     * 
     * @return Ad identifier for the thing being audited
     */
    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * 
     * @return The path to the object being audited
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * 
     * @return Map of properties before the audited action
     */
    public Map<QName, Serializable> getBeforeProperties()
    {
        return this.beforeProperties;
    }
    
    /**
     * 
     * @return Map of properties after the audited action
     */
    public Map<QName, Serializable> getAfterProperties()
    {
        return this.beforeProperties;
    }
}
