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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.service.namespace.QName;

/**
 * This class contains the definitions and conversions between client-side and server-side
 * custom reference IDs. On the server-side a custom namespace,
 * as well as a label or (source and target) strings are combined and then
 * used as the qname of the association e.g. {http://www.alfresco.org/model/rmcustom/1.0}supported__null__null
 * 
 * @author Neil McErlean
 */
public class CustomReferenceId
{
	/**
	 * This String is used to separate label, source and target substrings in the server-side
	 * qname.
	 */
	public final static String SEPARATOR = "__";
	private Pattern regEx = Pattern.compile(RecordsManagementCustomModel.RM_CUSTOM_PREFIX + ":"
    		+ "(.+){1}" + SEPARATOR + "(.+){1}" + SEPARATOR + "(.+){1}");
    
    private static final List<CustomReferenceId> existingIds = new ArrayList<CustomReferenceId>();
    
    private final String label;
    private final String source;
    private final String target;
    
    private String mungedString;
    
    public CustomReferenceId(String label, String source, String target)
    {
    	// Either label must be set or else source AND target must be set.
    	if ((label != null || (source != null && target != null)) == false)
    	{
    		StringBuilder msg = new StringBuilder();
    		msg.append("Illegal custom reference id: ")
    		    .append("label=").append(label)
    		    .append(", source=").append(source)
    		    .append(", target=").append(target);
    		throw new IllegalArgumentException(msg.toString());
    	}
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
    	this.label = m.group(1);
    	this.source = m.group(2);
    	this.target = m.group(3);
    	
    	this.mungedString = serverSideId;
    }
    
    public String getReferenceId()
    {
    	if (mungedString == null)
    	{
    		StringBuilder buf = new StringBuilder();
    		buf.append(RecordsManagementCustomModel.RM_CUSTOM_PREFIX)
    		    .append(":")
    		    .append(label)
    		    .append(SEPARATOR)
    		    .append(source)
    		    .append(SEPARATOR)
    		    .append(target);

    		mungedString = buf.toString();
    	}
    	
    	//TODO check for overwrites
    	existingIds.add(this);
    	return mungedString;
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

	/**
	 * This method finds the qname string for the specified client-side id.
	 * 
	 * @param clientId this can be either a label string or a source__target string.
	 * @return
	 */
	public static String getReferenceIdFor(String clientId)
    {
		if (clientId == null)
		{
			return null;
		}

		String label = null;
		String source = null;
		String target = null;
		
		if (!clientId.contains(SEPARATOR))
		{
			label = clientId;
		}
		else
		{
			String[] substrings = clientId.split(SEPARATOR);
			source = substrings[0];
			target = substrings[1];
		}
		
		for (CustomReferenceId crId : existingIds)
		{
			if (label != null && label.equals(crId.getLabel()))
			{
				return crId.getReferenceId();
			}
			else if (source != null & target != null
					&& source.equals(crId.getSource())
					&& target.equals(crId.getTarget()))
			{
				return crId.getReferenceId();
			}
		}
		return null;
    }
}
