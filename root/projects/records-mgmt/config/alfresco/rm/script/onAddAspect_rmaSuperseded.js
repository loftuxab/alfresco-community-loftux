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
 * Script: onAddAspect_rmaSuperseeded.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the superseeded aspect is added.
 */
 
var record = behaviour.args[0];
if (record.hasAspect("rma:record") == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    {
        if (filePlan.properties[rm.PROP_CUTOFF_ON_SUPERSEDED] == true)        
        {
            // Add the cutoff aspect to the record
            record.addAspect(rm.ASPECT_CUTOFF);
       }
    }
}