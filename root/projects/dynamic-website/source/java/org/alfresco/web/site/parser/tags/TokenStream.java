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

import org.alfresco.tools.Array;
import org.alfresco.tools.ArrayIterator;

/**
 * The tokenstream class is a wrapper around an array that can return 
 * a "reader" for the token stream.  It is the persisted representation
 * of a tokenized page.
 * 
 * @author muzquiano
 */
public class TokenStream implements java.io.Serializable
{
    private static final long serialVersionUID = 1142161358406531996L;

    public TokenStream()
    {
        tokens = new Array();
    }

    public void clear()
    {
        tokens.clear();
    }

    public void putToken(Token tok)
    {
        tokens.add(tok);
    }

    public Token getToken(int index)
    {
        return (Token) tokens.get(index);
    }

    public ArrayIterator getIterator()
    {
        ArrayIterator arrayIterator = tokens.begin();
        return arrayIterator;
    }

    public int size()
    {
        return tokens.size();
    }

    public TokenStreamReader getTokenStreamReader()
    {
        return new TokenStreamReader(this);
    }

    // allow this token stream to be associated with a seqID in the cache
    public void setSeqID(int id)
    {
        seqID = id;
    }

    public int getSeqID()
    {
        return seqID;
    }

    private Array tokens;
    private int seqID;
}
