package org.alfresco.repo.dictionary.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.util.CachingDateFormat;


/**
 * Support translating model from and to XML
 * 
 * @author David Caruana
 *
 */
public class M2XML
{
   
    /**
     * Convert XML date (of the form yyyy-MM-dd) to Date 
     * 
     * @param date  the xml representation of the date
     * @return  the date
     * @throws ParseException
     */
    public static Date deserialiseDate(String date)
        throws ParseException
    {
        Date xmlDate = null;
        if (date != null)
        {
            SimpleDateFormat df = CachingDateFormat.getDateOnlyFormat();
            xmlDate = df.parse(date);
        }
        return xmlDate;
    }

    
    /**
     * Convert date to XML date (of the form yyyy-MM-dd)
     * 
     * @param date  the date
     * @return  the xml representation of the date
     */
    public static String serialiseDate(Date date)
    {
        String xmlDate = null;
        if (date != null)
        {
            SimpleDateFormat df = CachingDateFormat.getDateOnlyFormat();
            xmlDate = df.format(date);
        }
        return xmlDate;
    }
    
}
