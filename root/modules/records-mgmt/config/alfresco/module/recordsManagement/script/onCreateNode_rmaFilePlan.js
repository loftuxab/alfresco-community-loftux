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
 * Script: onCreateNode_rmaFielPlan.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when a file plan node is created
 */ 
 
var filePlan = behaviour.args[0].child;

// Add the template to the file plan
filePlan.addAspect("cm:templatable");
filePlan.properties["cm:template"] = "workspace://SpacesStore/fileplan_template";
filePlan.save();