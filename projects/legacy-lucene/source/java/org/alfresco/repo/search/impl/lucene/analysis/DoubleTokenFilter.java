package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

import org.alfresco.util.NumericEncoder;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * Simple tokeniser for doubles.
 * 
 * @author Andy Hind
 */
public class DoubleTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;
    
    public DoubleTokenFilter(Reader in)
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
        Token candidate;
        while((candidate = baseTokeniser.next()) != null)
        {
            try
            {
                Double d = Double.valueOf(candidate.termText());
                String valueString = NumericEncoder.encode(d.doubleValue());
                Token doubleToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(),
                        candidate.type());
                return doubleToken;
            }
            catch (NumberFormatException e)
            {
                // just ignore and try the next one
            }
        }
        return null;
    }
}