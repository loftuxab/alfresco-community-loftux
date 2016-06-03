package org.alfresco.util;

import junit.framework.TestCase;

/**
 * @see org.alfresco.util.SearchLanguageConversion
 * 
 * @author Derek Hulley
 */
public class SearchLanguageConversionTest extends TestCase
{
    /**
     * A string with a whole lod of badness to stress test with
     */
    private static final String BAD_STRING =
            "\\ | ! \" £ " +
            "$ % ^ & * ( " +
            ") _ { } [ ] " +
            "@ # ~ ' : ; " +
            ", . < > + ? " +
            "/ \\\\ \\* \\? \\_";
    
    public void testEscapeXPathLike()
    {
        String good = SearchLanguageConversion.escapeForXPathLike(BAD_STRING);
        assertEquals("Escaping for xpath failed",
                "\\\\ | ! \" £ " +
                "$ \\% ^ & * ( " +
                ") \\_ { } \\[ \\] " +
                "@ # ~ ' : ; " +
                ", . < > + ? " +
                "/ \\\\\\\\ \\\\* \\\\? \\\\\\_",
                good);
    }
    
    public void testEscapeRegex()
    {
        String good = SearchLanguageConversion.escapeForRegex(BAD_STRING);
        assertEquals("Escaping for regex failed",
                "\\\\ \\| ! \" £ " +
                "\\$ % \\^ & \\* \\( " +
                "\\) _ \\{ \\} \\[ \\] " +
                "@ # ~ ' : ; " +
                ", \\. < > \\+ \\? " +
                "/ \\\\\\\\ \\\\\\* \\\\\\? \\\\_",
                good);
    }
    
    public void testEscapeLucene()
    {
        String good = SearchLanguageConversion.escapeForLucene(BAD_STRING);
        assertEquals("Escaping for Lucene failed",
                "\\\\ \\| \\! \\\" £ " +
                "$ % \\^ \\& \\* \\( " +
                "\\) _ \\{ \\} \\[ \\] " +
                "@ # \\~ ' \\: ; " +
                ", . < > \\+ \\? " +
                "\\/ \\\\\\\\ \\\\\\* \\\\\\? \\\\_",
                good);
    }
    
    public void testConvertXPathLikeToRegex()
    {
        String good = SearchLanguageConversion.convertXPathLikeToRegex(BAD_STRING);
        assertEquals("XPath like to regex failed",
                "(?s) \\| ! \" £ " +
                "\\$ .* \\^ & \\* \\( " +
                "\\) . \\{ \\} \\[ \\] " +
                "@ # ~ ' : ; " +
                ", \\. < > \\+ \\? " +
                "/ \\\\ \\* \\? _",
                good);
    }
    
    public void testConvertXPathLikeToLucene()
    {
        String good = SearchLanguageConversion.convertXPathLikeToLucene(BAD_STRING);
        assertEquals("XPath like to Lucene failed",
                " \\| \\! \\\" £ " +
                "$ * \\^ \\& \\* \\( " +
                "\\) ? \\{ \\} \\[ \\] " +
                "@ # \\~ ' \\: ; " +
                ", . < > \\+ \\? " +
                "\\/ \\\\ \\* \\? _",
                good);
    }
    
    public void testSqlToLucene()
    {
        String sqlLike = "AB%_*?\\%\\_";
        String lucene = "AB*?\\*\\?%_";
        String converted = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_SQL_LIKE, SearchLanguageConversion.DEF_LUCENE, sqlLike);
        assertEquals(lucene, converted);
    }
    
    public void testLuceneToRegexp()
    {
        String lucene = "AB*?\\*\\?.*.";
        String regexp = "AB.*.\\*\\?\\..*\\.";
        String converted = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_LUCENE, SearchLanguageConversion.DEF_REGEX, lucene);
        assertEquals(regexp, converted);
    }
    
    public void testLuceneToSql()
    {
        String lucene = "%_";
        String sql = "\\%\\_";
        String converted = SearchLanguageConversion.convert(SearchLanguageConversion.DEF_LUCENE, SearchLanguageConversion.DEF_SQL_LIKE, lucene);
        assertEquals(sql, converted);
    }
    
    public void testTokenizeString()
    {
        String[] res = SearchLanguageConversion.tokenizeString("");
        assertTrue(res.length == 1);
        res = SearchLanguageConversion.tokenizeString("bob");
        assertTrue(res.length == 1);
        assertEquals("bob", res[0]);
        res = SearchLanguageConversion.tokenizeString("   bob   ");
        assertTrue(res.length == 1);
        assertEquals("bob", res[0]);
        res = SearchLanguageConversion.tokenizeString("   bob hope ");
        assertTrue(res.length == 2);
        assertEquals("bob", res[0]);
        assertEquals("hope", res[1]);
        res = SearchLanguageConversion.tokenizeString("   bob    hope ");
        assertTrue(res.length == 2);
        assertEquals("bob", res[0]);
        assertEquals("hope", res[1]);
        res = SearchLanguageConversion.tokenizeString("    bob  no    hope  ");
        assertTrue(res.length == 3);
        assertEquals("bob", res[0]);
        assertEquals("no", res[1]);
        assertEquals("hope", res[2]);
    }
}
