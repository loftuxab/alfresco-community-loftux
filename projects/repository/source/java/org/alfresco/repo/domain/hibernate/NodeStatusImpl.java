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
package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.NodeKey;
import org.alfresco.repo.domain.NodeStatus;

/**
 * Hibernate implementation of a <b>node status</b>
 * 
 * @author Derek Hulley
 */
public class NodeStatusImpl implements NodeStatus
{
    private NodeKey key;
    private String changeTxnId;
    private boolean deleted;

    public NodeKey getKey()
    {
        return key;
    }

    public void setKey(NodeKey key)
    {
        this.key = key;
    }

    public String getChangeTxnId()
    {
        return changeTxnId;
    }

    public void setChangeTxnId(String txnId)
    {
        this.changeTxnId = txnId;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }
}
