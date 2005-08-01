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
package org.alfresco.repo.node.integrity;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Stores information for all events in the system
 * 
 * @author Derek Hulley
 */
public class IntegrityEvent
{
    /**
     * The type of integrity event 
     */
    public static enum EventType
    {
        NODE_CREATED,
        NODE_DELETED,
        ASPECT_ADDED,
        ASPECT_REMOVED,
        PROPERTIES_CHANGED,
        NODE_ASSOC_ADDED,
        NODE_ASSOC_REMOVED,
        CHILD_ASSOC_ADDED,
        CHILD_ASSOC_REMOVED;
    }
    
    private EventType eventType;
    private NodeRef primaryNodeRef;
    private NodeRef secondaryNodeRef;
    private QName aspectTypeQName;
    private QName assocTypeQName;
    private QName assocQName;
    private StackTraceElement[] trace;

    /**
     * @param eventType the type of the event
     * @param primaryNodeRef the node that the event primarily applies to
     */
    public IntegrityEvent(EventType eventType, NodeRef primaryNodeRef)
    {
        this.eventType = eventType;
        this.primaryNodeRef = primaryNodeRef;
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public NodeRef getPrimaryNodeRef()
    {
        return primaryNodeRef;
    }

    public NodeRef getSecondaryNodeRef()
    {
        return secondaryNodeRef;
    }

    public void setSecondaryNodeRef(NodeRef secondaryNodeRef)
    {
        this.secondaryNodeRef = secondaryNodeRef;
    }

    public QName getAspectTypeQName()
    {
        return aspectTypeQName;
    }

    public void setAspectTypeQName(QName aspectTypeQName)
    {
        this.aspectTypeQName = aspectTypeQName;
    }

    public QName getAssocTypeQName()
    {
        return assocTypeQName;
    }

    public void setAssocTypeQName(QName assocTypeQName)
    {
        this.assocTypeQName = assocTypeQName;
    }

    public QName getAssocQName()
    {
        return assocQName;
    }

    public void setAssocQName(QName assocQName)
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
