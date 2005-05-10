/*
 * Created on Mar 16, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.repo.value.CachingDateFormat;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * @author andyh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DateTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;
    
    public DateTokenFilter(Reader in)
    {
        super(in);
        baseTokeniser = new WhitespaceTokenizer(in);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.TokenStream#next()
     */

    public Token next() throws IOException
    {
        SimpleDateFormat df = CachingDateFormat.getDateFormat();
        SimpleDateFormat dof = CachingDateFormat.getDateOnlyFormat();
        Token candidate;
        while((candidate = baseTokeniser.next()) != null)
        {
            Date date;
            try
            {
                date = df.parse(candidate.termText());
            }
            catch (ParseException e)
            {
               continue;
            }
            String valueString = dof.format(date);
            Token integerToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(),
                    candidate.type());
            return integerToken;
        }
        return null;
    }
}