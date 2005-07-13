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
package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.IntegrityEvent;

public class IntegrityEventImpl implements IntegrityEvent
{
    private Long id;
    private String transactionId;
    private String eventType;
    private String primaryNodeRef;
    private String secondaryNodeRef;
    private String aspectTypeQName;
    private String assocTypeQName;
    private String assocQName;
    private StackTraceElement[] trace;

    public String toString()
    {
        StringBuilder sb = new StringBuilder(150);
        sb.append("IntegrityEvent")
          .append("[ transactionId=").append(transactionId)
          .append(", type=").append(eventType)
          .append(", primary=").append(primaryNodeRef)
          .append(", secondary=").append(secondaryNodeRef)
          .append(", aspect type=").append(aspectTypeQName)
          .append(", assoc type=").append(assocTypeQName)
          .append(", assoc name=").append(assocQName)
          .append(", trace=").append(trace != null)
          .append("]");
        return sb.toString();
    }
    
    public Long getId()
    {
        return id;
    }
    
    /**
     * For Hibernate use
     */
    private void setId(Long id)
    {
        this.id = id;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String txnId)
    {
        this.transactionId = txnId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getPrimaryNodeRef()
    {
        return primaryNodeRef;
    }

    public void setPrimaryNodeRef(String nodeRef)
    {
        this.primaryNodeRef = nodeRef;
    }

    public String getSecondaryNodeRef()
    {
        return secondaryNodeRef;
    }

    public void setSecondaryNodeRef(String nodeRef)
    {
        this.secondaryNodeRef = nodeRef;
    }
    
    public String getAspectTypeQName()
    {
        return aspectTypeQName;
    }

    public void setAspectTypeQName(String qname)
    {
        this.aspectTypeQName = qname;
    }

    public String getAssocTypeQName()
    {
        return assocTypeQName;
    }

    public void setAssocTypeQName(String assocTypeQName)
    {
        this.assocTypeQName = assocTypeQName;
    }

    public String getAssocQName()
    {
        return assocQName;
    }

    public void setAssocQName(String assocQName)
    {
        this.assocQName = assocQName;
    }

    public StackTraceElement[] getTrace()
    {
        return trace;
    }

    public void setTrace(StackTraceElement[] trace)
    {
        this.trace = trace;
    }
}
