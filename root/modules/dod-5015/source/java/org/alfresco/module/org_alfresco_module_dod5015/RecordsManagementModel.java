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
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.service.namespace.QName;

/**
 * Helper class containing records management qualified names
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementModel 
{
	
	// Namespace details
	public static String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
	public static String RM_PREFIX = "rma";
    
    // File plan type 
    public static QName TYPE_FILE_PLAN = QName.createQName(RM_URI, "filePlan");
    
    // Record Category
    public static QName TYPE_RECORD_CATEGORY = QName.createQName(RM_URI, "recordCategory");
    public static QName ASSOC_DISPOSITION_ACTIONS = QName.createQName(RM_URI, "dispositionActions");
    
    // Disposition actions
    public static QName PROP_TYPE = QName.createQName(RM_URI, "dispositionAction");
    public static QName PROP_DISPOSITION_ACTION_NAME = QName.createQName(RM_URI, "dispositionActionName");
    public static QName PROP_DISPOSITION_DESCRIPTION = QName.createQName(RM_URI, "dispositionDescription");
    public static QName PROP_DISPOSITION_PERIOD = QName.createQName(RM_URI, "dispositionPeriod");
    public static QName PROP_DISPOSITION_PERIOD_PROPERTY = QName.createQName(RM_URI, "dispositionPeriodProperty");
    
    // Records folder
    public static QName TYPE_RECORD_FOLDER = QName.createQName(RM_URI, "recordFolder");
    public static QName PROP_IS_CLOSED = QName.createQName(RM_URI, "isClosed");
    
    // Undeclared record aspect
    public static QName ASPECT_UNDECLARED_RECORD = QName.createQName(RM_URI, "undeclaredRecord");
    
    // Record aspect
    public static QName ASPECT_RECORD = QName.createQName(RM_URI, "record");
    public static QName PROP_DATE_FILED = QName.createQName(RM_URI, "dateFiled");
    public static QName PROP_ORIGINATOR = QName.createQName(RM_URI, "originator");
    public static QName PROP_ORIGINATING_ORGANIZATION = QName.createQName(RM_URI, "originatingOrganization");
    public static QName PROP_PUBLICATION_DATE = QName.createQName(RM_URI, "publicationDate");
    public static QName PROP_MEDIA_TYPE = QName.createQName(RM_URI, "mediaType");
    public static QName PROP_FORMAT = QName.createQName(RM_URI, "format");
    public static QName PROP_DATE_RECEIVED = QName.createQName(RM_URI, "dateReceived");  
    
    // Common record details
    public static QName PROP_SUPPLEMENTAL_MARKING_LIST = QName.createQName(RM_URI, "supplementalMarkingList");
    public static QName PROP_LOCATION = QName.createQName(RM_URI, "location");
    
    // Fileable aspect
    public static QName ASPECT_FILABLE = QName.createQName(RM_URI, "fileable");
    
    // Record component identifier aspect
    public static QName ASPECT_RECORD_COMPONENT_ID = QName.createQName(RM_URI, "recordComponentIdentifier");
    public static QName PROP_IDENTIFIER = QName.createQName(RM_URI, "identifier");
    
    // Vital record details aspect
    public static QName ASPECT_VITAL_RECORD_DETAILS = QName.createQName(RM_URI, "vitalRecordDetails");
    public static QName PROP_VITAL_RECORD_INDICATOR = QName.createQName(RM_URI, "vitalRecordIndicator");
    public static QName PROP_REVIEW_PERIOD = QName.createQName(RM_URI, "reviewPeriod");
     
    // Vital record aspect
    public static QName ASPECT_VITAL_RECORD = QName.createQName(RM_URI, "vitalRecord");
    public static QName PROP_REVIEW_AS_OF = QName.createQName(RM_URI, "reviewAsOf");        
    
    // Cut off aspect
    public static QName ASPECT_CUT_OFF = QName.createQName(RM_URI, "cutOff");
    public static QName PROP_CUT_OFF_DATE = QName.createQName(RM_URI, "cutOffDate");
    
    // Disposition schedule aspect
    public static QName ASPECT_DISPOSITION_SCHEDULE = QName.createQName(RM_URI, "dispositionSchedule");
    public static QName PROP_DISPOSITION_ACTION_ID = QName.createQName(RM_URI, "dispositionActionId");
    public static QName PROP_DISPOSITION_ACTION = QName.createQName(RM_URI, "dispositionAction");
    public static QName PROP_DISPOSITION_AS_OF = QName.createQName(RM_URI, "dispositionAsOf");
    public static QName PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION = QName.createQName(RM_URI, "previousDispositionAction");
    public static QName PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE = QName.createQName(RM_URI, "previousDispositionDate");
    
    // Custom RM data aspect
    public static QName ASPECT_CUSTOM_RM_DATA = QName.createQName(RM_URI, "customRMData");
    
	
}
