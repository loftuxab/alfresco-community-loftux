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
package org.alfresco.abdera.ext.cmis;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.Entry;


/**
 * CMIS Version: 0.61
 *
 * CMIS Children for the Abdera ATOM library.
 * 
 * Encapsulates access to nested children..
 * 
 * @author davidc
 */
public class CMISChildren extends ElementWrapper /*implements Feed*/
{
    public CMISChildren(Element internal)
    {
        super(internal);
    }

    public CMISChildren(Factory factory)
    {
        super(factory, CMISConstants.CHILDREN);
    }

    /**
     * Gets count of child entries
     * 
     * @return
     */
    public int size()
    {
        return getEntries().size();
    }
    
    /**
     * Gets all entries of child feed
     * 
     * @return
     */
    public List<Entry> getEntries()
    {
        List<Element> elements = getElements();
        List<Entry> entries = new ArrayList<Entry>(elements.size());
        for (Element element : elements)
        {
            if (element instanceof Entry)
            {
                entries.add((Entry)element);
            }
        }
        return entries;
    }
    
    /**
     * Gets entry by id
     * 
     * @param id
     * @return  entry (or null, if not found)
     */
    public Entry getEntry(String id)
    {
        List<Element> elements = getElements();
        for (Element element : elements)
        {
            if (element instanceof Entry)
            {
                Entry entry = (Entry)element;
                IRI entryId = entry.getId();
                if (entryId != null && entryId.equals(new IRI(id)))
                {
                    return entry;
                }
            }
        }
        return null;
    }
    
}
