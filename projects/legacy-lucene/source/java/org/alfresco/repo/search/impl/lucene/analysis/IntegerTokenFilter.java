package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

import org.alfresco.util.NumericEncoder;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * Simple tokeniser for integers.
 * 
 * @author Andy Hind
 */
public class IntegerTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;
    
    public IntegerTokenFilter(Reader in)
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
                Integer integer = Integer.valueOf(candidate.termText());
                String valueString = NumericEncoder.encode(integer.intValue());
                Token integerToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(),
                        candidate.type());
                return integerToken;
            }
            catch (NumberFormatException e)
            {
                // just ignore and try the next one
            }
        }
        return null;
    }
}