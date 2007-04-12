/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have recieved a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    NamingContextBindingsEnumeration.java
*----------------------------------------------------------------------------*/

package org.alfresco.jndi;

import org.apache.naming.NamingEntry;
import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Naming enumeration implementation.
 *
 * @author Remy Maucherat
 * @version $Revision: 366304 $ $Date: 2006-01-05 16:42:29 -0500 (Thu, 05 Jan 2006) $
 */

public class NamingContextBindingsEnumeration 
    implements NamingEnumeration {


    // ----------------------------------------------------------- Constructors


    public NamingContextBindingsEnumeration(Iterator entries, Context ctx) {
    	iterator = entries;
        this.ctx = ctx;
    }

    // -------------------------------------------------------------- Variables


    /**
     * Underlying enumeration.
     */
    protected Iterator iterator;

    
    /**
     * The context for which this enumeration is being generated.
     */
    private Context ctx;


    // --------------------------------------------------------- Public Methods


    /**
     * Retrieves the next element in the enumeration.
     */
    public Object next()
        throws NamingException {
        return nextElementInternal();
    }


    /**
     * Determines whether there are any more elements in the enumeration.
     */
    public boolean hasMore()
        throws NamingException {
        return iterator.hasNext();
    }


    /**
     * Closes this enumeration.
     */
    public void close()
        throws NamingException {
    }


    public boolean hasMoreElements() {
        return iterator.hasNext();
    }


    public Object nextElement() {


        try {
            return nextElementInternal();
        } catch (NamingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private Object nextElementInternal() throws NamingException {

        NamingEntry entry = (NamingEntry) iterator.next();
        
        // If the entry is a reference, resolve it
        if (entry.type == NamingEntry.REFERENCE
                || entry.type == NamingEntry.LINK_REF) {
            try {
                // A lookup will resolve the entry
                ctx.lookup(new CompositeName(entry.name));
            } catch (NamingException e) {
                throw e;
            } catch (Exception e) {
                NamingException ne = new NamingException(e.getMessage());
                ne.initCause(e);
                throw ne;
            }
        }
        
        return new Binding(entry.name, entry.value.getClass().getName(), 
                           entry.value, true);
    }


}

