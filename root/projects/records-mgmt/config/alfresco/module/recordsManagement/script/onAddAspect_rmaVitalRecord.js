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
 * Script: onAddAspect_rmaVitalRecord.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the vitalRecord aspect is added.
 */
 
var record = behaviour.args[0];
if (record.hasAspect(rm.ASPECT_RECORD) == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    {   
        // Get the current date
        var myDate = new Date();
        
        // Set the properties
        record.properties["rma:prevReviewDate"] = myDate.getTime();
    
        // Calculate the next review period.
        var reviewPeriod = rm.calculateDateInterval(
                                      filePlan.properties[rm.PROP_VITAL_RECORD_REVIEW_PERIOD_UNIT], 
                                      filePlan.properties[rm.PROP_VITAL_RECORD_REVIEW_PERIOD_VALUE], 
                                      myDate);
        logger.log("Review period: " + reviewPeriod);                                  
        if (reviewPeriod != null)
        {
           record.properties["rma:nextReviewDate"] = reviewPeriod.getTime();
        }
        
        record.save();
    }
}