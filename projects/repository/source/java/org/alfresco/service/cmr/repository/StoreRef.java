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
package org.alfresco.service.cmr.repository;

import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Reference to a node store
 * 
 * @author Derek Hulley
 */
public final class StoreRef implements EntityRef, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 3905808565129394486L;

    public static final String PROTOCOL_WORKSPACE = "workspace";
    
    private static final String URI_FILLER = "://";

    private String protocol;
    private String identifier;

    /**
     * @param protocol
     *            well-known protocol for the store, e.g. <b>workspace</b> or
     *            <b>versionstore</b>
     * @param identifier
     *            the identifier, which may be specific to the protocol
     */
    public StoreRef(String protocol, String identifier)
    {
        if (protocol == null)
        {
            throw new IllegalArgumentException("Store protocol may not be null");
        }
        if (identifier == null)
        {
            throw new IllegalArgumentException("Store identifier may not be null");
        }

        this.protocol = protocol;
        this.identifier = identifier;
    }

    public StoreRef(String string)
    {
        int dividerPatternPosition = string.indexOf("://");
        if(dividerPatternPosition == -1)
        {
            throw new AlfrescoRuntimeException("Invalid store ref: Does not contain //:" + string);
        }
        this.protocol = string.substring(0, dividerPatternPosition);
        this.identifier = string.substring(dividerPatternPosition+3);
    }

    public String toString()
    {
        return protocol + URI_FILLER + identifier;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof StoreRef)
        {
            StoreRef that = (StoreRef) obj;
            return (this.protocol.equals(that.protocol)
                    && this.identifier.equals(that.identifier));
        } else
        {
            return false;
        }
    }
    
    /**
     * Creates a hashcode from both the {@link #getProtocol()} and {@link #getIdentifier()}
     */
    public int hashCode()
    {
        return (protocol.hashCode() + identifier.hashCode());
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getIdentifier()
    {
        return identifier;
    }
}