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
package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;

import org.alfresco.repo.search.ResultSetRowIterator;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetMetaData;
import org.alfresco.service.cmr.search.ResultSetRow;

public class RMFilteringResultSet implements ResultSet
{
    private ResultSet unfiltered;
    
    private BitSet inclusionMask;
    
    RMFilteringResultSet(ResultSet unfiltered, RMCaveatConfigImpl caveatConfigImpl)
    {
        this.unfiltered = unfiltered;
        inclusionMask = new BitSet(unfiltered.length());
        
        if (caveatConfigImpl != null)
        {
            for (int i = 0; i < this.unfiltered.length(); i++)
            {
                NodeRef nodeRef = this.unfiltered.getNodeRef(i);
                
                if (caveatConfigImpl.hasAccess(nodeRef))
                {
                    // TODO - optimise - do we need to check parent if child is not a record component ?
                    NodeRef parentNodeRef = this.unfiltered.getChildAssocRef(i).getParentRef();
                    if (caveatConfigImpl.hasAccess(parentNodeRef))
                    {
                        setIncluded(i, true);
                        continue;
                    }
                }
                
                // not accessible - caveat applies
                setIncluded(i, false);
            }
        }
        else
        {
            // TODO - warning ... should this return no results if no caveat config (ie. set inclusion to false) ?
            for (int i = 0; i < this.unfiltered.length(); i++)
            {
                setIncluded(i, false);
            }
        }
    }
    
    private void setIncluded(int i, boolean excluded)
    {
        inclusionMask.set(i, excluded);
    }
    
    public int length()
    {
        return inclusionMask.cardinality();
    }
    
    private int translateIndex(int n)
    {
        if (n > length())
        {
            throw new IndexOutOfBoundsException();
        }
        int count = -1;
        for (int i = 0, l = unfiltered.length(); i < l; i++)
        {
            if (inclusionMask.get(i))
            {
                count++;
            }
            if (count == n)
            {
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }
    
    public NodeRef getNodeRef(int n)
    {
        return unfiltered.getNodeRef(translateIndex(n));
    }
    public float getScore(int n)
    {
        return unfiltered.getScore(translateIndex(n));
    }
    
    public void close()
    {
        unfiltered.close();
    }
    
    public ResultSetRow getRow(int i)
    {
        return unfiltered.getRow(translateIndex(i));
    }
    
    public List<NodeRef> getNodeRefs()
    {
        List<NodeRef> answer = unfiltered.getNodeRefs();
        for (int i = unfiltered.length() - 1; i >= 0; i--)
        {
            if (!inclusionMask.get(i))
            {
                answer.remove(i);
            }
        }
        return answer;
    }
    
    public List<ChildAssociationRef> getChildAssocRefs()
    {
        List<ChildAssociationRef> answer = unfiltered.getChildAssocRefs();
        for (int i = unfiltered.length() - 1; i >= 0; i--)
        {
            if (!inclusionMask.get(i))
            {
                answer.remove(i);
            }
        }
        return answer;
    }
    
    public ChildAssociationRef getChildAssocRef(int n)
    {
        return unfiltered.getChildAssocRef(translateIndex(n));
    }
    
    public ListIterator<ResultSetRow> iterator()
    {
        return new RMFilteringIterator();
    }
    
    class RMFilteringIterator implements ResultSetRowIterator
    {
        // -1 at the start
        int underlyingPosition = -1;
        
        public boolean hasNext()
        {
            return inclusionMask.nextSetBit(underlyingPosition + 1) != -1;
        }
        
        public ResultSetRow next()
        {
            underlyingPosition = inclusionMask.nextSetBit(underlyingPosition + 1);
            if( underlyingPosition == -1)
            {
                throw new IllegalStateException();
            }
            return unfiltered.getRow(underlyingPosition);
        }
        
        public boolean hasPrevious()
        {
            if (underlyingPosition <= 0)
            {
                return false;
            }
            else
            {
                for (int i = underlyingPosition - 1; i >= 0; i--)
                {
                    if (inclusionMask.get(i))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        
        public ResultSetRow previous()
        {
            if (underlyingPosition <= 0)
            {
                throw new IllegalStateException();
            }
            for (int i = underlyingPosition - 1; i >= 0; i--)
            {
                if (inclusionMask.get(i))
                {
                    underlyingPosition = i;
                    return unfiltered.getRow(underlyingPosition);
                }
            }
            throw new IllegalStateException();
        }
        
        public int nextIndex()
        {
            return inclusionMask.nextSetBit(underlyingPosition+1);
        }
        
        public int previousIndex()
        {
            if (underlyingPosition <= 0)
            {
                return -1;
            }
            for (int i = underlyingPosition - 1; i >= 0; i--)
            {
                if (inclusionMask.get(i))
                {
                    return i;
                }
            }
            return -1;
        }
        
        /*
         * Mutation is not supported
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
        public void set(ResultSetRow o)
        {
            throw new UnsupportedOperationException();
        }
        
        public void add(ResultSetRow o)
        {
            throw new UnsupportedOperationException();
        }
        
        public boolean allowsReverse()
        {
            return true;
        }
        
        public ResultSet getResultSet()
        {
           return RMFilteringResultSet.this;
        }
    }
    
    public ResultSetMetaData getResultSetMetaData()
    {
        return unfiltered.getResultSetMetaData();
    }
    
    public void setResultSetMetaData(ResultSetMetaData resultSetMetaData)
    {
        throw new UnsupportedOperationException();
    }
    
    public int getStart()
    {
        throw new UnsupportedOperationException();
    }
    
    public boolean hasMore()
    {
        throw new UnsupportedOperationException();
    }
}
