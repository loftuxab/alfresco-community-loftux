/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.tools;

/**
 * Used for iterating over Array objects.
 * 
 * @author muzquiano
 */
public class ArrayIterator
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2345461358406531996L;

    /** The array. */
    Array theArray;
    
    /** The index. */
    int theIndex;
    
    /** The initial index. */
    int initialIndex;

    /**
     * Instantiates a new array iterator.
     */
    public ArrayIterator()
    {
        initialIndex = 0;
    }

    /**
     * Instantiates a new array iterator.
     * 
     * @param it
     *            the it
     */
    public ArrayIterator(ArrayIterator it)
    {
        theArray = it.theArray;
        theIndex = it.theIndex;
        initialIndex = theIndex;
    }

    /**
     * Instantiates a new array iterator.
     * 
     * @param vector
     *            the vector
     * @param index
     *            the index
     */
    public ArrayIterator(Array vector, int index)
    {
        theArray = vector;
        theIndex = index;
        initialIndex = theIndex;
    }

    /**
     * Reset.
     */
    public void reset()
    {
        theIndex = initialIndex;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        return new ArrayIterator(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        return object instanceof ArrayIterator && equals((ArrayIterator) object);
    }

    /**
     * Equals.
     * 
     * @param iterator
     *            the iterator
     * 
     * @return true, if successful
     */
    public boolean equals(ArrayIterator iterator)
    {
        return iterator.theIndex == theIndex && iterator.theArray == theArray;
    }

    /**
     * Gets the.
     * 
     * @param offset
     *            the offset
     * 
     * @return the object
     */
    public Object get(int offset)
    {
        return theArray.get(theIndex + offset);
    }

    /**
     * Put.
     * 
     * @param offset
     *            the offset
     * @param object
     *            the object
     */
    public void put(int offset, Object object)
    {
        theArray.set(theIndex + offset, object);
    }

    /**
     * At begin.
     * 
     * @return true, if successful
     */
    public boolean atBegin()
    {
        return theIndex == 0;
    }

    /**
     * At end.
     * 
     * @return true, if successful
     */
    public boolean atEnd()
    {
        return theIndex == theArray.size();
    }

    /**
     * Checks for more elements.
     * 
     * @return true, if successful
     */
    public boolean hasMoreElements()
    {
        return theIndex < theArray.size();
    }

    /**
     * Advance.
     */
    public void advance()
    {
        ++theIndex;
    }

    /**
     * Advance.
     * 
     * @param n
     *            the n
     */
    public void advance(int n)
    {
        theIndex += n;
    }

    /**
     * Retreat.
     */
    public void retreat()
    {
        --theIndex;
    }

    /**
     * Retreat.
     * 
     * @param n
     *            the n
     */
    public void retreat(int n)
    {
        theIndex -= n;
    }

    /**
     * Next element.
     * 
     * @return the object
     */
    public Object nextElement()
    {
        return theArray.get(theIndex++);
    }

    /**
     * Gets the.
     * 
     * @return the object
     */
    public Object get()
    {
        return theArray.get(theIndex);
    }

    /**
     * Put.
     * 
     * @param object
     *            the object
     */
    public void put(Object object)
    {
        theArray.set(theIndex, object);
    }

    /**
     * Index.
     * 
     * @return the int
     */
    public int index()
    {
        return theIndex;
    }

}
