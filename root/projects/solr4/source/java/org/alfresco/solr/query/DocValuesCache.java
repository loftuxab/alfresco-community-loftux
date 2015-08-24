/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.solr.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Map;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;


/**
 * The DocValuesCache is an in-memory uncompressed numeric DocValues cache. It is designed to provide the fastest
 * possible access to numeric docValues. The DocValuesCache can be used instead of the Direct DocValues format which also
 * provides uncompressed in-memory docValues. The DocValuesCache can be used in situations when it is not
 * practical to re-index to use Direct docValues.
 **/

public class DocValuesCache
{
    private static Map<String, WeakHashMap<Object, int[]>> intCache = new HashMap();

    public static synchronized int[] getIntValues(String field, AtomicReader reader) throws IOException
    {
        WeakHashMap<Object, int[]> fieldCache = intCache.get(field);

        if(fieldCache == null)
        {
            fieldCache = new WeakHashMap();
            intCache.put(field, fieldCache);
        }

        Object cacheKey = reader.getCoreCacheKey();
        int[] intValues = fieldCache.get(cacheKey);

        if(intValues == null)
        {
            NumericDocValues fieldValues = reader.getNumericDocValues(field);
            if(fieldValues == null)
            {
                return null;
            }
            else
            {
                int maxDoc = reader.maxDoc();
                intValues = new int[maxDoc];
                for(int i=0; i<maxDoc; i++)
                {
                    intValues[i] = (int)fieldValues.get(i);
                }
                fieldCache.put(cacheKey, intValues);
                return intValues;
            }
        }
        else
        {
            return intValues;
        }
    }
}