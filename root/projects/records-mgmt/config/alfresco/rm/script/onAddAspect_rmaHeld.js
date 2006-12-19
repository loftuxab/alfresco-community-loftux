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
 * Script: onAddAspect_rmaHeld.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the held aspect is added.
 */

var record = behaviour.args[0];

if (record.hasAspect(rm.ASPECT_RECORD) == true)
{
    var filePlan = rm.getFilePlan(record);
    
    // Set the hold schedule details
    record.properties[rm.PROP_HOLD_UNTIL_EVENT] = filePlan.properties[rm.PROP_DISPOSITION_INSTRUCTIONS];
    record.properties[rm.PROP_FROZEN] = false;  
     
    var cutoffDateTime = null;    
    if (filePlan.properties[rm.PROP_DISCRETIONARY_HOLD] == true) 
    {
        logger.log("Creating discretionary hold.");
        cutoffDateTime = new Date();
        var newYear = cutoffDateTime.getFullYear() + 100;
        cutoffDateTime.setFullYear(newYear);
    }
    else
    {
        logger.log("Normal hold");
        cutoffDateTime = rm.calculateDateInterval(
                                      filePlan.properties[rm.PROP_HOLD_PERIOD_UNIT], 
                                      filePlan.properties[rm.PROP_HOLD_PERIOD_VALUE], 
                                      new Date());
    }
    
    if (cutoffDateTime != null)
    {
        record.properties[rm.PROP_HOLD_UNTIL] = cutoffDateTime;
    }
    
    record.save();
}