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
package org.alfresco.repo.security.permissions.impl.hibernate;

import java.util.Set;

import org.alfresco.repo.domain.NodeKey;
import org.alfresco.service.cmr.repository.NodeRef;

public interface NodePermissionEntry
{
    public NodeKey getNodeKey();

    public void setNodeKey(NodeKey key);
    
    public NodeRef getNodeRef();
    
    public boolean getInherits();
    
    public void setInherits(boolean inherits);
    
    public Set<PermissionEntry> getPermissionEntries();
    
}
