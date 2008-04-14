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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;

/**
 * Information on the Tag Library;
 * this class is instantiated from the Tag Library Descriptor file (TLD).
 * 
 * @author muzquiano
 */
public class TagLibraryInfoImpl extends TagLibraryInfo
{
    /**
     * Constructor
     *
     * This will invoke the constructors for TagInfo, and TagAttributeInfo
     * after parsing the TLD file.
     *
     * @param prefix the prefix actually used by the taglib directive
     * @param uri the URI actually used by the taglib directive
     */

    protected TagLibraryInfoImpl(String prefix, String uri, String tlibversion,
            String jspversion, String shortname, String info)
    {
        super(prefix, uri);
        this.tlibversion = tlibversion;
        this.jspversion = jspversion;
        this.shortname = shortname;
        this.info = info;
        tagHash = new Hashtable();
        tagCount = 0;
    }

    protected synchronized void putTag(TagInfo tag)
    {
        // make sure we don't count a tag twice
        if (!tagHash.containsKey(tag.getTagName()))
            tagCount++;
        tagHash.put(tag.getTagName(), tag);
        tags = null;
    }

    public TagInfo[] getTags()
    {
        if (tags != null)
            return tags;
        tags = new TagInfo[tagCount];
        Enumeration en = tagHash.elements();
        int i = 0;
        while (en.hasMoreElements())
        {
            tags[i++] = (TagInfo) en.nextElement();
        }
        return tags;
    }

    /**
     * Get the TagInfo for a given tag name
     */

    public TagInfo getTag(String shortname)
    {
        return (TagInfo) tagHash.get(shortname);
    }

    // Protected fields
    protected Hashtable tagHash;
    protected int tagCount;
}
