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
 *
 * Script: onContentUpdate_rmaRecord.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when content is updated on a record.
 */ 
 
// Get the file plan and record nodes
var record = behaviour.args[0];
var isNewContent = behaviour.args[1];

if (record.hasAspect(rm.ASPECT_RECORD) == true && isNewContent == true)
{
    // Extract any meta-data
    logger.log("Extracting record meta-data");
    var action = actions.create("extract-metadata");
    action.execute(record);
    
    // Get the title and description of the record
    var title = record.properties["cm:title"];
    var description = record.properties["cm:description"];

    // Set the property values of the record based on the file plan
    record.properties["cm:title"] = record.properties["cm:name"];
    if (description == "" || description == null) 
    {
        description = title;
    }
    record.properties["rma:subject"] = description;
    
    // Add the emailed aspect if applicable
    if (record.hasAspect("cm:emailed") == true)
    {
        logger.log("Setting email details on record.");
        
        record.properties["rma:dateReceived"] = record.properties["cm:sentdate"];
        if (record.properties["rma:dateReceived"] == null)
        {
           record.properties["rma:dateReceived"] = record.properties["cm:modified"];
        }
      
        originator = record.properties["cm:originator"];
        if (originator == null || originator == "")
        {
            record.properties["rma:originator"] = record.properties["cm:author"] = person.name;
        }
        else
        {
            record.properties["rma:originator"] = record.properties["cm:author"] = originator;
        }
      
        if (record.properties["cm:subjectline"] != null && record.properties["cm:subjectline"] != "")
        {
            record.properties["rma:subject"] = record.properties["cm:description"] = record.properties["cm:subjectline"];
        } 
      
        var addressees = "";
        var strarray = record.properties["cm:addressees"];
        if (strarray != null) 
        {
            for (var i = 0; i<strarray.length; i++) 
            {
                if (i != 0) addressees += "; ";
                addressees += strarray[i];
            }
        }
        else 
        {
            addressees = record.properties["cm:addressee"];
        }
      
        if (addressees == "")
        {
            record.properties["rma:addressee"] = record.properties["cm:creator"];
        }
        else
        {
            record.properties["rma:addressee"] = addressees;
        }
    }
    
    record.save();
}