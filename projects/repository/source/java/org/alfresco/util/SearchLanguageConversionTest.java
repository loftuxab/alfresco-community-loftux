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
    
    public void testEscapeXPath()
    {
        String good = SearchLanguageConversion.escapeForXPath(BAD_STRING);
        assertEquals("Escaping for xpath failed",
                "\\\\ | ! \" £ " +
                "$ \\% ^ & * ( " +
                ") \\_ \\{ \\} [ ] " +
                "@ # ~ ' : ; " +
                ", \\. < > + ? " +
                "\\/ \\\\\\\\ \\\\* \\\\? \\\\\\_",
                good);
    }
    
    public void testEscapeRegex()
    {
        String good = SearchLanguageConversion.escapeForRegex(BAD_STRING);
        assertEquals("Escaping for regex failed",
                "\\\\ \\| ! \" £ " +
                "\\$ % \\^ & \\* \\( " +
                "\\) _ \\{ \\} [ ] " +
                "@ # ~ ' : ; " +
                ", \\. < > \\+ \\? " +
                "/ \\\\\\\\ \\\\\\* \\\\\\? \\\\_",
                good);
    }
    
    public void testConvertXPathLikeToRegex()
    {
        String good = SearchLanguageConversion.convertXPathLikeToRegex(BAD_STRING);
        assertEquals("XPath like to regex failed",
                "\\ \\| ! \" £ " +
                "\\$ .* \\^ & \\* \\( " +
                "\\) . \\{ \\} [ ] " +
                "@ # ~ ' : ; " +
                ", \\. < > \\+ \\? " +
                "/ \\\\ \\* \\? \\_",
                good);
    }
    
    public void testConvertXPathLikeToLucene()
    {
        String good = SearchLanguageConversion.convertXPathLikeToLucene(BAD_STRING);
        assertEquals("XPath like to regex failed",
                "\\ | \\! \\\" £ " +
                "$ * \\^ & \\* \\( " +
                "\\) ? \\{ \\} \\[ \\] " +
                "@ # \\~ ' \\: ; " +
                ", . < > \\+ \\? " +
                "/ \\\\ \\* \\? \\_",
                good);
    }
}
