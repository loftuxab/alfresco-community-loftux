package org.alfresco.repo.search.impl.lucene;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Unit tests for {@link LuceneUtils}.
 * 
 * @author Neil Mc Erlean
 * @since 4.0
 */
public class LuceneUtilsTest
{
    @Test public void convertSimpleDate() throws Exception
    {
        Calendar cal = Calendar.getInstance();
        
        // November 12th, 1955. 10:04 pm exactly. :)
        final int year = 1955;
        final int month = 10; // 0-based
        final int day = 12;
        final int hours = 22;
        final int minutes = 04;
        final int seconds = 00;
        cal.set(year, month, day, hours, minutes, seconds);
        
        Date testDate = cal.getTime();
        
        String dateString = LuceneUtils.getLuceneDateString(testDate);
        final String expectedString = "1955\\-11\\-12T22:04:00";
        
        assertEquals("Incorrect data string.", expectedString, dateString);
    }
}
