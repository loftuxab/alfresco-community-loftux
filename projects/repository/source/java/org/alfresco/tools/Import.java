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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImportStreamHandler;
import org.alfresco.service.cmr.view.ImporterProgress;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;


/**
 * Alfresco Repository Import Tool
 * 
 * @author David Caruana
 */
public class Import
{

    /**
     * Entry Point
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            ImportContext context = processArgs(args);
    
            try
            {
                Import importer = new Import(context);
                importer.doImport();
                System.exit(0);
            }
            catch (ImportException e)
            {
                displayError(e);
                System.exit(-1);
            }
        }
        catch(ImportException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            displayHelp();
            System.exit(-1);
        }
        catch (Throwable e)
        {
            System.out.println("The following import error has occured:");
            displayError(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private static void displayError(Throwable e)
    {
        Throwable display = e;
        while(display != null)
        {
            System.out.println(display.getMessage());
            display = display.getCause();
        }
    }
    

    
    /** Import Context */
    private ImportContext context;
    

    /**
     * Constuct Export Tool
     * 
     * @param context  export context
     */
    private Import(ImportContext context)
    {
        this.context = context;
    }

    /**
     * Performs the Export
     */        
    private void doImport()
    {
        log("Alfresco Repository Importer");

        // Get handle onto Importer service
        ApplicationContext appcontext = ApplicationContextHelper.getApplicationContext();
        ServiceRegistry registry = (ServiceRegistry) appcontext.getBean(ServiceRegistry.SERVICE_REGISTRY);
        ImporterService importer = registry.getImporterService();
        
        // Create Import package
        File packageFile = context.getPackageFile();
        log("Importing from package " + packageFile.getAbsolutePath());
        InputStream input = getPackageFile(packageFile);
        PackageStreamHandler streamHandler = new PackageStreamHandler(context.getSourceDir());

        // Export Repository content to export package
        importer.importView(input, streamHandler, context.getLocation(), null, new ImportProgress());
                
        log("Import completed successfully.");
    }

    /**
     * Get XML Export File
     * 
     * @param packageFile  the name of the file
     * @return the input stream to the file
     */
    private InputStream getPackageFile(File packageFile)
    {
        try
        {
            return new FileInputStream(packageFile);
        }
        catch(IOException e)
        {
            throw new ImportException("Failed to get package file " + packageFile.getAbsolutePath() + " due to " + e.getMessage());
        }
    }

    /**
     * Log Export message
     * 
     * @param msg  message to log
     */
    private void log(String msg)
    {
        if (context.quiet == false)
        {
            System.out.println(msg);
        }
    }

    /**
     * Log Export message
     * 
     * @param msg  message to log
     */
    private void logVerbose(String msg)
    {
        if (context.verbose)
        {
            log(msg);
        }
    }
    
    /**
     * Process Export Tool command line arguments
     * 
     * @param args  the arguments
     * @return  the export context
     */
    private static ImportContext processArgs(String[] args)
    {
        ImportContext context = new ImportContext();

        int i = 0;
        while (i < args.length)
        {
            if (args[i].equals("-h") || args[i].equals("-help"))
            {
                displayHelp();
                System.exit(0);
            }
            else if (args[i].equals("-s") || args[i].equals("-store"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ImportException("The value <store> for the parameter -store must be specified");
                }
                context.storeRef = new StoreRef(args[i]);
            }
            else if (args[i].equals("-p") || args[i].equals("-path"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ImportException("The value <path> for the parameter -path must be specified");
                }
                context.path = args[i];
            }
            else if (args[i].equals("-d") || args[i].equals("-dir"))
            {
                i++;
                if (i == args.length || args[i].length() == 0)
                {
                    throw new ImportException("The value <dir> for the parameter -dir must be specified");
                }
                context.sourceDir = args[i];
            }
            else if (args[i].equals("-quiet"))
            {
                context.quiet = true;
            }
            else if (args[i].equals("-verbose"))
            {
                context.verbose = true;
            }
            else if (i == (args.length - 1))
            {
                context.packageName = args[i];
            }
            else
            {
                throw new ImportException("Unknown option " + args[i]);
            }

            // next argument
            i++;
        }

        context.validate();
        return context;
    }

    /**
     * Display Help
     */
    private static void displayHelp()
    {
        System.out.println("Usage: import -store store [options] packagename");
        System.out.println("");
        System.out.println("store: the store to import into the form of scheme://store_name");
        System.out.println("packagename: the filename to import from (with or without extension)");
        System.out.println("");
        System.out.println("Options:");
        System.out.println(" -h[elp] display this help");
        System.out.println(" -p[ath] the path within the store to extract into (default: /)");
        System.out.println(" -d[ir] the source directory to import from (default: current directory)");
        System.out.println(" -quiet do not display any messages during import");
        System.out.println(" -verbose report import progress");
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
                throw new ImportException("Failed to read content url " + url + " from file " + fileURL.getAbsolutePath());
            }
        }
    }
    
    
    /**
     * Import Tool Context
     * 
     * @author David Caruana
     */
    private static class ImportContext
    {
        /** Store Reference to import into */
        private StoreRef storeRef;
        /** Path to import into */
        private String path;
        /** Source directory to import from */
        private String sourceDir;
        /** The package name to import */
        private String packageName;
        /** Log messages whilst importing? */
        private boolean quiet = false;
        /** Verbose logging */
        private boolean verbose = false;
        

        /**
         * Validate the Import Context i.e. ensure all required information has been provided and is correct
         */
        private void validate()
        {
            if (storeRef == null)
            {
                throw new ImportException("Store to import into has not been specified.");
            }
            if (packageName == null)
            {
                throw new ImportException("Package name has not been specified.");
            }
            if (sourceDir != null)
            {
                File fileSourceDir = new File(sourceDir);
                if (fileSourceDir.exists() == false)
                {
                    throw new ImportException("Source directory " + fileSourceDir.getAbsolutePath() + " does not exist.");
                }
            }
            File packageFile = getPackageFile();
            if (packageFile.exists() == false)
            {
                throw new ImportException("Package file " + packageFile.getAbsolutePath() + " does not exist.");
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
     * Import Tool Exception
     * 
     * @author David Caruana
     */
    private static class ImportException extends RuntimeException
    {
        private static final long serialVersionUID = 3257008761007847733L;

        private ImportException(String msg)
        {
            super(msg);
        }

        private ImportException(String msg, Throwable cause)
        {
            super(msg, cause);
        }
    }

}
