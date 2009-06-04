package org.alfresco.deployment;

import java.io.File;

public class PathUtil 
{
	    /**
	     * Get a File object for the given path in this target.
	     * @param path
	     * @return
	     */
	   public static File getFileForPath(String rootDir, String path)
	   {
	        return new File(rootDir + normalizePath(path));
	    }
	    private static final String fgSeparatorReplacement;
	
	    static
	    {
	    	fgSeparatorReplacement = File.separator.equals("/") ? "/" : "\\\\";
	    }
	
	    /**
	     * Utility to normalize a path to platform specific form.
	     * @param path
	     * @return
	     */
	    private static String normalizePath(String path)
	    {
	        path = path.replaceAll("/+", fgSeparatorReplacement);
	        path = path.replace("/$", "");
	        if (!path.startsWith(File.separator))
	        {
	            path = File.separator + path;
	        }
	        return path;
	    }
}

