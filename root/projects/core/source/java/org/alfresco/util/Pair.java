/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.ObjectInputStream.GetField;

/**
 * Utility class for containing two things that aren't like each other
 */
public final class Pair<F, S> implements Serializable
{
    @SuppressWarnings("unchecked")
    public static final Pair NULL_PAIR = new Pair(null, null);
    
    @SuppressWarnings("unchecked")
    public static final <X, Y> Pair<X, Y> nullPair()
    {
        return NULL_PAIR;
    }
    
    private static final long serialVersionUID = -7406248421185630612L;

    /**
     * The first member of the pair.
     */
    private F first;
    
    /**
     * The second member of the pair.
     */
    private S second;
    
    /**
     * Make a new one.
     * 
     * @param first The first member.
     * @param second The second member.
     */
    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Get the first member of the tuple.
     * @return The first member.
     */
    public F getFirst()
    {
        return first;
    }
    
    /**
     * Get the second member of the tuple.
     * @return The second member.
     */
    public S getSecond()
    {
        return second;
    }
    
    public void setFirst(F first)
    {
        this.first = first;
    }
    
    public void setSecond(S second)
    {
        this.second = second;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || !(other instanceof Pair<?, ?>))
        {
            return false;
        }
        Pair<?, ?> o = (Pair<?, ?>)other;
        return EqualsHelper.nullSafeEquals(this.first, o.first) &&
               EqualsHelper.nullSafeEquals(this.second, o.second);
    }
    
    @Override
    public int hashCode()
    {
        return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode());
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Ensure that previously-serialized instances don't fail due to the member name change.
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException
    {
        GetField fields = is.readFields();
        if (fields.defaulted("first"))
        {
            // This is a pre-V3.3
            this.first = (F) fields.get("fFirst", null);
            this.second = (S) fields.get("fSecond", null);
        }
        else
        {
            this.first = (F) fields.get("first", null);
            this.second = (S) fields.get("second", null);
        }
    }
}
