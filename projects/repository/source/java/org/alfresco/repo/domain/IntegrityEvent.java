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
package org.alfresco.repo.domain;

/**
 * Interface for the various integrity events that are recorded during a
 * transaction.
 * 
 * @author Derek Hulley
 */
public interface IntegrityEvent
{
    public static final String EVENT_TYPE_NODE_CREATED = "nodeCreated";
    public static final String EVENT_TYPE_NODE_DELETED = "nodeDeleted";
    public static final String EVENT_TYPE_ASPECT_ADDED = "aspectAdded";
    public static final String EVENT_TYPE_ASPECT_REMOVED = "aspectRemoved";
    public static final String EVENT_TYPE_PROPERTIES_CHANGED = "propertiesChanged";
    public static final String EVENT_TYPE_NODE_ASSOC_ADDED = "assocAdded";
    public static final String EVENT_TYPE_NODE_ASSOC_REMOVED = "assocRemoved";
    public static final String EVENT_TYPE_CHILD_ASSOC_ADDED = "childAssocAdded";
    public static final String EVENT_TYPE_CHILD_ASSOC_REMOVED = "childAssocRemoved";
    
    /**
     * @return Returns the automatically-assigned
     */
    public Long getId();
    
    /**
     * Mandatory property
     * 
     * @return Returns the unique ID of the transaction to which this event belongs
     */
    public String getTransactionId();
    
    /**
     * @param txnId the unique ID of the transaction to which this event belongs
     */
    public void setTransactionId(String txnId);
    
    /**
     * Mandatory property
     * 
     * @return the type of the event
     */
    public String getEventType();
    
    /**
     * @param eventType the type of the event
     */
    public void setEventType(String eventType);
    
    /**
     * Represents the primary node involved in the event.  For example, during a deletion,
     * the child node will be the primary node as it is being deleted.  The secondary node
     * will be the parent node.
     * <p>
     * Mandatory property
     * 
     * @return Returns the primary node involved
     */
    public String getPrimaryNodeRef();
    
    /**
     * @param nodeRef the primary node involved
     */
    public void setPrimaryNodeRef(String nodeRef);

    /**
     * @return Returns the secondary or child node involved
     * 
     * @see #getPrimaryNodeRef()
     */
    public String getSecondaryNodeRef();
    
    /**
     * @param nodeRef the secondary of child node involved
     */
    public void setSecondaryNodeRef(String nodeRef);
    
    /**
     * @return Returns the type name of the aspect involved 
     */
    public String getAspectTypeQName();
    
    /**
     * @param qname the type name of the aspect involved
     */
    public void setAspectTypeQName(String qname);

    /**
     * @return Returns the type name of the association
     */
    public String getAssocTypeQName();

    /**
     * @param assocTypeQName the type name of the association
     */
    public void setAssocTypeQName(String assocTypeQName);

    /**
     * @return Returns the association name
     */
    public String getAssocQName();

    /**
     * @param assocQName the association name
     */
    public void setAssocQName(String assocQName);
    
    /**
     * @return Returns a call stack trace of the stack leading up to this event
     */
    public StackTraceElement[] getTrace();
    
    /**
     * @param trace a stack trace of the call stack leading up to this event
     */
    public void setTrace(StackTraceElement[] trace);
}
