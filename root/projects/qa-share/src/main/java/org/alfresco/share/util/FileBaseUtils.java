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
