/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.alfresco.web.scripts.JaxRSUriIndex.IndexEntry;
import org.alfresco.web.scripts.JaxRSUriIndex.UriTemplate;


/**
 * Test Jax-RS Uri Template
 * 
 * @author davidc
 */
public class JaxRSUriIndexTest extends TestCase
{

    public void testInvalidTemplate()
    {
        try
        {
            new UriTemplate(null);
            fail("Failed to catch null template");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("");
            fail("Failed to catch empty template");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("//");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/a//");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/a//b");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/{1a}");
            fail("Failed to catch var name beginning with number");
        }
        catch(WebScriptException e) {};
    }
 
    
    public void testParseTemplate()
    {
        UriTemplate i1 = new UriTemplate("/");
        assertEquals("/", i1.getTemplate());
        assertEquals("/", i1.getRegex().pattern());
        assertEquals(1, i1.getStaticCharacterCount());
        assertEquals(0, i1.getVariableNames().length);

        UriTemplate i2 = new UriTemplate("/a/{a1}/b{b1}b");
        assertEquals("/a/{a1}/b{b1}b", i2.getTemplate());
        assertEquals("/a/(.*?)/b(.*?)b", i2.getRegex().pattern());
        assertEquals(6, i2.getStaticCharacterCount());
        assertEquals(2, i2.getVariableNames().length);
        assertEquals("a1", i2.getVariableNames()[0]);
        assertEquals("b1", i2.getVariableNames()[1]);
    }
 

    public void testTemplateMatch()
    {
        UriTemplate i1 = new UriTemplate("/a/{a1}/b/b{b1}b");
        assertNull(i1.match("/"));
        assertNull(i1.match("/a"));
        assertNull(i1.match("/a/1/b"));
        assertNull(i1.match("/a/1/b/2"));
        assertNull(i1.match("/a/1/b/b2"));
        assertNull(i1.match("/a/1/b/b2b/"));
        
        Map<String, String> values1 = i1.match("/a/1/b/b2b");
        assertNotNull(values1);
        assertEquals(2, values1.size());
        assertEquals("1", values1.get("a1"));
        assertEquals("2", values1.get("b1"));

        UriTemplate i2 = new UriTemplate("/a/{a1}/b/{b1}");
        Map<String, String> values2 = i2.match("/a/1/b/2/3");
        assertNotNull(values2);
        assertEquals(2, values2.size());
        assertEquals("1", values2.get("a1"));
        assertEquals("2/3", values2.get("b1"));

        UriTemplate i3 = new UriTemplate("/a/{a1}/b/{a1}");
        Map<String, String> values3 = i3.match("/a/1/b/2");
        assertNull(values3);
    }
    
    
    public void testIndexSort()
    {
        IndexEntry i1 = new IndexEntry("GET", new UriTemplate("/"), false, null);
        IndexEntry i2 = new IndexEntry("POST", new UriTemplate("/a/{a}"), false, null);
        IndexEntry i3 = new IndexEntry("get", new UriTemplate("/a/{a}/b"), false, null);
        IndexEntry i4 = new IndexEntry("get", new UriTemplate("/a"), false, null);
        IndexEntry i5 = new IndexEntry("get", new UriTemplate("/c/d"), false, null);
        IndexEntry i6 = new IndexEntry("get", new UriTemplate("/c/d/{e}"), true, null);
        IndexEntry i7 = new IndexEntry("get", new UriTemplate("/a/b"), false, null);
        IndexEntry i8 = new IndexEntry("get", new UriTemplate("/c/d/{e}/{e}"), false, null);
        IndexEntry i9 = new IndexEntry("get", new UriTemplate("/e"), false, null);
        IndexEntry i10 = new IndexEntry("GET", new UriTemplate("/a/{a}"), false, null);

        Set<IndexEntry> index = new TreeSet<IndexEntry>(JaxRSUriIndex.COMPARATOR);
        index.add(i1);
        index.add(i2);
        index.add(i3);
        index.add(i4);
        index.add(i5);
        index.add(i6);
        index.add(i7);
        index.add(i8);
        index.add(i9);
        index.add(i10);

        IndexEntry[] sorted = new IndexEntry[index.size()];
        index.toArray(sorted);
        assertEquals(i1, sorted[9]);
        assertEquals(i4, sorted[8]);
        assertEquals(i9, sorted[7]);
        assertEquals(i10, sorted[6]);
        assertEquals(i2, sorted[5]);
        assertEquals(i7, sorted[4]);
        assertEquals(i5, sorted[3]);
        assertEquals(i3, sorted[2]);
        assertEquals(i6, sorted[1]);
        assertEquals(i8, sorted[0]);
    }
    
}
