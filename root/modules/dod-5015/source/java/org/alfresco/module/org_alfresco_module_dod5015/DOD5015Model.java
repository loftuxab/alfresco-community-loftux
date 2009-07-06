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
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.service.namespace.QName;


/**
 * Helper class containing DOD 5015 model qualified names
 * 
 * @author Roy Wetherall
 */
public interface DOD5015Model extends RecordsManagementModel
{	
	// Namespace details
	public static String DOD_URI = "http://www.alfresco.org/model/dod5015/1.0";
	public static String DOD_PREFIX = "dod";
    
	// File plan type 
    public static QName TYPE_FILE_PLAN = QName.createQName(DOD_URI, "filePlan");
 
    // Record series type
    public static QName TYPE_RECORD_SERIES = QName.createQName(DOD_URI, "recordSeries");
    
    // Record Category
    public static QName TYPE_RECORD_CATEGORY = QName.createQName(DOD_URI, "recordCategory");
	
}
