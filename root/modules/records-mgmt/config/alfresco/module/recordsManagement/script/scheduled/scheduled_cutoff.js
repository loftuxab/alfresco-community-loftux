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
 * Script: scheduled_cutoff.js
 * Author: Roy Wetherall
 * 
 * Checks for record reaedy for scheduled cutoff and applied the cutoff aspect is appropriate.
 */
 
 logger.log("Executing scheduled cutoff");
 
// Calculate the date range used in the query
var newDate = new Date();
var currentdate = String(newDate.getFullYear()) + "-"
                  + utils.pad(String(newDate.getMonth()+1),2) + "-"
                  + utils.pad(String(newDate.getDate()),2)
                  + "T00:00:00.00Z";                  
var dateRange = "[\"1970-01-01T00:00:00.00Z\" TO \"" + currentdate + "\"]";

// Execute the query and process the results
var query = "+ASPECT:\"rma:record\" +ASPECT:\"rma:cutoffSchedule\" -ASPECT:\"rma:cutoff\" +@rma\\:cutoffDateTime:" + dateRange;      
logger.log(query);

var records = search.luceneSearch(query);   
logger.log(records.length);
for (var i=0; i<records.length; i++) 
{
    // Get the record
    record = records[i];         
    
    // Cut the record off
    record.addAspect(rm.ASPECT_CUTOFF);
}
                  
                  