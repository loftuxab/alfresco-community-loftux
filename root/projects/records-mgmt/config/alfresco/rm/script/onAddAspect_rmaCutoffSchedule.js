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
 * Script: onAddAspect_rmaCutoffSchedule.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the cutoffSchedule aspect is added.
 */
 
var record = behaviour.args[0];
if (record.hasAspect(rm.ASPECT_RECORD) == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    { 
        logger.log("Found the record an file plan and populating the cutoff schedule properties");
        
        record.properties[rm.CUTOFF_EVENT] = filePlan.properties[rm.EVENT_TRIGGER];
        
        // Calculate the next cutoff period
        var cutoffPeriod = rm.calculateDateInterval(
                filePlan.properties[rm.PROP_CUTOFF_PERIOD_UNIT],
                filePlan.properties[rm.PROP_CUTOFF_PERIOD_VALUE],
                new Date());
                
        if (cutoffPeriod != null)        
        {
           logger.log("CutoffDate: " + cutoffPeriod);   
           record.properties[rm.PROP_CUTOFF_DATE_TIME] = cutoffPeriod.getTime();
        }
        
        record.save();
    }
}