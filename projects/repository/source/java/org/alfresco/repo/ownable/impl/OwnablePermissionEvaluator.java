/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *
 * Created on 02-Aug-2005
 */
package org.alfresco.repo.ownable.impl;

import net.sf.acegisecurity.Authentication;

import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.impl.DynamicPermissionEvaluator;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Evaluates if the owner permission should be allowed given the node and authenticated user.
 * 
 * @author andyh
 */
public class OwnablePermissionEvaluator implements DynamicPermissionEvaluator
{

    public OwnablePermissionEvaluator()
    {
        super();
    }

    public AccessStatus evaluatePermission(NodeRef nodeRef, Authentication authentication)
    {
        return AccessStatus.DENIED;
    }

}
