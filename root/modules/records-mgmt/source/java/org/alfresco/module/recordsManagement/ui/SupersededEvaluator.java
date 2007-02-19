/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.recordsManagement.ui;

import org.alfresco.module.recordsManagement.RecordsManagementModel;
import org.alfresco.web.bean.repository.Node;


/**
 * @author Roy Wetherall
 */
public class SupersededEvaluator extends BaseEvaluator
{
    public boolean evaluate(Node node)
    {
        boolean result = false;
        if (node.hasAspect(RecordsManagementModel.ASPECT_RECORD) == true &&
            node.hasAspect(RecordsManagementModel.ASPECT_CUTOFF) == false &&
            node.hasAspect(RecordsManagementModel.ASPECT_HELD) == false &&
            node.hasAspect(RecordsManagementModel.ASPECT_SUPERSEDED) == false &&
            isRecordsManager() == true)
        {
            result = true;            
        }
        return result;
    }
}
