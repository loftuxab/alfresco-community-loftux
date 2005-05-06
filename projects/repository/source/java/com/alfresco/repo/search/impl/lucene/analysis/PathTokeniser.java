/*
 * Created on Mar 16, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

/**
 * @author andyh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PathTokeniser extends CharTokenizer
{
    public PathTokeniser(Reader in)
    {
        super(in);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.CharTokenizer#isTokenChar(char)
     */
    protected boolean isTokenChar(char c)
    {
        return (c != '/') && !Character.isWhitespace(c);
    }

}
