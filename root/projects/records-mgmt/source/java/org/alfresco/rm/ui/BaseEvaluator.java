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
 */
package org.alfresco.rm.ui;

import java.util.Set;

import javax.faces.context.FacesContext;

import org.alfresco.rm.RecordsManagementModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.web.action.ActionEvaluator;
import org.alfresco.web.bean.repository.Repository;


/**
 * 
 * @author Roy Wetherall
 */
public abstract class BaseEvaluator implements ActionEvaluator
{
    protected ServiceRegistry getServiceRegistry()
    {
        return Repository.getServiceRegistry(FacesContext.getCurrentInstance());
    }
    
    protected boolean isRecordsManager()
    {
        boolean result = false;
        String currentUser = getServiceRegistry().getAuthenticationService().getCurrentUserName();
        Set<String> authorities = getServiceRegistry().getAuthorityService().getContainingAuthorities(null, currentUser, false);
        if (getServiceRegistry().getAuthorityService().hasAdminAuthority() == true || 
            authorities.contains(RecordsManagementModel.RM_GROUP) == true)
        {
            result = true;
        }        
        return result;
    }
}
