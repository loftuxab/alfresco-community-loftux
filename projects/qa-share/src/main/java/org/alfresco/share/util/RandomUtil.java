/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.share.util;

import java.util.*;

/**
 * @author Aliaksei Boole
 */
public class RandomUtil
{
    private static final Random RANDOM = new Random();

    public static String getRandomString(int length)
    {
        char from[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            result.append(from[RANDOM.nextInt((from.length - 1))]);
        }
        return result.toString();
    }

    public static List<String> getRandomListString(int arrayLength, int stringsLength)
    {
        List<String> randomStrings = new ArrayList<String>();
        for (int i = 0; i < arrayLength; i++)
        {
            randomStrings.add(getRandomString(stringsLength));
        }
        return randomStrings;
    }

    public static int getInt(int bound)
    {
        return RANDOM.nextInt(bound);
    }


}
