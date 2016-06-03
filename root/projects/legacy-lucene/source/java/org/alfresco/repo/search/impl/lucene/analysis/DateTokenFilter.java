package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.alfresco.util.CachingDateFormat;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * @author andyh
 */
public class DateTokenFilter extends Tokenizer
{
    Tokenizer baseTokeniser;

    public DateTokenFilter(Reader in)
    {
        super(in);
        baseTokeniser = new WhitespaceTokenizer(in);
    }

    public Token next() throws IOException
    {
        SimpleDateFormat dof = CachingDateFormat.getDateOnlyFormat();
        Token candidate;
        while ((candidate = baseTokeniser.next()) != null)
        {
            Date date;
            if (candidate.termText().equalsIgnoreCase("now"))
            {
                date = new Date();
            }
            else if (candidate.termText().equalsIgnoreCase("today"))
            {
                date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                
            }
            else
            {
                try
                {
                    date = CachingDateFormat.lenientParse(candidate.termText(), Calendar.DAY_OF_MONTH).getFirst();
                }
                catch (ParseException e)
                {
                    continue;
                }
            }
            String valueString = dof.format(date);
            Token integerToken = new Token(valueString, candidate.startOffset(), candidate.startOffset(), candidate.type());
            return integerToken;
        }
        return null;
    }
}