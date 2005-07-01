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
 */
package org.alfresco.repo.copy;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public interface CopyServicePolicies 
{
	/**
	 * Policy invoked when a <b>node</b> is copied
	 */
	public interface OnCopyNodePolicy extends ClassPolicy
	{
        /**
         * 
         * @param classRef              the type of node being copied
         * @param sourceNodeRef         node being copied
         * @param destinationStoreRef   the destination store reference
         * @param copyToNewNode         indicates whether we are copying to a new node or not 
         * @param copyDetails           modifiable <b>node</b> details
         */
		public void onCopyNode(
				QName classRef,
				NodeRef sourceNodeRef,
                StoreRef destinationStoreRef,
                boolean copyToNewNode,
				PolicyScope copyDetails);
	}
}
