package org.alfresco.deployment.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Set;

import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.DirectoryMetaData;

/**
 * Class for viewing meta data
 *
 * Usage MetaTool [filename]
 */
public class MetaTool {
	
		public static void main(String args[]) {
			
			try {
				
			if(args.length != 1)
			{
				System.out.println("Usage: MetaTool filename");
			}
				
			DirectoryMetaData meta = getDirectory(args[0]);
			
			Set<FileDescriptor> metaList = meta.getListing();
			
			for(FileDescriptor file : metaList)
			{	
				System.out.println("fileDescriptor : " + file);
			}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		
		}
		
	    /**
	     * Utility routine to get a metadata object.
	     * @param path
	     * @return
	     */
	    private static DirectoryMetaData getDirectory(String path)
	    {
	        try
	        {
	            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
	            try {
	            	DirectoryMetaData md = (DirectoryMetaData)in.readObject();
	            	return md;
	            }
	            finally
	            {
	            	in.close();
	            }
	        }
	        catch (IOException ioe)
	        {
	            throw new DeploymentException("Could not read metadata file " + path, ioe);
	        }
	        catch (ClassNotFoundException nfe)
	        {
	            throw new DeploymentException("Configuration error: could not instantiate DirectoryMetaData.");
	        }
	    }
}
