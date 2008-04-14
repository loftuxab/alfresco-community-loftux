/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
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
    private static final long serialVersionUID = 2345461358406531996L;

    Array theArray;
    int theIndex;
    int initialIndex;

    public ArrayIterator()
    {
        initialIndex = 0;
    }

    public ArrayIterator(ArrayIterator it)
    {
        theArray = it.theArray;
        theIndex = it.theIndex;
        initialIndex = theIndex;
    }

    public ArrayIterator(Array vector, int index)
    {
        theArray = vector;
        theIndex = index;
        initialIndex = theIndex;
    }

    public void reset()
    {
        theIndex = initialIndex;
    }

    public Object clone()
    {
        return new ArrayIterator(this);
    }

    public boolean equals(Object object)
    {
        return object instanceof ArrayIterator && equals((ArrayIterator) object);
    }

    public boolean equals(ArrayIterator iterator)
    {
        return iterator.theIndex == theIndex && iterator.theArray == theArray;
    }

    public Object get(int offset)
    {
        return theArray.get(theIndex + offset);
    }

    public void put(int offset, Object object)
    {
        theArray.set(theIndex + offset, object);
    }

    public boolean atBegin()
    {
        return theIndex == 0;
    }

    public boolean atEnd()
    {
        return theIndex == theArray.size();
    }

    public boolean hasMoreElements()
    {
        return theIndex < theArray.size();
    }

    public void advance()
    {
        ++theIndex;
    }

    public void advance(int n)
    {
        theIndex += n;
    }

    public void retreat()
    {
        --theIndex;
    }

    public void retreat(int n)
    {
        theIndex -= n;
    }

    public Object nextElement()
    {
        return theArray.get(theIndex++);
    }

    public Object get()
    {
        return theArray.get(theIndex);
    }

    public void put(Object object)
    {
        theArray.set(theIndex, object);
    }

    public int index()
    {
        return theIndex;
    }

}
