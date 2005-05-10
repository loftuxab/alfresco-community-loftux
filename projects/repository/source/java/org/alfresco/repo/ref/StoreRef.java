package org.alfresco.repo.ref;

import java.io.Serializable;

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