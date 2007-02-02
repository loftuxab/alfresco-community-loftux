/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
 
 