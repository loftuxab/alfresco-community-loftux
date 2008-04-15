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
package org.alfresco.web.site.parser.tags;

import org.alfresco.tools.ArrayIterator;

/**
 * @author muzquiano
 */
public class TokenStreamReader
{
    protected TokenStreamReader(TokenStream tokens)
    {
        this.tokens = tokens;
        iter = tokens.getIterator();
        marks = new int[64];
        markindex = 0;
        index = 0;
    }

    public void reset()
    {
        int old = index;
        if (markindex == 0)
            index = 0;
        else
            index = marks[--markindex];
        //iter.reset();
        iter.retreat(old - index);
    }

    public void mark()
    {
        if (markindex == marks.length)
        {
            int newarray[] = new int[marks.length * 2];
            System.arraycopy(marks, 0, newarray, 0, marks.length);
            marks = newarray;
        }
        marks[markindex++] = index;
    }

    public void popMark()
    {
        if (markindex > 0)
            markindex--;
    }

    public Token nextToken()
    {
        index++;
        return (Token) iter.nextElement();
    }

    public boolean hasMoreTokens()
    {
        return iter.hasMoreElements();
    }

    private TokenStream tokens;
    private ArrayIterator iter;
    private int marks[];
    private int markindex;
    private int index;
}
