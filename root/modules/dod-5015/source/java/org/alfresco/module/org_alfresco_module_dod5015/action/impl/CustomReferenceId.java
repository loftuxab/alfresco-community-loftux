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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.service.namespace.QName;

/**
 * This class contains the definitions and conversions between client-side and server-side
 * custom reference IDs. On the client-side, there is a non-namespaced, simple name
 * e.g. customRefStandard1250274577519. However on the server-side a custom namespace,
 * as well as a label or (source and target) strings are added to the id, which is then
 * used as the qname of the association e.g. {http://www.alfresco.org/model/rmcustom/1.0}customRefStandard1250274577519__supported__null__null
 * 
 * @author Neil McErlean
 */
public class CustomReferenceId
{
	private Pattern regEx = Pattern.compile(RecordsManagementCustomModel.RM_CUSTOM_PREFIX + ":"
    		+"(.+){1}" + SEPARATOR + "(.+){1}" + SEPARATOR
    		+ "(.+){1}" + SEPARATOR + "(.+){1}");
    
    private static final Map<String, CustomReferenceId> mappings = new HashMap<String, CustomReferenceId>();
    private final static String SEPARATOR = "__";
    
    private final String uiName;
    private final String label;
    private final String source;
    private final String target;
    
    private String mungedString;
    
    public CustomReferenceId(String uiName, String label, String source, String target)
    {
    	// Either label must be set or else source AND target must be set.
    	if ((label != null || (source != null && target != null)) == false)
    	{
    		StringBuilder msg = new StringBuilder();
    		msg.append("Illegal custom reference id: ")
    		    .append("uiName=").append(uiName)
    		    .append(", label=").append(label)
    		    .append(", source=").append(source)
    		    .append(", target=").append(target);
    		throw new IllegalArgumentException(msg.toString());
    	}
    	this.uiName = uiName;
    	this.label = label;
    	this.source = source;
    	this.target = target;
    }
    
    public CustomReferenceId(QName serverSideQName)
    {
    	this(serverSideQName.toPrefixString());
    }
    
    public CustomReferenceId(String serverSideId)
    {
    	Matcher m = regEx.matcher(serverSideId);
    	if (!m.matches()) throw new IllegalArgumentException("Illegal CustomReferenceId: " + serverSideId);
    	this.uiName = m.group(1);
    	this.label = m.group(2);
    	this.source = m.group(3);
    	this.target = m.group(4);
    	
    	this.mungedString = serverSideId;
    }
    
    public String getReferenceId()
    {
    	if (mungedString == null)
    	{
    		//TODO Munge up something usable here.
    		StringBuilder buf = new StringBuilder();
    		buf.append(RecordsManagementCustomModel.RM_CUSTOM_PREFIX)
    		    .append(":")
    		    .append(uiName)
    		    .append(SEPARATOR)
    		    .append(label)
    		    .append(SEPARATOR)
    		    .append(source)
    		    .append(SEPARATOR)
    		    .append(target);

    		mungedString = buf.toString();
    	}
    	
    	//TODO Do I need to check for overwrites?
    	mappings.put(uiName, this);
    	return mungedString;
    }
    
    public String getUiName()
    {
		return uiName;
	}

	public String getLabel()
	{
		return label;
	}

	public String getSource()
	{
		return source;
	}

	public String getTarget()
	{
		return target;
	}

	public static String getReferenceIdFor(String uiName)
    {
    	CustomReferenceId refIdObject = mappings.get(uiName);
		return refIdObject == null ? null : refIdObject.getReferenceId();
    }
}
