/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.cache;

import java.io.Serializable;

/**
 * Represents cache invalidation messages as sent/received by {@link InvalidatingCache}.
 * <p>
 * Messages are capable of invalidating a single key (including the null key) or an entire cache.
 * <p>
 * Keys must not only be Serializable, but MUST implement valid equals() and hashCode() methods.
 * 
 * @author Matt Ward
 */
public final class InvalidationMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static enum Scope { ALL_KEYS, SINGLE_KEY };
    private final Scope scope;
    private final Serializable key;
    
    /**
     * Private constructor. Use static factory methods for external object creation.
     * 
     * @param scope   Key scope, e.g. all keys or just a single key. Must not be null.
     * @param key     Serializable key, only for use when scope is a single key. May be null.
     */
    private InvalidationMessage(Scope scope, Serializable key)
    {
        if (scope == null)
        {
            throw new IllegalArgumentException("Scope cannot be null.");
        }
        this.scope = scope;
        this.key = key;
    }
    
    /**
     * Creates an invalidation message signifying that all keys are now invalid.
     * 
     * @return InvalidationMessage for all keys.
     */
    public static InvalidationMessage forAllKeys()
    {
        return new InvalidationMessage(Scope.ALL_KEYS, null);
    }
    
    /**
     * Creates an invalidation message signifying that the specified key is no longer valid.
     * 
     * @param key   The key to invalidate.
     * @return Invalidation message for the specified key.
     */
    public static InvalidationMessage forKey(Serializable key)
    {
        return new InvalidationMessage(Scope.SINGLE_KEY, key);
    }
    
    public boolean invalidatesAllKeys()
    {
        return scope == Scope.ALL_KEYS;
    }
    
    public boolean invalidatesSingleKey()
    {
        return scope == Scope.SINGLE_KEY;
    }
    
    public Serializable staleKey()
    {
        if (!invalidatesSingleKey())
        {
            throw new IllegalStateException(
                        "Attempt to retrieve stale key for invalidation message with scope " + scope);
        }
        return key;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.scope == null) ? 0 : this.scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        InvalidationMessage other = (InvalidationMessage) obj;
        if (this.key == null)
        {
            if (other.key != null) return false;
        }
        else if (!this.key.equals(other.key)) return false;
        if (this.scope != other.scope) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "InvalidationMessage [scope=" + this.scope + ", key=" + this.key + "]";
    }
}
