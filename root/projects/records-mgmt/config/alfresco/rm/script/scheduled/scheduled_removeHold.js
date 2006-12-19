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
 * Script: scheduled_removeHold.js
 * Author: Roy Wetherall
 * 
 * Checks for held records that have passed their hold untill date and are not frozen and remove the hold.
 */
 
 logger.log("Executing scheduled remove hold");
 
// Calculate the date range used in the query
var newDate = new Date();
var currentdate = String(newDate.getFullYear()) + "-"
                  + utils.pad(String(newDate.getMonth()+1),2) + "-"
                  + utils.pad(String(newDate.getDate()),2)
                  + "T00:00:00.00Z";                  
var dateRange = "[\"1970-01-01T00:00:00.00Z\" TO \"" + currentdate + "\"]";

// Execute the query and process the results
var query = "+ASPECT:\"rma:record\" +ASPECT:\"rma:held\" +ASPECT:\"rma:cutoff\" +@rma\\:frozen:FALSE +@rma\\:holdUntil:" + dateRange;      

logger.log(query);

var records = search.luceneSearch(query);   
for (var i=0; i<records.length; i++) 
{
    // Get the record
    record = records[i];         
    
    // Remove the hold
    record.removeAspect(rm.ASPECT_HELD);
}
                  
                  