package org.alfresco.solr.query;

import static org.junit.Assert.*;

import org.alfresco.solr.query.AlfrescoLuceneQParserPlugin.AlfrescoLuceneQParser;
import org.junit.Before;
import org.junit.Test;


public class AlfrescoLuceneQParserTest
{
    private AlfrescoLuceneQParser parser;
    
    @Before
    public void setUp() throws Exception
    {
        parser = new AlfrescoLuceneQParser(null, null, null, null, null);
    }

    @Test
    public void forwardSlashesEscapedCorrectly()
    {
        assertEquals("Nothing to escape", "abcdef", parser.escape("abcdef"));
        assertEquals("Escape single slash", "abc\\/def", parser.escape("abc/def"));
        assertEquals("Do not escape quoted slash", "abc\"/\"def", parser.escape("abc\"/\"def"));
        assertEquals("Do not escape escaped slash", "abc\\/def", parser.escape("abc\\/def"));
        assertEquals("Do not escape quoted escaped slash", "abc\"\\/\"def", parser.escape("abc\"\\/\"def"));
        assertEquals("Escape multiple consecutive slashes", "abc\\/\\/def", parser.escape("abc//def"));
        assertEquals("abc\\/\\/\\/\\/\\/def\\/\\/\\/:\"bl/ah\"", parser.escape("abc/////def/\\//:\"bl/ah\""));
        
        // Test case from ACE-3071
        final String input = "(@\\{http\\://www.alfresco.org/model/imap/1.0\\}messageId:\"000000005E157E8B296B6B4DA1F00A30E4F21FE1C4002000\" OR @\\{http\\://www.westernacher.com/alfresco/models/wpsmail\\-v2\\}messageId:\"000000005E157E8B296B6B4DA1F00A30E4F21FE1C4002000\")";
        final String expected = "(@\\{http\\:\\/\\/www.alfresco.org\\/model\\/imap\\/1.0\\}messageId:\"000000005E157E8B296B6B4DA1F00A30E4F21FE1C4002000\" OR @\\{http\\:\\/\\/www.westernacher.com\\/alfresco\\/models\\/wpsmail\\-v2\\}messageId:\"000000005E157E8B296B6B4DA1F00A30E4F21FE1C4002000\")";
        assertEquals(expected, parser.escape(input));
    }
}
