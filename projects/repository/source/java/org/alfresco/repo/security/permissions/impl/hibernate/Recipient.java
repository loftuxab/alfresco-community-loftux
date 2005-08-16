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

import java.io.Serializable;
import java.util.Set;

/** 
 * The interface against which recipients of permission are persisted
 * @author andyh
 */
public interface Recipient extends Serializable 
{
    /**
     * Get the recipient.
     * 
     * @return
     */
    public String getRecipient();
    
    /**
     * Set the recipient
     * 
     * @param recipient
     */
    public void setRecipient(String recipient);
    
    /**
     * Get the external keys that map to this recipient.
     * 
     * @return
     */
    public Set<String> getExternalKeys();
}
