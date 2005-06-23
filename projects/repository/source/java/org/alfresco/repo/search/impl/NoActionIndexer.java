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
package org.alfresco.repo.search.impl;

import org.alfresco.repo.search.Indexer;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A no action indexer - the indexing is done automatically along with
 * persistence
 * 
 * TODO: Rename to Adaptor?
 * 
 * @author andyh
 * 
 */
public class NoActionIndexer implements Indexer
{

    public void createNode(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void updateNode(NodeRef nodeRef)
    {
        return;
    }

    public void deleteNode(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void createChildRelationship(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void updateChildRelationship(ChildAssociationRef relationshipBeforeRef, ChildAssociationRef relationshipAfterRef)
    {
        return;
    }

    public void deleteChildRelationship(ChildAssociationRef relationshipRef)
    {
        return;
    }

}
