package org.alfresco.repo.dictionary.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.repo.value.CachingDateFormat;


public class M2XML
{
    
    public static Date deserialiseDate(String date)
        throws ParseException
    {
        SimpleDateFormat df = CachingDateFormat.getDateOnlyFormat();
        return df.parse(date);
    }

    public static String serialiseDate(Date date)
    {
        SimpleDateFormat df = CachingDateFormat.getDateOnlyFormat();
        return df.format(date);
    }
    
}
