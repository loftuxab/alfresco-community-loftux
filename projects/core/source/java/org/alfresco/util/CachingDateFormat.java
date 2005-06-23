/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides <b>thread safe</b> means of obtaining a cached date formatter.
 * <p>
 * The cached string-date mappings are stored in a <tt>WeakHashMap</tt>.
 * 
 * @see java.text.DateFormat#setLenient(boolean)
 * 
 * @author Derek Hulley
 */
public class CachingDateFormat extends SimpleDateFormat
{
    private static final long serialVersionUID = 3258415049197565235L;

    /** <pre> yyyy-MM-dd'T'HH:mm:ss </pre> */
    public static final String FORMAT_FULL_GENERIC = "yyyy-MM-dd'T'HH:mm:ss";

    /** <pre> yyyy-MM-dd </pre> */
    public static final String FORMAT_DATE_GENERIC = "yyyy-MM-dd";

    /** <pre> HH:mm:ss </pre> */
    public static final String FORMAT_TIME_GENERIC = "HH:mm:ss";

    private static ThreadLocal<SimpleDateFormat> s_localDateFormat = new ThreadLocal<SimpleDateFormat>();

    private static ThreadLocal<SimpleDateFormat> s_localDateOnlyFormat = new ThreadLocal<SimpleDateFormat>();

    private static ThreadLocal<SimpleDateFormat> s_localTimeOnlyFormat = new ThreadLocal<SimpleDateFormat>();

    transient private Map<String, Date> cacheDates = new WeakHashMap<String, Date>(89);

    private CachingDateFormat(String format)
    {
        super(format);
    }

    public String toString()
    {
        return this.toPattern();
    }

    /**
     * @param length
     *            the type of date format, e.g. {@link CachingDateFormat#LONG }
     * @param locale
     *            the <code>Locale</code> that will be used to determine the
     *            date pattern
     * 
     * @see #getDateFormat(String, boolean)
     * @see CachingDateFormat#SHORT
     * @see CachingDateFormat#MEDIUM
     * @see CachingDateFormat#LONG
     * @see CachingDateFormat#FULL
     */
    public static SimpleDateFormat getDateFormat(int length, Locale locale, boolean lenient)
    {
        SimpleDateFormat dateFormat = (SimpleDateFormat) CachingDateFormat.getDateInstance(length, locale);
        // extract the format string
        String pattern = dateFormat.toPattern();
        // we have a pattern to use
        return getDateFormat(pattern, lenient);
    }

    /**
     * @param dateLength
     *            the type of date format, e.g. {@link CachingDateFormat#LONG }
     * @param timeLength
     *            the type of time format, e.g. {@link CachingDateFormat#LONG }
     * @param locale
     *            the <code>Locale</code> that will be used to determine the
     *            date pattern
     * 
     * @see #getDateFormat(String, boolean)
     * @see CachingDateFormat#SHORT
     * @see CachingDateFormat#MEDIUM
     * @see CachingDateFormat#LONG
     * @see CachingDateFormat#FULL
     */
    public static SimpleDateFormat getDateTimeFormat(int dateLength, int timeLength, Locale locale, boolean lenient)
    {
        SimpleDateFormat dateFormat = (SimpleDateFormat) CachingDateFormat.getDateTimeInstance(dateLength, timeLength, locale);
        // extract the format string
        String pattern = dateFormat.toPattern();
        // we have a pattern to use
        return getDateFormat(pattern, lenient);
    }

    /**
     * @param pattern
     *            the conversion pattern to use
     * @param lenient
     *            true to allow the parser to extract the date in conceivable
     *            manner
     * @return Returns a conversion-cacheing formatter for the given pattern,
     *         but the instance itself is not cached
     */
    public static SimpleDateFormat getDateFormat(String pattern, boolean lenient)
    {
        // create an alfrescoDateFormat for cacheing purposes
        SimpleDateFormat dateFormat = new CachingDateFormat(pattern);
        // set leniency
        dateFormat.setLenient(lenient);
        // done
        return dateFormat;
    }

    /**
     * @return Returns a thread-safe formatter for the generic date/time format
     * 
     * @see #FORMAT_FULL_GENERIC
     */
    public static SimpleDateFormat getDateFormat()
    {
        if (s_localDateFormat.get() != null)
        {
            return s_localDateFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_FULL_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localDateFormat.set(formatter);
        // done
        return s_localDateFormat.get();
    }

    /**
     * @return Returns a thread-safe formatter for the generic date format
     * 
     * @see #FORMAT_DATE_GENERIC
     */
    public static SimpleDateFormat getDateOnlyFormat()
    {
        if (s_localDateOnlyFormat.get() != null)
        {
            return s_localDateOnlyFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_DATE_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localDateOnlyFormat.set(formatter);
        // done
        return s_localDateOnlyFormat.get();
    }

    /**
     * @return Returns a thread-safe formatter for the generic time format
     * 
     * @see #FORMAT_TIME_GENERIC
     */
    public static SimpleDateFormat getTimeOnlyFormat()
    {
        if (s_localTimeOnlyFormat.get() != null)
        {
            return s_localTimeOnlyFormat.get();
        }

        CachingDateFormat formatter = new CachingDateFormat(FORMAT_TIME_GENERIC);
        // it must be strict
        formatter.setLenient(false);
        // put this into the threadlocal object
        s_localTimeOnlyFormat.set(formatter);
        // done
        return s_localTimeOnlyFormat.get();
    }

    /**
     * Parses and caches date strings.
     * 
     * @see java.text.DateFormat#parse(java.lang.String,
     *      java.text.ParsePosition)
     */
    public Date parse(String text, ParsePosition pos)
    {
        Date cached = cacheDates.get(text);
        if (cached == null)
        {
            Date date = super.parse(text, pos);
            if ((date != null) && (pos.getIndex() == text.length()))
            {
                cacheDates.put(text, date);
                Date clonedDate = (Date) date.clone();
                return clonedDate;
            }
            else
            {
                return date;
            }
        }
        else
        {
            pos.setIndex(text.length());
            Date clonedDate = (Date) cached.clone();
            return clonedDate;
        }
    }
}
