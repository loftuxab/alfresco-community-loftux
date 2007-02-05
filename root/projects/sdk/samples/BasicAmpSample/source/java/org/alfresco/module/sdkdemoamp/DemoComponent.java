/*
 * Copyright (C) 2007 Alfresco, Inc.
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
 */
package org.alfresco.module.sdkdemoamp;

import org.alfresco.repo.module.AbstractModuleComponent;

/**
 * A basic component that will be started for this module.
 * 
 * @author Derek Hulley
 */
public class DemoComponent extends AbstractModuleComponent
{
    @Override
    protected void executeInternal() throws Throwable
    {
        System.out.println("DemoComponent has been executed");
    }
}
