/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImportStreamHandler;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;


/**
 * Import Tool.
 * 
 * @author David Caruana
 */
public class Import extends Tool
{
    /** Import Tool Context */
    private ImportContext context;
    
    
    /**
     * Entry Point
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Tool tool = new Import();
        tool.start(args);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.tools.Tool#processArgs(java.lang.String[])
     */
    @Override
    /*package*/ ToolContext processArgs(String[] args)
    {
        context = new ImportContext();
        context.setLogin(true);

        int i = 0;
        while (i < args.length)
        {
            if (args[i].equals("-h") || args[i].equals("-help"))
            {
                context.setHelp(true);
                break;
            }
            else if (args[i].equals("-s") || args[i].equals("-store"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <store> for the option -store must be specified");
                }
                context.storeRef = new StoreRef(args[i]);
            }
            else if (args[i].equals("-p") || args[i].equals("-path"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <path> for the option -path must be specified");
                }
                context.path = args[i];
            }
            else if (args[i].equals("-d") || args[i].equals("-dir"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <dir> for the option -dir must be specified");
                }
                context.sourceDir = args[i];
            }
            else if (args[i].equals("-user"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <user> for the option -user must be specified");
                }
                context.setUsername(args[i]);
            }
            else if (args[i].equals("-pwd"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <password> for the option -pwd must be specified");
                }
                context.setPassword(args[i]);
            }
            else if (args[i].equals("-encoding"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ToolException("The value <encoding> for the option -encoding must be specified");
                }
                context.encoding = args[i];
            }
            else if (args[i].equals("-quiet"))
            {
                context.setQuiet(true);
            }
            else if (args[i].equals("-verbose"))
            {
                context.setVerbose(true);
            }
            else if (i == (args.length - 1))
            {
                context.packageName = args[i];
            }
            else
            {
                throw new ToolException("Unknown option " + args[i]);
            }

            // next argument
            i++;
        }

        return context;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.tools.Tool#displayHelp()
     */
    @Override
    /*package*/ void displayHelp()
    {
        System.out.println("Usage: import -user username -s[tore] store [options] packagename");
        System.out.println("");
        System.out.println("username: username for login");
        System.out.println("store: the store to import into the form of scheme://store_name");
        System.out.println("packagename: the filename to import from (with or without extension)");
        System.out.println("");
        System.out.println("Options:");
        System.out.println(" -h[elp] display this help");
        System.out.println(" -p[ath] the path within the store to extract into (default: /)");
        System.out.println(" -d[ir] the source directory to import from (default: current directory)");
        System.out.println(" -pwd password for login");
        System.out.println(" -encoding package file encoding (default: " + Charset.defaultCharset() + ")");
        System.out.println(" -quiet do not display any messages during import");
        System.out.println(" -verbose report import progress");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.tools.Tool#getToolName()
     */
    @Override
    /*package*/ String getToolName()
    {
        return "Alfresco Repository Importer";
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.tools.Tool#execute()
     */
    @Override
    /*package*/ void execute() throws ToolException
    {
        ImporterService importer = getServiceRegistry().getImporterService();
        
        // Create Import package
        File packageFile = context.getPackageFile();
        log("Importing from package " + packageFile.getAbsolutePath());
        Reader viewReader = getPackageReader(packageFile, context.encoding);
        PackageStreamHandler streamHandler = new PackageStreamHandler(context.getSourceDir());

        // Export Repository content to export package
        importer.importView(viewReader, streamHandler, context.getLocation(), null, new ImportProgress());
    }
    
    /**
     * Get XML Export File
     * 
     * @param packageFile  the name of the file
     * @return the input stream to the file
     */
    private Reader getPackageReader(File packageFile, String encoding)
    {
        try
        {
            InputStream inputStream = new FileInputStream(packageFile);
            Reader inputReader = (encoding == null) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, encoding);
            return new BufferedReader(inputReader);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new ToolException("Encoding " + encoding + " is not supported");
        }
        catch(IOException e)
        {
            throw new ToolException("Failed to read package " + packageFile.getAbsolutePath() + " due to " + e.getMessage());
        }
    }
    
    /**
     * Handler for importing Repository content streams from file system
     * 
     * @author David Caruana
     */
    private class PackageStreamHandler
        implements ImportStreamHandler
    {
        private File sourceDir;

        /**
         * Constuct Handler
         * 
         * @param sourceDir  source directory
         * @param packageDir  relative directory within source to place exported content  
         */
        private PackageStreamHandler(File sourceDir)
        {
            this.sourceDir = sourceDir;
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImportStreamHandler#importStream(java.lang.String)
         */
        public InputStream importStream(String url)
        {
            File fileURL = new File(url);
            if (fileURL.isAbsolute() == false)
            {
                fileURL = new File(sourceDir, url);
            }
            
            try
            {
                return new FileInputStream(fileURL);
            }
            catch(IOException e)
            {
                throw new ToolException("Failed to read content url " + url + " from file " + fileURL.getAbsolutePath());
            }
        }
    }
    
    /**
     * Report Import Progress
     * 
     * @author David Caruana
     */
    private class ImportProgress
        implements ImporterProgress
    {
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterProgress#nodeCreated(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName)
         */
        public void nodeCreated(NodeRef nodeRef, NodeRef parentRef, QName assocName, QName childName)
        {
            logVerbose("Imported node " + nodeRef + " (parent=" + parentRef + ", childname=" + childName + ", association=" + assocName + ")");            
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterProgress#contentCreated(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
         */
        public void contentCreated(NodeRef nodeRef, String sourceUrl)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterProgress#propertySet(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.io.Serializable)
         */
        public void propertySet(NodeRef nodeRef, QName property, Serializable value)
        {
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterProgress#aspectAdded(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
         */
        public void aspectAdded(NodeRef nodeRef, QName aspect)
        {
        }
    }
    
    /**
     * Import Tool Context
     * 
     * @author David Caruana
     */
    private class ImportContext extends ToolContext
    {
        /** Store Reference to import into */
        private StoreRef storeRef;
        /** Path to import into */
        private String path;
        /** Source directory to import from */
        private String sourceDir;
        /** The package name to import */
        private String packageName;
        /** The package encoding */
        private String encoding = null;
        

        /* (non-Javadoc)
         * @see org.alfresco.tools.ToolContext#validate()
         */
        @Override
        /*package*/ void validate()
        {
            super.validate();
            
            if (storeRef == null)
            {
                throw new ToolException("Store to import into has not been specified.");
            }
            if (packageName == null)
            {
                throw new ToolException("Package name has not been specified.");
            }
            if (sourceDir != null)
            {
                File fileSourceDir = new File(sourceDir);
                if (fileSourceDir.exists() == false)
                {
                    throw new ToolException("Source directory " + fileSourceDir.getAbsolutePath() + " does not exist.");
                }
            }
            File packageFile = getPackageFile();
            if (packageFile.exists() == false)
            {
                throw new ToolException("Package file " + packageFile.getAbsolutePath() + " does not exist.");
            }
        }

        /**
         * Get the location within the Repository to import into
         * 
         * @return the location
         */
        private Location getLocation()
        {
            Location location = new Location(storeRef);
            location.setPath(path);
            return location;
        }
        
        /**
         * Get the source directory
         * 
         * @return the source directory (or null if current directory)
         */
        private File getSourceDir()
        {
            File dir = (sourceDir == null) ? null : new File(sourceDir); 
            return dir;
        }

        /**
         * Get the xml import file
         * 
         * @return the package file
         */
        private File getPackageFile()
        {
            String packageFile = (packageName.indexOf('.') != -1) ? packageName : packageName + ".xml";
            File file = new File(getSourceDir(), packageFile); 
            return file;
        }
    }
    
}
