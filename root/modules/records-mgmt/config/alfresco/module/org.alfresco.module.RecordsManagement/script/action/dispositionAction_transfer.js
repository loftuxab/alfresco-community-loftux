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
 * Script: dispositionAction_accession.js
 * Author: Roy Wetherall
 * 
 * Transfer disposition action implementation.
 */
 
 var record = document;
 if (record.hasAspect(rm.ASPECT_TRANSFERED) == false)
 {
     var transferLocation = action.parameters[rm.PARAM_LOCATION];
     
     // TODO: do we need to ensure that is actually a record?
     
     // TODO: for now we assume that this is a valid path location to a folder in the spaces store of the repository
     // Resolve the path to a node
     var nodes = search.xpathSearch(transferLocation);
     if (nodes.length == 1)
     {
         var node = nodes[0];
         record.move(node);
         record.addAspect("rma:transfered");
     }
     else
     {
         // TODO how do we handle exceptions
         logger.log("An invalid transfer location has been set: " + transferLocation);
     }
 }
 else
 {
     logger.log("This record has already been transfered. (" + record.id + ")");
 }
 
 