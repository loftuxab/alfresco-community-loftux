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
 * Script: onAddAspect_rmaCutoff.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the cutoff aspect is added.
 */ 
 
var record = behaviour.args[0];

if (record.hasAspect(rm.ASPECT_RECORD) == true)
{
    var filePlan = rm.getFilePlan(record);    
    
    if (filePlan.properties[rm.PROP_PROCESS_HOLD]) 
    {
        /// Hold the record
        record.addAspect(rm.ASPECT_HELD);
    }
    else
    {
        // Process any dispositions that should occure immediatly
        rm.processImmediateDispositions(record);
    }
    
    // Ensure that once a record is cutoff only the record managers can see it
    rm.setCutoffPermissions(record);
}