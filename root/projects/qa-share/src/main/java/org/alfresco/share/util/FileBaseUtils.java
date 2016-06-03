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

import java.io.File;
import java.util.Date;

/**
 * This file will handle actions related to Files, directories
 * 
 * @author Paul Brodner
 */
public class FileBaseUtils
{
    /**
     * Create the full path of the File
     * 
     * @param paths
     * @return String
     */
    public static String combinePaths(String... paths)
    {
        if (paths.length == 0)
        {
            return "";
        }

        File combined = new File(paths[0]);

        int i = 1;
        while (i < paths.length)
        {
            combined = new File(combined, paths[i]);
            ++i;
        }

        return combined.getPath();
    }

    /**
     * Wait for file
     * 
     * @param file
     */
    public static boolean waitForFile(File file)
    {
        int cnt = 0;
        while (!file.exists() && cnt < 10)
        {
            pause(1);
            cnt++;
        }
        return file.exists();
    }

    /**
     * Just pause, without threads
     * 
     * @param seconds
     */
    public static void pause(int seconds)
    {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000)
        {
            end = new Date();
        }
    }
}
